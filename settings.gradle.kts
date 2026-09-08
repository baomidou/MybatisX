pluginManagement{
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        gradlePluginPortal()
    }
}

rootProject.name = "MybatisX"
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
