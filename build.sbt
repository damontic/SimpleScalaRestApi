lazy val root = (project in file(".")).
  settings(
    name := "SimpleScalaRestApi",
    version      in ThisBuild := sys.env.get("APP_VER").getOrElse("0.0.0"),
    scalaVersion := "2.12.7",
    test in assembly := {}
  )

resolvers ++= Seq(
    Resolver.DefaultMavenRepository,
    Resolver.bintrayRepo("lonelyplanet", "maven")
)
 
libraryDependencies ++= {

    val akkaVersion = "2.5.19"
    val akkaHttpVersion = "10.1.7"
    val akkaStreamVersion = "2.5.19"
    val prometheusAkkaHttpVersion = "0.4.0"
    val scalaTestVersion = "3.0.5"
    val consulApiVersion = "1.4.2"

    Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion,
        "com.lonelyplanet" %% "prometheus-akka-http" % prometheusAkkaHttpVersion,
        "com.ecwid.consul" % "consul-api" % consulApiVersion,
        "org.scalatest" % "scalatest_2.12" % scalaTestVersion % "test"
    )
}

mainClass in (Compile, run) := Some("co.s4n.main.Main")

assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${(version in ThisBuild).value}.jar"

logBuffered in Test := false
