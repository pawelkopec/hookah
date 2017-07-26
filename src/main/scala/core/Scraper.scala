package core

import java.net.UnknownHostException
import java.util.logging.{Level, Logger}

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.gargoylesoftware.htmlunit.html.{DomNode, HtmlAnchor, HtmlPage}
import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}
import org.apache.commons.logging.LogFactory

import scala.collection.JavaConverters

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
abstract class Scraper(analyser: ActorRef) extends Actor with Scrape with ActorLogging {

  LogFactory.getFactory.setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
  Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
  Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF)

  override def receive: Receive = {
    case ExtractArticles() =>
      val htmlPage = getPage(url, 5000)
      if (htmlPage != null)
        extractLinks(htmlPage)
          .par
          .map(getPage(_, 3000))
          .map(extractArticle)
          .foreach(analyser ! Analyse(_))
  }

  //TODO handle network errors and timeouts
  def getPage(url: String, scriptWaitingTime: Long = 0): HtmlPage = {
    try {
      val client = getClient

      log.info("Connecting to " + url)
      val htmlPage: HtmlPage = client.getPage(url)

      log.info("Waiting for Javascript in " + url)
      client.waitForBackgroundJavaScript(scriptWaitingTime)
      htmlPage
    }
    catch {
      case e: UnknownHostException =>
        e.printStackTrace()
        null
    }
  }

  def getClient: WebClient = {
    val client = new WebClient(BrowserVersion.CHROME)
    client.setJavaScriptTimeout(500)
    client.getOptions.setThrowExceptionOnScriptError(false)
    client.getOptions.setThrowExceptionOnFailingStatusCode(false)
    client
  }

  def extractLinks(htmlPage: HtmlPage): List[String] = {
    log.info("Extracting links from " + url)

    val links = htmlPage.querySelectorAll(linkSelector)
    JavaConverters.asScalaBuffer(links)
      .toList
      .filter(x => x.isInstanceOf[HtmlAnchor])
      .map(x => x.asInstanceOf[HtmlAnchor])
      .map(_.getHrefAttribute)
      .map(normalizeUrl)
      .distinct
  }

  def extractArticle(htmlPage: HtmlPage): Article = {
    log.info("Extracting article from " + htmlPage.getBaseURL)

    val title: DomNode = htmlPage.querySelector(titleSelector)
    val content: DomNode = htmlPage.querySelector(contentSelector)

    new Article(
      if (title != null) title.asText() else "",
      if (content != null) content.asText() else ""
    )
  }

  def normalizeUrl(url: String): String =
    if (url.startsWith("/")) this.url + url else url
}

trait Scrape {

  def url: String

  def linkSelector: String

  def titleSelector: String

  def contentSelector: String

  def extractArticle(htmlPage: HtmlPage): Article
}