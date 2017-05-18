package com.paratha


import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, Props, Terminated}
import akka.routing._

case object WorkMessage

case object Report

class RouterActor(val routingLogic: RoutingLogic) extends Actor {

  val counter: AtomicInteger = new AtomicInteger()

  val routees = Vector.fill(5) {
    val workerCount = counter.getAndIncrement()
    val r = context.actorOf(Props(
      new WorkerActor(workerCount)), name = s"workerActor-$workerCount")
    context watch r
    ActorRefRoutee(r)
  }

  //create a Router based on the incoming class field
  //RoutingLogic which will really determine what type of router
  //we end up with
  var router = Router(routingLogic, routees)

  def receive = {
    case WorkMessage =>
      router.route(WorkMessage, sender())
    case Report => routees.foreach(ref => ref.send(Report, sender()))
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val workerCount = counter.getAndIncrement()
      val r = context.actorOf(Props(
        new WorkerActor(workerCount)), name = s"workerActor-$workerCount")
      context watch r
      router = router.addRoutee(r)
  }
}

import akka.actor._

import scala.io.StdIn
import scala.language.postfixOps

object AkkaRouting extends App {

  //==============================================================
  //Standard Actor that does routing using Router class
  //where we apply relevant RoutingLogic
  //Supervision is done manually within the Actor that hosts
  //the Router, where we monitor the routees and remove /recreate
  //them on 'Terminated'
  //==============================================================
  RunRoutingDemo(RandomRoutingLogic())


  def RunRoutingDemo(routingLogic: RoutingLogic): Unit = {
    val system = ActorSystem("RoutingSystem")
    val actorRef = system.actorOf(Props(
      new RouterActor(routingLogic)), name = "theRouter")

    for (i <- 0 until 10) {
      actorRef ! WorkMessage
      Thread.sleep(1000)
    }
    actorRef ! Report

    StdIn.readLine()
    system.terminate()
  }
}

import akka.actor.Actor

class WorkerActor(val id: Int) extends Actor {

  var msgCount = 0
  val actName = self.path.name

  def receive = {
    case WorkMessage => {
      msgCount += 1
      println(s"worker : {$id}, name : ($actName) ->  ($msgCount)")
    }
    case Report => {
      println(s"worker : {$id}, name : ($actName) ->  saw total messages : ($msgCount)")
    }
    case _ => println("unknown message")
  }
}