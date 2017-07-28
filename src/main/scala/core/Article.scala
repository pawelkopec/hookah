package core

/**
  * Created by Paweł Kopeć on 7/18/17.
  */
class Article(val title: String, val content: String) {
  override def toString: String =
    "\"" + title.substring(0, Math.min(20, title.length)) + "...\": " +
      content.substring(0, Math.min(20, content.length)) + "..."
}
