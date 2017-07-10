package app

import akka.actor.ActorRef
import core.Scraper
import org.jsoup.nodes.Document

/**
  * Created by Paweł Kopeć on 7/10/17.
  */
class HuffingtonScraper(analyser: ActorRef) extends Scraper(analyser) {
  //TODO
  def linkSelector: String =
    "div[data-card-type=top_stories] > div.card__content > a"


  //TODO
  def extractContent(url: String) : String =
    url
}
