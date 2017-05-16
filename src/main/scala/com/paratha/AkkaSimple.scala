package com.paratha

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by sandeep on 15/05/2017.
  */
object AkkaSimple extends App {

  val system=ActorSystem("system")
  val myActor=system.actorOf(Props[MyActor],"myActor")
  myActor!"its"
  myActor! 2
  myActor!"fdsf"
}

class MyActor extends Actor{
  override def receive: Receive = {
    case _:String => println("its string")
    case _:Int => println("its number")
    case _ => println("something else")
  }
}
