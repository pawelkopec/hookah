package core

import akka.actor.ActorRef

/**
  * Created by Paweł Kopeć on 7/9/17.
  */

case class Start(sites: List[(ActorRef, String)])
case class Timeout()
case class ProcessArticles(url: String)
case class Analyse(profile: String)