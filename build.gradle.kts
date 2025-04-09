plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}
// Version of HungerGames
val projectVersion = "5.0.0-beta1"
// Where this builds on the server
val serverLocation = "1-21-4"
// Minecraft version to build against
val minecraftVersion = "1.21.4"

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    mavenLocal()

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")

    // Command Api Snapshots
    maven("https://s01.oss.sonatype.org/content/repositories")

    // JitPack repo
    maven("https://jitpack.io")

    // MythicMobs
    maven("https://mvn.lumine.io/repository/maven-public/") {
        content { includeGroup("io.lumine")  }
    }

    // Papi
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // CodeMC (NBT-API)
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:${minecraftVersion}-R0.1-SNAPSHOT")

    // Command Api
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.1.0")

    // MythicMobs
    compileOnly("io.lumine:Mythic-Dist:5.6.1")

    // Papi
    compileOnly("me.clip:placeholderapi:2.11.6")

    // NBT-API
    implementation("de.tr7zw:item-nbt-api:2.14.2-SNAPSHOT") {
        isTransitive = false
    }

    // VaultAPI
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        isTransitive = false
    }

    // FastBoard
    implementation("fr.mrmicky:fastboard:2.1.4")
}

tasks {
    register("server", Copy::class) {
        dependsOn("shadowJar")
        from("build/libs") {
            include("HungerGames-*.jar")
            destinationDir = file("/Users/ShaneBee/Desktop/Server/${serverLocation}/plugins/")
        }

    }
    processResources {
        val prop = ("version" to projectVersion)
        filesMatching("plugin.yml") {
            expand(prop)
        }
    }
    compileJava {
        options.release = 21
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
        exclude("com/shanebeestudios/hg/plugin/commands")
        exclude("com/shanebeestudios/hg/plugin/listeners")
        (options as StandardJavadocDocletOptions).links(
            "https://jd.papermc.io/paper/1.21.1/",
            "https://jd.advntr.dev/api/4.17.0/",
            "https://tr7zw.github.io/Item-NBT-API/v2-api/"
        )

    }
    shadowJar {
        relocate("fr.mrmicky.fastboard", "com.shanebeestudios.hg.api.fastboard")
        relocate("dev.jorel.commandapi", "com.shanebeestudios.hg.api.commandapi")
        relocate("de.tr7zw.changeme.nbtapi", "com.shanebeestudios.hg.api.nbt")
        relocate("org.bstats", "com.shanebeestudios.hg.api.metrics")
        archiveFileName = "HungerGames-${projectVersion}.jar"
    }
    jar {
        dependsOn(shadowJar)
        archiveFileName.set("HungerGames.jar")
    }
}
