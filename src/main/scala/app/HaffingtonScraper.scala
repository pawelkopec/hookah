package app

import akka.actor.ActorRef
import core.Scraper
import org.jsoup.nodes.Document

/**
  * Created by Paweł Kopeć on 7/10/17.
  */
class HaffingtonScraper(analyser: ActorRef) extends Scraper(analyser) {
  //TODO
  override def findArticles(document: Document) : List[String] = {
    println("Otrzymałem " + document.body())
    List("a", "b", "c")
  }

  //TODO
  override def extractContent(url: String) : String =
    "tekst prezykladowy"
}
