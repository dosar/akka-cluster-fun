import Dependencies._

lazy val basicSettings = Seq(
  organization := "ru.dosar",
  description := "config service poc",
  startYear := Some(2017),
  shellPrompt := { s => s"${Project.extract(s).currentProject.id} > " },
  version := "1.0",
  scalaVersion := "2.12.2",
  logLevel := Level.Info
)

lazy val root = (Project("akka-distributed-config-service-root", file("."))
  settings (moduleName := "akka-distributed-config-service-root")
  settings basicSettings
  aggregate (configService, clusterManagement))

lazy val configService = (Project("akka-distributed-config-service", file("service"))
  settings (moduleName := "akka-distributed-config-service")
  settings basicSettings
  settings (libraryDependencies ++= Seq(akkaCluster)))

lazy val clusterManagement = (Project("config-service-cluster-management", file("cluster-management"))
  settings (moduleName := "config-service-cluster-management")
  settings basicSettings
  settings (libraryDependencies ++= Seq(akkaCluster, akkaClusterManagement)))