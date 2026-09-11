pluginManagement{
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement{
    repositories{
        maven { url=uri("https://maven.aliyun.com/repository/public/") }
    }
}

rootProject.name = "MybatisX"
pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
