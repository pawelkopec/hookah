package app

import akka.actor.ActorRef
import core.Scraper

/**
  * Created by Paweł Kopeć on 7/10/17.
  */
class HuffingtonScraper(analyser: ActorRef) extends Scraper(analyser) {

  def startUrl =
    "http://www.huffingtonpost.com"

  def linkSelector: String =
    "div[data-card-type=top_stories] > div.card__content > a"

  def titleSelector: String =
    "header.entry__header > div.headline > h1.headline__title"

  def contentSelector: String =
    "div.entry__content > div.entry__body > div.entry__text"
}
