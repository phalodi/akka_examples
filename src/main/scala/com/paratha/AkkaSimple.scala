package com.paratha

import akka.actor.{Actor, ActorLogging, ActorSystem, DeadLetter, PoisonPill, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by sandeep on 15/05/2017.
  */
object AkkaSimple extends App {

  val system = ActorSystem("system")
  val log = Logging(system, classOf[MyActor])
  log.debug("startttttttttt")
  val myActor = system.actorOf(Props[MyActor], "myActor")
  val askActor = system.actorOf(Props[AskActor], "askActor")
  val deadLetterMonitorActor =
    system.actorOf(Props[DeadLetterMonitorActor],
      name = "deadlettermonitoractor")
  system.eventStream.subscribe(
    deadLetterMonitorActor, classOf[DeadLetter])

  implicit val timeout = Timeout(1 seconds)

  myActor ! "its"
  myActor ! 2
  myActor ! "fdsf"
  val reply = (askActor ? "i have query").mapTo[String]
  reply onComplete {
    case Success(n) => println(n)
    case Failure(e) => println("Failure")
  }

  myActor!PoisonPill
  Thread.sleep(1000)
  myActor ! 5
}

class MyActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case _: String =>
      log.debug("get string message")
      println("its string")
    case _: Int => println("its number")
    case _ => println("something else")
  }
}

class AskActor extends Actor {
  override def receive: Receive = {
    case _: String => sender() ! "hello i reply u back"
    case _ => sender() ! "i only understand strings"
  }
}


class DeadLetterMonitorActor
  extends Actor
    with akka.actor.ActorLogging {
  log.info("DeadLetterMonitorActor: constructor")

  def receive = {
    case d: DeadLetter => {
      log.error(s"DeadLetterMonitorActor : saw dead letter $d")
    }
    case _ => log.info("DeadLetterMonitorActor : got a message")
  }
}
