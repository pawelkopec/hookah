import akka.actor.{ActorSystem, Props}
import app.HuffingtonScraper
import core.{ArticleAnalyser, Start, Supervisor}

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
object Main extends App {
  val system = ActorSystem()
  val analyser = system.actorOf(Props(new ArticleAnalyser))
  val supervisor = system.actorOf(Props(new Supervisor(system)))

  val sites = List(
    (system.actorOf(Props(new HuffingtonScraper(analyser))), "http://www.huffingtonpost.com")
  )

  supervisor ! Start(sites)
}