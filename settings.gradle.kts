pluginManagement{
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement{
    repositories{
        maven { url=uri("https://maven.aliyun.com/repository/public/") }
    }
}

rootProject.name = "MybatisX"
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
