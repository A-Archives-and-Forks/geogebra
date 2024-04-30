plugins {
    java
    id("org.docstr.gwt")
}

val webjarsPath: Provider<RegularFile> = layout.buildDirectory.file("generated/sources/webjars/java/main/")

dependencies {
    gwt(files(webjarsPath))
}

tasks.register("extractJs") {
    doLast {
        configurations.runtimeClasspath.get().forEach { jarFile ->
            if (jarFile.path.toString().contains("webjars.npm")) {
                val normalizedName = jarFile.name.replace("-[0-9].*".toRegex(), "").replace("-", "_")
                copy {
                    from(zipTree(jarFile).matching { include("**/*.js") })
                    into(webjarsPath)
                    eachFile {
                        val shortPath = path.split("/").drop(5).joinToString("/")
                        path = "$normalizedName/$shortPath"
                    }
                }
            }
        }
    }
}

tasks.compileJava {
    dependsOn("extractJs")
}
