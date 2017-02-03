package com.paratha

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

object Akka_Schedule extends App {

val system=ActorSystem("system")
system.scheduler.schedule(0 seconds, 5 seconds)(func(System.currentTimeMillis(),(time:Long)=>time+1))

  def func(time:Long,f:Long=>Long)=println(f(time))
}
