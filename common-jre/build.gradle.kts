plugins {
    `java-test-fixtures`
    jacoco
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.sourcesets)
}

description = "Common parts of GeoGebra that depends on JRE support."

dependencies {
    api(project(":common"))
    api(libs.mozilla.rhino)

    implementation(project(":giac-jni"))
    implementation(project(":renderer-base"))
    implementation(project(":editor-base"))

    testImplementationIntegration(project(":ggbjdk"))
    testImplementationIntegration(libs.junit)
    testImplementationIntegration(libs.hamcrest)
    testImplementationIntegration(libs.mockito.core)

    // Junit 5 support with backward compatibility
    testImplementationIntegration(platform(libs.junit5.bom))
    testImplementationIntegration(libs.junit5.jupiter)
    testRuntimeOnlyIntegration(libs.junit5.vintage)
}

private fun DependencyHandler.testImplementationIntegration(dependencyNotation: Any) {
    testImplementation(dependencyNotation)
    testFixturesImplementation(dependencyNotation)
}

private fun DependencyHandler.testRuntimeOnlyIntegration(dependencyNotation: Any) {
    testRuntimeOnly(dependencyNotation)
    testFixturesRuntimeOnly(dependencyNotation)
}

tasks.test {
    ignoreFailures = true
    useJUnitPlatform {
        includeEngines("junit-jupiter", "junit-vintage")
    }
}

// http://stackoverflow.com/questions/20638039/gradle-and-jacoco-instrument-classes-from-a-separate-subproject
gradle.projectsEvaluated {
    // include src from all dependent projects (compile dependency) in JaCoCo test report
    tasks.jacocoTestReport {
        // get all projects we have a (compile) dependency on
        configurations.implementation.get().allDependencies.withType<ProjectDependency>().forEach {
            additionalSourceDirs(files(it.dependencyProject.sourceSets.main.get().java.srcDirs))
            additionalClassDirs(files(files(it.dependencyProject.sourceSets.main.get().java.destinationDirectory).map { file ->
                fileTree(file) {
                    exclude("org/apache/**", "edu/**", "org/geogebra/common/kernel/barycentric/**")
                }
            }))
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = false
    }
}
