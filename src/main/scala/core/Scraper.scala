package core

import java.net.UnknownHostException
import java.util.logging.{Level, Logger}

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.gargoylesoftware.htmlunit.html.{DomNode, HtmlAnchor}
import com.gargoylesoftware.htmlunit.{BrowserVersion, SgmlPage, WebClient}
import org.apache.commons.logging.LogFactory

import scala.collection.JavaConverters

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
abstract class Scraper(analyser: ActorRef) extends Actor with Scrape with ActorLogging {

  LogFactory.getFactory.setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
  Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
  Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF)

  var maxArticlesPerSite = 6

  override def receive: Receive = {
    case ExtractArticles() =>
      val startingPage = getPage(startUrl, 3000)
      if (startingPage != null)
        extractLinks(startingPage)
          .take(maxArticlesPerSite)
          .par.map(getPage(_, 3000))
          .seq.map(extractArticle)
          .foreach(analyser ! Analyse(_))
  }

  //TODO handle network errors and timeouts
  def getPage(url: String, scriptWaitingTime: Long = 0): SgmlPage = {
    try {
      val client = getClient

      log.info("Requesting " + url)
      val htmlPage: SgmlPage = client.getPage(url)
      log.info("Received " + url)

      log.info("Waiting for Javascript on " + url)
      client.waitForBackgroundJavaScript(scriptWaitingTime)
      log.info("Javascript loaded on " + url)
      htmlPage
    }
    catch {
      case e: UnknownHostException =>
        e.printStackTrace()
        null
    }
  }

  def getClient: WebClient = {
    val client = new WebClient(BrowserVersion.FIREFOX_52)
    val options = client.getOptions
    options.setThrowExceptionOnScriptError(false)
    options.setThrowExceptionOnFailingStatusCode(false)
    options.setCssEnabled(false)
    options.setTimeout(5000)
    client.getCache.setMaxSize(0)
    client
  }

  def extractLinks(page: SgmlPage): List[String] = {
    log.info("Extracting links from " + startUrl)

    val links = page.querySelectorAll(linkSelector)
    if (links.getLength == 0)
      log.error("No links found on " + page.getUrl)

    JavaConverters.asScalaBuffer(links)
      .toList
      .filter(x => x.isInstanceOf[HtmlAnchor])
      .map(x => x.asInstanceOf[HtmlAnchor])
      .map(_.getHrefAttribute)
      .map(normalizeUrl)
      .distinct
  }

  def extractArticle(page: SgmlPage): Article = {
    log.info("Extracting article from " + page.getUrl)

    val title: DomNode = page.querySelector(titleSelector)
    val content: DomNode = page.querySelector(contentSelector)

    if (title == null)
      log.error("No article title found on " + page.getUrl)
    if (content == null)
      log.error("No article content found on " + page.getUrl)

    new Article(
      if (title != null) title.asText() else "",
      if (content != null) content.asText() else ""
    )
  }

  def normalizeUrl(url: String): String =
    if (url.startsWith("/")) this.startUrl + url else url
}

trait Scrape {

  def startUrl: String

  def linkSelector: String

  def titleSelector: String

  def contentSelector: String
}