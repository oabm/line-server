package services.file

import javax.inject.Singleton

/**
  * Test implementation. Doesn't actually read from a file.
  */
@Singleton
class TestFileReader extends FileReader {
  override def getLine(lineIndex: Int): Option[String] = Some(s"Test string #$lineIndex")
}
