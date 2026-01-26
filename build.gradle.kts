//import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

// 1.x转2.x的参考 https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html#ids-of-bundled-plugins
plugins {
//    id("java") // Java support
//    alias(libs.plugins.kotlin) apply false // Kotlin support
//    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    id("org.jetbrains.intellij.platform") version "2.2.1"
//    id("org.jetbrains.intellij.platform.migration") version "2.1.0"
    kotlin("jvm") version "1.9.0"
}
intellijPlatform{
    pluginConfiguration  {
        ideaVersion {
            sinceBuild = "232"
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
    gradlePluginPortal()
}


// 各种版本去这里找
// https://www.jetbrains.com/intellij-repository/releases
group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

dependencies {
    implementation("com.softwareloop:mybatis-generator-lombok-plugin:1.0")
    implementation("uk.com.robust-it:cloning:1.9.2")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.freemarker:freemarker:2.3.30")
    implementation("com.itranswarp:compiler:1.0")
// https://mvnrepository.com/artifact/commons-lang/commons-lang
    implementation("commons-lang:commons-lang:2.6")


    testImplementation("junit:junit:4.13.1")
    testImplementation("commons-io:commons-io:2.18.0")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor ("org.projectlombok:lombok:1.18.24")

    intellijPlatform {
        // 依赖版本  https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html#default-target-platforms
        intellijIdeaUltimate( providers.gradleProperty("platformVersion"), useInstaller = true)
//        intellijIdeaCommunity( providers.gradleProperty("platformVersion"), useInstaller = true)
        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

//        instrumentationTools()
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
    compileOnly(libs.intellij.structure.base) {
        exclude("org.jetbrains.kotlin")
    }
    compileOnly(libs.intellij.structure.intellij) {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
    }
//    implementation(libs.annotations)
//    implementation(libs.undertow)

//    implementation(libs.intellij.structure.base) {
//        exclude("org.jetbrains.kotlin")
//    }
//    implementation(libs.intellij.structure.intellij) {
//        exclude("org.jetbrains.kotlin")
//        exclude("org.jetbrains.kotlinx")
//    }
//    implementation(libs.intellij.pluginRepositoryRestClient) {
//        exclude("org.jetbrains.kotlin")
//        exclude("org.jetbrains.kotlinx")
//        exclude("org.slf4j")
//    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named("buildSearchableOptions").configure {
    enabled = false
}
