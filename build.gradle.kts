plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}
// Version of HungerGames
val projectVersion = "5.0.0-beta4"
// Minimum version of Minecraft that HungerGames supports
val apiVersion = "1.21.10"
// Where this builds on the server
val serverLocation = "1-21-11"
// Minecraft version to build against
val minecraftVersion = "26.1.2"

java.sourceCompatibility = JavaVersion.VERSION_25

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
        content { includeGroup("io.lumine") }
    }

    // Papi
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // CodeMC (NBT-API)
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:${minecraftVersion}.build.+")

    // Command Api
    implementation("dev.jorel:commandapi-paper-shade:11.2.0")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.2.0")

    // MythicMobs
    compileOnly("io.lumine:Mythic-Dist:5.6.1")

    // Papi
    compileOnly("me.clip:placeholderapi:2.12.2")

    // NBT-API
    implementation("de.tr7zw:item-nbt-api:2.15.7") {
        isTransitive = false
    }

    // VaultAPI
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        isTransitive = false
    }

    // FastBoard
    implementation("fr.mrmicky:fastboard:2.1.5")
}

tasks {
    register("server", Copy::class) {
        dependsOn("shadowJar")
        from("build/libs") {
            include("HungerGames-*.jar")
            destinationDir = file("/Users/ShaneBee/Desktop/Server/Minecraft/${serverLocation}/plugins/")
        }

    }
    processResources {
        val prop = ("version" to projectVersion)
        val prop2 = ("apiversion" to apiVersion)
        filesMatching("paper-plugin.yml") {
            expand(prop, prop2)
        }

    }
    compileJava {
        options.release = 25
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
        exclude("com/shanebeestudios/hg/plugin/commands")
        exclude("com/shanebeestudios/hg/plugin/listeners")
        (options as StandardJavadocDocletOptions).links(
            "https://jd.papermc.io/paper/1.21.5/",
            "https://jd.advntr.dev/api/4.17.0/",
            "https://tr7zw.github.io/Item-NBT-API/v2-api/"
        )

    }
    shadowJar {
        archiveFileName = "HungerGames-${projectVersion}.jar"
        archiveClassifier.set("")
        relocate("fr.mrmicky.fastboard", "com.shanebeestudios.hg.shaded-api.fastboard")
        relocate("dev.jorel.commandapi", "com.shanebeestudios.hg.shaded-api.commandapi")
        relocate("de.tr7zw.changeme.nbtapi", "com.shanebeestudios.hg.shaded-api.nbt")
        relocate("org.bstats", "com.shanebeestudios.hg.api.metrics")
    }
    jar {
        enabled = false
        dependsOn(shadowJar)
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
        withSourcesJar()
    }
}
