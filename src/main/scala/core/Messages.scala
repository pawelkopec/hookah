package core

import akka.actor.ActorRef

/**
  * Created by Paweł Kopeć on 7/9/17.
  */

case class Start(scrapers: List[ActorRef])
case class Timeout()
case class ExtractArticles()
case class Analyse(article: Article)