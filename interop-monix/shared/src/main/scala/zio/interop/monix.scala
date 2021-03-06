package scalaz.zio.interop

import monix.eval
import monix.execution.Scheduler
import scalaz.zio.{ IO, Task, UIO }

object monixio {
  implicit class IOObjOps(private val obj: IO.type) extends AnyVal {
    def fromTask[A](task: eval.Task[A])(implicit scheduler: Scheduler): Task[A] =
      Task.fromFuture(_ => task.runToFuture)

    def fromCoeval[A](coeval: eval.Coeval[A]): Task[A] =
      Task.fromTry(coeval.runTry())
  }

  implicit class IOThrowableOps[A](private val io: Task[A]) extends AnyVal {
    def toTask: UIO[eval.Task[A]] =
      io.fold(eval.Task.raiseError, eval.Task.now)

    def toCoeval: UIO[eval.Coeval[A]] =
      io.fold(eval.Coeval.raiseError, eval.Coeval.now)
  }
}
