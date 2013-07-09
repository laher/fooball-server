//
// Socko Bootstrap HelloApp build file
//

import sbt._
import Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import sbt.Project.Initialize
import sbtassembly.Plugin._
import AssemblyKeys._

//
// Build setup
//
object SockoBuild extends Build {

  //
  // Settings
  //
  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    // Info
    organization := "nz.net.laher.fooball",
    version      := "0.1.0",
    scalaVersion := "2.10.1",
    organizationHomepage := Some(url("http://laher.net.nz")),

    // Repositories
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Maven Repository" at "http://repo1.maven.org/maven2/",

    // sbtEclipse - see examples https://github.com/typesafehub/sbteclipse/blob/master/sbteclipse-plugin/src/sbt-test/sbteclipse/02-contents/project/Build.scala
    EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.Unmanaged, EclipseCreateSrc.Source, EclipseCreateSrc.Resource),
    EclipseKeys.withSource := true
  )

  // Compile settings
  lazy val compileJdk6Settings = Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-optimize", "-feature", "-target:jvm-1.6"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.6", "-target", "1.6")
  )
  lazy val compileJdk7Settings = Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-optimize", "-feature", "-target:jvm-1.7"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")
  )

  //
  // Projects
  //
  lazy val fooballServer = Project(id = "fooball-server",
                         base = file("."),
                         settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                           libraryDependencies ++= Seq(Dependency.socko, Dependency.json4sNative, Dependency.scalatest, Dependency.junit)
                         ))

}

object Dependency {
  val socko         = "org.mashupbots.socko" %% "socko-webserver" % "0.3.0"
  val json4sNative = "org.json4s" %% "json4s-native" % "3.2.4"
  val scalatest     = "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
  val junit = "junit" % "junit" % "4.7" % "test"
}




