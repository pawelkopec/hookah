package core

import akka.actor.{Actor, ActorRef}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
abstract class Scraper(analyser: ActorRef) extends Actor with Scrapy {
  //TODO handle network errors and timeouts
  override def receive: Receive = {
    case ProcessArticles(url) =>
      val response = Jsoup.connect(url).get()
      findArticles(response)
        .map(extractContent)
        .foreach(analyser ! Analyse(_))
  }
}

trait Scrapy {
  def findArticles(document: Document) : List[String]

  def extractContent(url: String) : String
}