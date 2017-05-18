package com.paratha

import akka.actor._
import akka.routing._

import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

object AkkaPoolRouting extends App {

  //==============================================================
  // Use built Pool router(s) which will do the supervision for us
  //
  //
  //    Comment/Uncomment to try the different router logic
  //
  //==============================================================
  RunScatterGatherFirstCompletedPoolDemo()
  //RunTailChoppingPoolDemo()


  def RunScatterGatherFirstCompletedPoolDemo(): Unit = {

    val supervisionStrategy = OneForOneStrategy() {
      case e => SupervisorStrategy.restart
    }

    val props = ScatterGatherFirstCompletedPool(
      5, supervisorStrategy = supervisionStrategy, within = 10.seconds).
      props(Props[FibonacciActor])

    RunPoolDemo(props)
  }

  def RunTailChoppingPoolDemo(): Unit = {

    val supervisionStrategy = OneForOneStrategy() {
      case e => SupervisorStrategy.restart
    }

    val props = TailChoppingPool(5, within = 10.seconds,
      supervisorStrategy = supervisionStrategy, interval = 20.millis).
      props(Props[FibonacciActor])

    RunPoolDemo(props)
  }

  def RunPoolDemo(props: Props): Unit = {
    val system = ActorSystem("RoutingSystem")
    val actorRef = system.actorOf(Props(
      new PoolRouterContainerActor(props, "theRouter")), name = "thePoolContainer")
    actorRef ! WorkMessage
    StdIn.readLine()
    system.terminate()
  }
}

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

class PoolRouterContainerActor(val props: Props, val name: String) extends Actor {

  val router: ActorRef = context.actorOf(props, name)

  def receive = {
    case WorkMessage =>
      implicit val timeout = Timeout(5 seconds)
      val futureResult = router ? FibonacciNumber(10)
      val (actName, result) = Await.result(futureResult, timeout.duration)

      println(s"FibonacciActor : ($actName) came back with result -> $result")
  }
}

import akka.actor.Actor

import scala.annotation.tailrec

class FibonacciActor extends Actor {

  val actName = self.path.name

  def receive = {
    case FibonacciNumber(nbr) => {
      println(s"FibonacciActor : ($actName) ->  " +
        s"has been asked to calculate FibonacciNumber")
      val result = fibonacci(nbr)
      sender ! (actName, result)
    }
  }

  private def fibonacci(n: Int): Int = {
    @tailrec
    def fib(n: Int, b: Int, a: Int): Int = n match {
      case 0 => a
      case _ => fib(n - 1, a + b, b)
    }

    fib(n, 1, 0)
  }
}

case class FibonacciNumber(nbr: Int)