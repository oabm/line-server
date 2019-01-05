package services.file

import javax.inject.{Inject, Singleton}
import play.api.Configuration


/**
  * Test implementation. Opens file from beginning each time.
  *
  * Super inefficient!
  *
  * @param configuration Play configuration object
  */
@Singleton
class PloddingFileReader @Inject() (configuration: Configuration) extends FileReader {
  private val fileName = configuration.get[String]("fileName")

  override def getLine(lineIndex: Int): Option[String] = {
    val lineIterator = scala.io.Source.fromFile(fileName).getLines()
    lineIterator.slice(lineIndex, lineIndex + 1).toList.headOption
  }
}
