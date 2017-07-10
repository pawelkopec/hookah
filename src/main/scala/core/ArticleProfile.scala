package core

/**
  * Created by Paweł Kopeć on 7/9/17.
  */
case class ArticleProfile(ngrams: Map[String, Int], words: Map[String, Int]) {
  def similarity(other: ArticleProfile): Double = {
    //TODO
    0
  }
}
