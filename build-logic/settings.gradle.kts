dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url="https://oss.sonatype.org/content/repositories/comgooglejsinterop-1038")
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":convention")
