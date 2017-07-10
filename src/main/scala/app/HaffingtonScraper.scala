package app

import akka.actor.ActorRef
import core.Scraper

/**
  * Created by Paweł Kopeć on 7/10/17.
  */
class HaffingtonScraper(analyser: ActorRef) extends Scraper(analyser) {

  def linkSelector: String =
    "div[data-card-type=top_stories] > div.card__content > a"

  //TODO
  override def extractContent(url: String): String =
    "<html>" + url + "</html>"
}