import akka.actor.{ActorSystem, Props}
import app.HuffingtonScraper
import core.{ArticleAnalyser, ExtractArticles}

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
object Hookah extends App {
  val system = ActorSystem()
  val analyser = system.actorOf(Props(new ArticleAnalyser))

  val scrapers = List(
    system.actorOf(Props(new HuffingtonScraper(analyser)))
  )

  scrapers.foreach(_ ! ExtractArticles())
}