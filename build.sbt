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
  aggregate (configService, clusterManagement, clusterClient, serviceProtocol))

lazy val configService = (Project("akka-distributed-config-service", file("service"))
  settings (moduleName := "akka-distributed-config-service")
  settings basicSettings
  settings (libraryDependencies ++= Seq(akka, akkaCluster, akkaTools))
  dependsOn serviceProtocol)

lazy val clusterManagement = (Project("config-service-cluster-management", file("cluster-management"))
  settings (moduleName := "config-service-cluster-management")
  settings basicSettings
  settings (libraryDependencies ++= Seq(akka, akkaCluster, akkaClusterManagement)))

lazy val clusterClient = (Project("config-service-client", file("config-client"))
  settings (moduleName := "config-service-client")
  settings basicSettings
  settings (libraryDependencies ++= Seq(akka, akkaCluster, akkaTools))
  dependsOn serviceProtocol)

lazy val serviceProtocol = (Project("config-service-protocol", file("service-protocol"))
  settings (moduleName := "config-service-protocol")
  settings basicSettings
  settings (libraryDependencies ++= Seq(akkaTools, scalaTest)))
