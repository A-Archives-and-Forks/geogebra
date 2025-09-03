import groovy.lang.Closure

data class Spec(
    val id: String,
    val title: String,
    val transKey: String,
    val exe: String? = null,
    val pkg: String? = null,
    val releases: String? = null,
    val icon: String? = null,
    val measurementId: String? = null,
    val firebaseAppId: String? = null,
    val appStoreId: String? = null,
    val partials: List<String> = listOf(),
    val vendor: String? = null
) {}

open class SpecList(specs: List<Spec>) {
    val all = specs

    fun forEach(action: Closure<Spec>) {
        for (spec in all) {
            action.call(spec)
        }
    }

    fun forEachOffline(action: Closure<Spec>) {
        for (spec in all.filter { it.pkg != null }) {
            action.call(spec)
        }
    }
}

project.extensions.create(
    "appSpecs",
    SpecList::class.java,
    listOf(
        Spec(
            id = "classic",
            title = "Classic",
            transKey = "Classic",
            exe = "GeoGebra",
            pkg = "GeoGebra_6.0",
            releases = "Releases",
            icon = "ggb.ico",
            measurementId = "G-8JGZQG6FHG",
            firebaseAppId = "1:895270214636:web:3c05deeefcd1a96584fec9"
        ),
        Spec(
            id = "graphing",
            title = "Graphing Calculator",
            transKey = "GraphingCalculator",
            exe = "GeoGebraGraphing",
            pkg = "GeoGebra_Graphing",
            releases = "Releases-graphing",
            icon = "Graphing.ico",
            appStoreId = "1146717204",
            measurementId = "G-55K7P9GRQK",
            firebaseAppId = "1:895270214636:web:db7055c9a67f1f1e84fec9"
        ),
        Spec(
            id = "3d",
            title = "3D Graphing Calculator",
            transKey = "Graphing3D",
            appStoreId = "1445871976",
            firebaseAppId = "1:895270214636:web:574d60209dda5f5384fec9",
            measurementId = "G-PMP3RB4M8S"
        ),
        Spec(
            id = "cas",
            title = "CAS Calculator",
            transKey = "CASCalculator",
            exe = "GeoGebraCAS",
            pkg = "GeoGebra_CAS",
            releases = "Releases-cas",
            icon = "CAS.ico",
            appStoreId = "1436278267",
            measurementId = "G-N3Z7S82FZ6",
            firebaseAppId = "1:895270214636:web:87b00b628121939d84fec9"
        ),
        Spec(
            id = "scientific",
            title = "Scientific Calculator",
            transKey = "ScientificCalculator",
            partials = listOf("undo-redo"),
            appStoreId = "1412748754",
            measurementId = "G-YJ1QVF1SFK",
            firebaseAppId = "1:895270214636:web:540b533d4b47a7bb84fec9"
        ),
        Spec(
            id = "geometry",
            title = "Geometry",
            transKey = "Geometry",
            exe = "GeoGebraGeometry",
            pkg = "GeoGebra_Geometry",
            releases = "Releases-geometry",
            icon = "Geometry.ico",
            appStoreId = "1232591335",
            measurementId = "G-82GL4RTR4H",
            firebaseAppId = "1:895270214636:web:6bf3d469e0d2b32584fec9"
        ),
        Spec(
            id = "suite",
            title = "Calculator Suite",
            transKey = "CalculatorSuite",
            firebaseAppId = "1:895270214636:web:7daf432883a8464084fec9",
            icon = "ggb.ico",
            measurementId = "G-FXVLXMD21Y",
            appStoreId = "1504416652",
            pkg = "GeoGebra_Calculator",
            exe = "GeoGebraCalculator",
            releases = "Releases-suite",
            partials = listOf("share-button", "signin-button", "undo-redo")
        ),
        Spec(
            id = "notes",
            title = "Notes",
            transKey = "Notes",
            exe = "GeoGebraNotes",
            releases = "Releases-notes",
            icon = "notes.ico",
            measurementId = "G-MWHBRQSXTL",
            firebaseAppId = "1:895270214636:web:aece5066924cacb084fec9"
        ),
        Spec(
            id = "notes-mebis",
            title = "Board",
            transKey = "Board",
            exe = "MebisBoard",
            pkg = "Mebis_Board",
            icon = "notes.ico",
            vendor = "Mebis"
        )
    )
)
