import com.github.spotbugs.snom.SpotBugsTask
import org.geogebra.gradle.Resources

plugins {
    id("com.github.spotbugs")
}

spotbugs {
    ignoreFailures = System.getenv("CI") != null
    excludeFilter = resources.text.fromUri(Resources::class.java.getResource("/spotbugs.xml")!!.toURI()).asFile()
    jvmArgs = listOf("-Dfindbugs.sf.comment=true")
}

val spotbugsVersion: VersionConstraint = project.rootProject
    .extensions
    .getByType(VersionCatalogsExtension::class.java).named("libs")
        .findVersion("spotbugs").get()

dependencies {
    spotbugs("com.github.spotbugs:spotbugs:$spotbugsVersion")
}

tasks.withType<SpotBugsTask> {
    reports {
        create(if (System.getenv("CI") != null) "xml" else "html") {
            required = true
        }
    }
}
