package core

import akka.actor.Actor

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
class ArticleAnalyser extends Actor {
  
  override def receive: Receive =  {
    //TODO
    case Analyse(hyperText) =>
      println(hyperText)
  }
}
