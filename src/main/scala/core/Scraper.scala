package core

import java.net.UnknownHostException

import akka.actor.{Actor, ActorRef}
import org.jsoup.Jsoup

import scala.collection.JavaConverters

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
abstract class Scraper(analyser: ActorRef) extends Actor with Scrapy {
  //TODO handle network errors and timeouts
  override def receive: Receive = {
    case ProcessArticles(url) =>
      try {
        val response = Jsoup.connect(url).userAgent("Hookah").get()
        val links = response.select(linkSelector)

        JavaConverters.asScalaBuffer(links).toList
          .map(_.attr("href"))
          .map(extractContent)
          .foreach(analyser ! Analyse(_))
      }
      catch {
        case e: UnknownHostException =>
          e.printStackTrace()
      }
  }
}

trait Scrapy {
  def linkSelector: String

  def extractContent(url: String) : String
}