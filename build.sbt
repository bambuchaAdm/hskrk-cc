name := "hskrk-cc"

version := "0.0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.9",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.9",
  "com.typesafe.akka" %% "akka-http-testkit" % "2.4.9",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.9",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

enablePlugins(SbtTwirl)

sourceDirectories in (Compile, TwirlKeys.compileTemplates) += sourceDirectory.value / "main" / "views"

target in (Compile, TwirlKeys.compileTemplates) := target.value / "scala-2.11" / "src_managed" / "main"

TwirlKeys.templateImports ++= Seq(
  "pl.hskrk.cc.assets._"
)

mainClass in assembly := Option("pl.hskrk.cc.Server")

val mode = SettingKey[String]("mode", "Define mode of working. Production, test or development")

mode := "dev"

val webpack = TaskKey[Seq[sbt.File]]("webpack", "Compile web scripts to all stuff")

webpack in Compile := {
  "npm run webpack".!
  IO.listFiles(target.value / "public")
}

resourceGenerators in Compile += (webpack in Compile).taskValue

fork in run := true

connectInput in run := true