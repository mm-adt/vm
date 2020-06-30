resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.mavenCentral
// addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
//dependencyOverrides += "org.asciidoctor" % "asciidoctorj" % "2.1.0"
//addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
addSbtPlugin("com.mdsol" % "sbt-asciidoctor" % "0.4")