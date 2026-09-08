import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.intellij.platform") version "2.18.1"
    kotlin("jvm") version "2.4.20"
}
intellijPlatform{
    pluginConfiguration  {
        ideaVersion {
            sinceBuild = "251"
            untilBuild = "263.*"
        }
    }
}

repositories {
    mavenLocal()
    maven { url=uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
    maven { url=uri("https://plugins.gradle.org/m2/") }
    maven { url=uri("https://oss.sonatype.org/content/repositories/releases/") }
    maven { url=uri("https://dl.bintray.com/jetbrains/intellij-plugin-service") }
    maven { url=uri("https://dl.bintray.com/jetbrains/intellij-third-party-dependencies/") }
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven {url=uri("https://oss.sonatype.org/content/repositories/snapshots/")}
}


// 各种版本去这里找
// https://www.jetbrains.com/intellij-repository/releases
group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

dependencies {
    implementation("com.softwareloop:mybatis-generator-lombok-plugin:1.0")
    implementation("io.github.kostaskougios:cloning:1.13.0")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.freemarker:freemarker:2.3.35")
    implementation("com.itranswarp:compiler:1.0")
// https://mvnrepository.com/artifact/commons-lang/commons-lang
    implementation("org.apache.commons:commons-lang3:3.20.0")


    testImplementation("junit:junit:4.13.1")
    testImplementation("commons-io:commons-io:2.22.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor ("org.projectlombok:lombok:1.18.48")

    intellijPlatform {
        intellijIdeaUltimate("2025.1")
        bundledModule("org.intellij.intelliLang")
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
//    compileOnly(libs.intellij.structure.base) {
//        exclude("org.jetbrains.kotlin")
//    }
//    compileOnly(libs.intellij.structure.intellij) {
//        exclude("org.jetbrains.kotlin")
//        exclude("org.jetbrains.kotlinx")
//    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named("buildSearchableOptions").configure {
    enabled = false
}
