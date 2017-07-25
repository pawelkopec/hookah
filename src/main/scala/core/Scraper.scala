package core

import java.net.UnknownHostException
import java.util.logging.{Level, Logger}

import akka.actor.{Actor, ActorRef}
import com.gargoylesoftware.htmlunit.html.{HtmlAnchor, HtmlElement, HtmlPage}
import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}

import scala.collection.JavaConverters
/**
  * Created by Paweł Kopeć on 7/9/17.
  */
abstract class Scraper(analyser: ActorRef) extends Actor with Scrape {

  val client = new WebClient(BrowserVersion.CHROME)
  client.getOptions.setThrowExceptionOnScriptError(false)
  client.getOptions.setThrowExceptionOnFailingStatusCode(false)
  Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF)

  override def receive: Receive = {
    case ExtractArticles() =>
      try {
        val links = connect(url, 5000).querySelectorAll(linkSelector)

        JavaConverters.asScalaBuffer(links)
          .toList.par
          .map(x => x.asInstanceOf[HtmlAnchor])
          .map(_.getHrefAttribute)
          .map(normalize)
          .map(connect(_))
          .map(extractArticle)
          .foreach(analyser ! Analyse(_))
      }
      catch {
        case e: UnknownHostException =>
          e.printStackTrace()
      }
  }

  def normalize(url: String) : String =
    if (url.startsWith("/")) this.url + url else url

  //TODO handle network errors and timeouts
  def connect(url: String, scriptWaitingTime: Long = 0) : HtmlPage = {
    val html: HtmlPage = client.getPage(url)
    client.waitForBackgroundJavaScript(scriptWaitingTime)
    html
  }

  //TODO exception handling
  def extractArticle(document: HtmlPage) : Article =
    new Article(
      document
        .querySelector(titleSelector)
        .asInstanceOf[HtmlElement]
        .asText(),
      document
        .querySelector(contentSelector)
        .asInstanceOf[HtmlElement]
        .asText()
    )
}

trait Scrape {

  def url: String

  def linkSelector: String

  def titleSelector: String

  def contentSelector: String

  def extractArticle(document: HtmlPage) : Article
}