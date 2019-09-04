import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */


plugins {
	// Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
	id("org.jetbrains.kotlin.jvm").version("1.3.21")

	// Apply the application plugin to add support for building a CLI application.
	application
}

repositories {
	// Use jcenter for resolving your dependencies.
	// You can declare any Maven/Ivy/file repository here.
	jcenter()
}

dependencies {
	// Use the Kotlin JDK 8 standard library.
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.jetbrains.kotlin:kotlin-reflect")

	//implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M1")

	// Use the Kotlin test library.
	//testImplementation("org.jetbrains.kotlin:kotlin-test")

	// Use the Kotlin JUnit integration.
	//testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

	testImplementation("org.junit.jupiter:junit-jupiter:5.5.0")

	implementation("com.googlecode.json-simple:json-simple:1.1.1")

	implementation("com.google.code.gson:gson:2.8.5")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

application {
	// Define the main class for the application.
	mainClassName = "net.merayen.elastic.Test"
}

sourceSets["main"].java.srcDirs(arrayOf("src/main/kotlin"))
sourceSets["main"].withConvention(KotlinSourceSet::class) {
	kotlin.srcDirs(arrayOf("src/main/kotlin"))
}
sourceSets["main"].resources.srcDirs(arrayOf("src/main/resources"))

tasks.jar {
	manifest {
		attributes(
			"Implementation-Title" to "Elastic",
			"Main-Class" to "net.merayen.elastic.Main"
		)
	}

	from(Callable {
		configurations["compileClasspath"].map { if (it.isDirectory) it else zipTree(it) }
	})
}