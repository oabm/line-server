package services.file

trait FileReader {
  /**
    * Get a line from the file for the given index
    *
    * @param lineIndex index of the desired line (starts at 0)
    * @return file line a the given index, or `None`, if index out of bounds
    */
  def getLine(lineIndex: Int): Option[String]
}
