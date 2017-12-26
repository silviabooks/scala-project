name := "BoxOffice"

version := "0.1"

scalaVersion := "2.11.6"

resolvers += "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "releases"  at "https://oss.sonatype.org/content/groups/scala-tools"

scalaSource in Compile := baseDirectory( _ / "src" ).value

mainClass in Compile := Some("org.unict.ing.advlanguages.boxoffice.Main")
libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % "2.11.6",
    "org.scala-lang" % "scala-reflect" % "2.11.6",
    "com.typesafe.akka" %% "akka-http" % "10.0.3",
    "com.typesafe.akka" %% "akka-http-spray-json"  % "3.0.0-RC1",
    "io.spray" %% "spray-json" % "1.3.0",
    "org.mongodb" % "bson" % "3.6.0",
    "org.slf4j" % "slf4j-api" % "1.7.7",
    "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.0"
)
