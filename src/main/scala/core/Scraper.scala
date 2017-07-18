package core

import java.net.UnknownHostException

import akka.actor.{Actor, ActorRef}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConverters

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
abstract class Scraper(analyser: ActorRef) extends Actor with Scrape {

  override def receive: Receive = {
    case ExtractArticles() =>
      try {
        val response = connect(url)
        val links = response.select(linkSelector)

        JavaConverters.asScalaBuffer(links).toList
          .map(_.attr("href"))
          .map(connect)
          .map(extractArticle)
          .foreach(analyser ! Analyse(_))
      }
      catch {
        case e: UnknownHostException =>
          e.printStackTrace()
      }
  }

  //TODO handle network errors and timeouts
  def connect(url: String) : Document =
    Jsoup.connect(url).userAgent("Hookah").get()

  //TODO exception handling
  def extractArticle(document: Document) : Article =
    new Article(
      document.select(titleSelector).first().text(),
      document.select(contentSelector).text()
    )
}

trait Scrape {

  def url: String

  def linkSelector: String

  def titleSelector: String

  def contentSelector: String

  def extractArticle(document: Document) : Article
}