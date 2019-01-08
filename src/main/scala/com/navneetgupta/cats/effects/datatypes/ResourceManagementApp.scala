package com.navneetgupta.cats.effects.datatypes

import java.io._

import cats.effect.IO
import cats.syntax.all._

import scala.concurrent.ExecutionContext.Implicits.global

object ResourceManagementApp {
  // Safe Resource Acquisition and Release

  // In General we use try / finally to acquire and release the resources for a computation
  /**
    * try/finally have issues as below:
    *
    * 1.  this statement is obviously meant for side-effectful computations and can’t be used by FP abstractions
    * 2.  it’s only meant for synchronous execution, so we can’t use it when working with abstractions capable of asynchrony (e.g. IO, Task, Future)
    * 3.  finally executes irregardless of the exception type, indiscriminately, so if you get an out of memory error it still tries to close the file handle, unnecessarily delaying a process crash
    * 4.  if the body of try throws an exception, then followed by the body of finally also throwing an exception, then the exception of finally gets rethrown, hiding the original problem
    * */

  def readFirstLine(file: File): IO[String] =
    IO(new BufferedReader(new FileReader(file))).bracket { in =>
      // Usage (the try block)
      IO(in.readLine())
    } { in =>
      // Releasing the reader (the finally block)
      IO(in.close())
    }

  // Of special consideration is that bracket calls the release action on cancellation as well

  implicit val context = IO.contextShift(global)
  def readFile(file: File): IO[String] = {
    // Opens file with an asynchronous boundary before it,
    // ensuring that processing doesn't block the "current thread"
    val acquire = IO.shift *> IO(new BufferedReader(new FileReader(file)))

    acquire.bracket { in =>
      // Usage (the try block)
      IO {
        // Ugly, low-level Java code warning!
        val content = new StringBuilder()
        var line: String = null
        do {
          line = in.readLine()
          if (line != null) content.append(line)
        } while (line != null)
        content.toString()
      }
    } { in =>
      // Releasing the reader (the finally block)
      // This is problematic if the resulting `IO` can get
      // canceled, because it can lead to data corruption
      IO(in.close())
    }
  }

//  The bracketCase operation is the generalized bracket, also receiving an ExitCase in release in order to distinguish between:
//
//      successful completion
//      completion in error
//      cancellation

  import cats.effect.ExitCase.{Canceled, Completed, Error}

  def readLine(in: BufferedReader): IO[String] =
    IO.pure(in).bracketCase { in =>
      IO(in.readLine())
    } {
      case (_, Completed | Error(_)) =>
        // Do nothing
        IO.unit
      case (in, Canceled) =>
        IO(in.close())
    }
  // In this example we are only closing the passed resource in case cancellation occurred.
  // As to why we’re doing this — consider that the BufferedReader reference was given to us and usually the
  // producer of such a resource should also be in charge of releasing it. If this function would release
  // the given BufferedReader on a successful result, then this would be a flawed implementation.
}
