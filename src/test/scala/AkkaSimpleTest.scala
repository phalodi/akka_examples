import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.paratha.{AskActor}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * Created by sandeep on 17/05/2017.
  */

class AskActorTests
  extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll
    with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A MyActor using implicit sender " must {
    "reply back" in {
      val helloActor = system.actorOf(Props[AskActor], name = "myActor")
      helloActor ! "hello"
      expectMsg("hello i reply u back")
    }
  }
}