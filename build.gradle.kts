plugins {
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("java")
}

group = "de.hglabor"
version = "1.0.1"

dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings("net.fabricmc:yarn:1.19.2+build.8")
    modImplementation("net.fabricmc:fabric-loader:0.14.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.60.0+1.19.2")
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version,
        )
        inputs.properties(properties)
        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }
    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("EurYzI8I")
    versionNumber.set(project.version.toString())
    versionType.set("release")
    gameVersions.addAll(listOf("1.19", "1.19.1", "1.19.2"))
    loaders.add("fabric")
    loaders.add("quilt")
    dependencies {
        required.project("fabric-api")
    }
    uploadFile.set(tasks.remapJar.get())
}