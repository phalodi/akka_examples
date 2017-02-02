package com.paratha

import akka.actor.{Actor, ActorSystem, Props}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

case class Order(t1: Int, l1: Int)

case class UrgentOrder(t1: Int, l1: Int)


object ParathaApp extends App {

  val system = ActorSystem("ParathaSystem",ConfigFactory.load)
  val parathaServe = system.actorOf(Props[ParathaActor].withDispatcher("prio-dispatcher"))
  parathaServe ! Order(6, 2)
  parathaServe ! UrgentOrder(5, 1)
  parathaServe ! Order(5, 6)
  parathaServe ! UrgentOrder(1, 5)
  parathaServe ! Order(6, 1)
  parathaServe ! UrgentOrder(1, 5)
  parathaServe ! Order(6, 1)
  parathaServe ! Order(6, 1)
  parathaServe ! UrgentOrder(1, 5)
  parathaServe ! Order(1, 54)

}


class ParathaActor extends Actor {
  val avg = 0
  var sum = 0

  def receive = {
    case x: Order => println("Normal order" + x)
    case u: UrgentOrder => println("Urgent Orders" + u)
    case _ => println("Customer not there")
  }

}

class MyPriorityActorMailbox(settings: ActorSystem.Settings, config: Config)
  extends UnboundedPriorityMailbox(
    // Create a new PriorityGenerator, lower prio means more important
    PriorityGenerator {
      case u: UrgentOrder => 0
      case x: Order => 1
      case _ => 2
    })

