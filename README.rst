Spring Data Google Cloud Datastore
==================================

Setup
-----

Gradle
^^^^^^

To use spring-data-gclouddatastore from a Gradle project::

    repositories {
        // ...
        maven {
            url 'http://dl.bintray.com/tkob/maven'
        }
        // ...
    }

    dependencies {
        // ...
        compile 'yokohama.unit:spring-data-gclouddatastore:0.1.0'
        // ...
    }

Maven
^^^^^

To use spring-data-gclouddatastore from a Maven project::

    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-tkob-maven</id>
            <name>bintray</name>
            <url>http://dl.bintray.com/tkob/maven</url>
        </repository>
    </repositories>

    <dependencies>
        <!-- ... -->
        <dependency>
            <groupId>yokohama.unit</groupId>
            <artifactId>spring-data-gclouddatastore</artifactId>
            <version>0.1.0</version>
        </dependency>
        <!-- ... -->
    </dependencies>
