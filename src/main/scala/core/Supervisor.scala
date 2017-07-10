package core

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
class Supervisor(system: ActorSystem) extends Actor {
  override def receive: Receive = {
    case Start(sites) =>
      sites.foreach(x => x._1 ! ProcessArticles(x._2))
  }
}
