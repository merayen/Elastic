import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	application
	kotlin("jvm") version "1.4.0"
}

repositories {
	jcenter()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
	implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

application {
	// Define the main class for the application.
	mainClassName = "net.merayen.elastic.Main"
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}
