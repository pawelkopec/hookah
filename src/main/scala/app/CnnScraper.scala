package app

import akka.actor.ActorRef
import core.Scraper

/**
  * Created by Paweł Kopeć on 7/18/17.
  */
class CnnScraper(analyser: ActorRef) extends Scraper(analyser) {

  def startUrl =
    "http://edition.cnn.com"

  def linkSelector: String =
    "ul.cn-list-hierarchical-xs > article.cd--article > " +
        "div.cd__wrapper > div.cd__content > h3.cd__headline > a"

  def titleSelector: String =
    "article div h1.pg-headline"

  def contentSelector: String =
    "article section#body-text"
}
