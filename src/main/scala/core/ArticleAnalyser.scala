package core

import akka.actor.{Actor, ActorLogging}

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
class ArticleAnalyser extends Actor with ActorLogging {

  override def receive: Receive = {
    //TODO
    case Analyse(article) =>
      log.info("Found article \"" + article.title + "\": " +
        article.content.substring(0, Math.min(20, article.content.length)) + "...")
  }
}
