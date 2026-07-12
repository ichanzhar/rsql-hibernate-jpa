plugins {
    id("com.gradleup.nmcp.settings") version "1.6.1"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "rsql-hibernate-jpa-root"

include("rsql-hibernate-jpa")

nmcpSettings {
    centralPortal {
        username = System.getenv("CENTRAL_USERNAME")
        password = System.getenv("CENTRAL_PASSWORD")
        publishingType = "AUTOMATIC"
    }
}
