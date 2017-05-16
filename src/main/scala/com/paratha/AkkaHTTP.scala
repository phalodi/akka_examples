package com.paratha

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

/**
  * Created by sandeep on 15/05/2017.
  */
object AkkaHTTP extends App {

  implicit val system=ActorSystem("system")
  implicit val materializer= ActorMaterializer()
  implicit val executionContext=system.dispatcher
  val (host, port) = ("localhost", 8080)
  val route= {
    get {
      path("insert") {
        complete {
          HttpResponse(StatusCodes.OK, entity = "<html><body>Hello world!</body></html>")
        }
      }
    }
  }
  val bindingFuture = Http().bindAndHandle(route, host, port)


  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
