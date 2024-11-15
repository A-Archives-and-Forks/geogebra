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

    testImplementation(project(":ggbjdk"))
    //testImplementation(libs.junit)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.core)

    testFixturesImplementation(project(":ggbjdk"))
    // testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.hamcrest)
    testFixturesImplementation(libs.mockito.core)


    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testFixturesImplementation(platform("org.junit:junit-bom:5.11.3"))

    testImplementation("org.junit.jupiter:junit-jupiter") {
        because("allows to write and run Jupiter tests")
    }
    testFixturesImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("junit:junit:4.13.2")
    testFixturesImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine") {
        because("allows JUnit 3 and JUnit 4 tests to run")
    }
    testFixturesRuntimeOnly("org.junit.vintage:junit-vintage-engine")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("allows tests to run from IDEs that bundle older version of launcher")
    }
    testFixturesRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    ignoreFailures = true
//    useJUnitPlatform {
//        includeEngines("junit-jupiter", "junit-ventage")
//    }
    useJUnitPlatform {
        includeEngines("junit-jupiter", "junit-vintage")
        // excludeEngines 'custom-engine'

        // includeTags 'fast'
        excludeTags("fast")
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
