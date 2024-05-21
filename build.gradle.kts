buildscript {
    repositories {
        mavenLocal()
        maven { url=uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
        maven { url=uri("https://plugins.gradle.org/m2/") }
        maven { url=uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-plugin-service") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-third-party-dependencies/") }
    }
    dependencies {
        classpath("org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.7.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
    }
}

plugins {
    java
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.intellij") version "0.7.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// https://github.com/JetBrains/gradle-intellij-plugin/
// http://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html
intellij {
    version = "2020.2"
//2023.1

//    type="IC"  // 社区版
//    setPlugins(arrayOf("java")) //Bundled plugin dependencies
    type = "IU" // 企业版
    setPlugins("java",
        "Kotlin",
        "IntelliLang",
        "Spring",
        "SpringBoot",
        "DatabaseTools") //Bundled plugin dependencies

    pluginName = "MybatisX"
    sandboxDirectory = "${rootProject.rootDir}/idea-sandbox"

    updateSinceUntilBuild = false
    isDownloadSources = true
}

//publishPlugin {
//    username = project.hasProperty("publishUsername") ? project.property("publishUsername") : ""
//    password = project.hasProperty("publishPassword") ? project.property("publishPassword") : ""
//}

// 各种版本去这里找
// https://www.jetbrains.com/intellij-repository/releases

group="com.baomidou.plugin.idea.mybatisx"
version="1.6.4"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.softwareloop:mybatis-generator-lombok-plugin:1.0")
    implementation("uk.com.robust-it:cloning:1.9.2")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.freemarker:freemarker:2.3.30")
    implementation("com.itranswarp:compiler:1.0")
    testCompile("junit:junit:4.13.1")
    testCompile("commons-io:commons-io:2.8.0")
    compileOnly("org.projectlombok:lombok:1.18.18")
    annotationProcessor ("org.projectlombok:lombok:1.18.18")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
