lazy val root = (project in file(".")).
  settings(
    name := "SimpleScalaRestApi",
    version := "1.0",
    scalaVersion := "2.12.7"
  )

resolvers ++= Seq(
    Resolver.DefaultMavenRepository,
    Resolver.bintrayRepo("lonelyplanet", "maven")
)
 
libraryDependencies ++= {

    val akkaVersion = "2.5.18"
    val akkaHttpVersion = "10.1.5"
    val akkaStreamVersion = "2.5.18"
    val prometheusAkkaHttpVersion = "0.3.3"

    Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion,
        "com.lonelyplanet" %% "prometheus-akka-http" % prometheusAkkaHttpVersion
    )
}

mainClass in (Compile, run) := Some("co.s4n.main.Main")

assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${(version in ThisBuild).value}.jar"
