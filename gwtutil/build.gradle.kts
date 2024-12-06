plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.pmd)
}

description = "Developer tools required to compile web platforms"

dependencies {
    api(libs.gwt.widgets)
    api(libs.gwt.user)
    api(libs.gwt.dev)
    api(files("C:\\Users\\zbyne\\git\\gwt-core\\gwt-core\\target\\gwt-core-1.0.2-GGB.jar"))
    api(libs.elemental2.core)
    api(libs.elemental2.dom)
    api(libs.elemental2.webstorage)
    api(libs.elemental2.media)
    api(libs.elemental2.webgl)
    api(libs.gwt.resources.api)
    api(libs.gwt.timer)

    implementation(project(":common"))
    implementation(libs.autoService)
}
