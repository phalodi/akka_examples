package com.paratha

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.util.Success
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure

/**
  * Created by sandeep on 15/05/2017.
  */
object AkkaSimple extends App {

  val system = ActorSystem("system")
  val myActor = system.actorOf(Props[MyActor], "myActor")
  val askActor = system.actorOf(Props[AskActor], "askActor")
  implicit val timeout=Timeout(1 seconds)

  myActor ! "its"
  myActor ! 2
  myActor ! "fdsf"
  val reply = (askActor ? "i have query").mapTo[String]
  reply onComplete {
    case Success(n)=>println(n)
    case Failure(e)=>println("Failure")
  }
}

class MyActor extends Actor {
  override def receive: Receive = {
    case _: String => println("its string")
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
