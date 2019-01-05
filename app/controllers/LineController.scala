package controllers

import javax.inject._
import play.api.mvc._
import services.file.FileReader

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` that demonstrates how to write
  * simple asynchronous code in a controller. It uses a timer to
  * asynchronously delay sending a response for 1 second.
  *
  * @param cc standard controller components
  * @param exec We need an `ExecutionContext` to execute our
  * asynchronous code.  When rendering content, you should use Play's
  * default execution context, which is dependency injected.  If you are
  * using blocking operations, such as database or network access, then you should
  * use a different custom execution context that has a thread pool configured for
  * a blocking API.
  */
@Singleton
class LineController @Inject()(fileReader: FileReader, cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {
  def getLine(lineIndex: Int) = Action {
    lineIndex match {
      case x if x < 0 => BadRequest
      case _ => fileReader.getLine(lineIndex) match {
          case Some(line) => Ok(line)
          case None => EntityTooLarge
        }
    }
  }
}

