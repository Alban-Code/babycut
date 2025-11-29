plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "io.onelioh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.media", "javafx.fxml")
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-media:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.openjfx:javafx-swing:21")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    implementation("org.bytedeco:javacv-platform:1.5.12")

}

application {
    mainClass.set("io.onelioh.babycut.Main")
    // mainClass.set("io.onelioh.babycut.sandbox.TestVideoPlayerApp")
    // mainClass.set("io.onelioh.babycut.sandbox.JavaFXPlayer")
    // Ajoute ça pour être sûr que les modules JavaFX sont chargés :
    applicationDefaultJvmArgs = listOf("--add-modules=javafx.controls,javafx.media,javafx.fxml,javafx.swing")
}


tasks.test {
    useJUnitPlatform()
}