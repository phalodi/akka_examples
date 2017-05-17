name := "akka_priority"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor_2.11" % "2.4.12",
	"com.typesafe.akka" % "akka-http_2.11" % "3.0.0-RC1",
	"com.typesafe.akka" % "akka-http-core_2.11" % "3.0.0-RC1",
	"com.typesafe.akka" % "akka-http-spray-json_2.11" % "3.0.0-RC1",
	"com.typesafe.akka" %% "akka-slf4j" % "2.4.12",
	"ch.qos.logback" % "logback-classic" % "1.1.7"
)

