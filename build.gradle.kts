plugins {
    id("java")
}

group = "net.azisaba"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://repo.mypet-plugin.de/") }
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MyPetORG.MyPet:mypet-api:5c8ceeac6a")
    compileOnly("de.keyle:knbt:0.0.5")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}
