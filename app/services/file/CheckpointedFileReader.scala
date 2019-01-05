package services.file

import java.io.{File, RandomAccessFile}

import javax.inject.{Inject, Singleton}
import play.api.Configuration

/**
  * Makes checkpoints throughout a file in order to efficiently find lines.
  *
  * Able to handle files of arbitrary size, but not very efficient for small ones.
  *
  * @param configuration Play configuration object
  */
@Singleton
class CheckpointedFileReader @Inject()(configuration: Configuration) extends FileReader {
  private val numCheckpoints = configuration.get[Int]("numCheckpoints")
  private val fileName = configuration.get[String]("fileName")

  private var maxLineIndex: Long = 0
  private val checkpoints: Array[(Long, Long)] = Array.ofDim(numCheckpoints)

  initCheckpoints()

  /**
    * Open file and retrieve desired line.
    *
    * @param lineIndex index of the desired line (starts at 0)
    * @return file line a the given index, or `None`, if index out of bounds
    */
  override def getLine(lineIndex: Int): Option[String] = {
    if (lineIndex > maxLineIndex) {
      return None
    }
    val randomAccessFile = new RandomAccessFile(fileName, "r")

    // Find all of the checkpoints that are smaller than the desired line index.
    // Then get the biggest of those.
    val checkpoint: (Long, Long) = checkpoints.
      iterator.
      takeWhile(_._1 <= lineIndex).
      reduce((_, x) => x)

    val (checkpointLineIndex, checkpointSizeIndex) = checkpoint

    // Navigate to the checkpoint
    randomAccessFile.seek(checkpointSizeIndex)

    // Read forward through the file
    val linesToBurn = lineIndex - checkpointLineIndex
    1L to linesToBurn foreach(_ => randomAccessFile.readLine())

    // Get the line we're looking for
    val desiredLine = randomAccessFile.readLine()
    randomAccessFile.close()

    Some(desiredLine)
  }

  /**
    * Read through file, recording checkpoints.
    */
  private def initCheckpoints(): Unit = {
    val file = new File(fileName)
    val fileSize = file.length()
    val targetChunkSize = fileSize / numCheckpoints

    val fileIter = scala.io.Source.fromFile(file).getLines()
    var curLineIndex = 0
    var curSize = 0

    for (i <- 0 until numCheckpoints) {
      val targetSize = i*targetChunkSize

      while (curSize < targetSize) {
        val curLine = fileIter.next()
        curLineIndex += 1

        // Add 1 to account for the newline, which `getLines()` removes
        curSize += curLine.length + 1
      }

      checkpoints(i) = (curLineIndex, curSize)
    }

    // Figure out how many lines are left in the file
    val remainingLines = fileIter.size
    // Determine the total number of lines in the file
    maxLineIndex = checkpoints(checkpoints.length-1)._1 + remainingLines - 1
  }
}
