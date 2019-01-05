package services.file

import javax.inject.{Inject, Singleton}
import play.api.Configuration

/**
  * Reads entire file into memory upon initialization.
  *
  * Doesn't work for file that won't fit into memory!
  *
  * @param configuration Play configuration object
  */
@Singleton
class GreedyFileReader @Inject() (configuration: Configuration) extends FileReader {
  private val fileName = configuration.get[String]("fileName")
  private val lines = scala.io.Source.fromFile(fileName).getLines().toArray

  override def getLine(lineIndex: Int): Option[String] = {
    if (lineIndex < lines.length) {
      Some(lines(lineIndex))
    } else {
      None
    }
  }
}
