plugins {
    java
}

group = "me.d2glaidee"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.DevLeoko:AdvancedBan:v2.3.0") {
        isTransitive = false
    }
}

tasks.jar {
    archiveFileName.set("AbanGUI-${version}.jar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
