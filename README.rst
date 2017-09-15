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
        compile 'yokohama.unit:spring-data-gclouddatastore:0.2.1'
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
            <version>0.2.1</version>
        </dependency>
        <!-- ... -->
    </dependencies>

Example
-------

Person model::

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.data.annotation.Id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Person {
        @Id long id;
        String firstName;
        String lastName;
        String emailAddress;
    }

Spring Boot application::

    import java.util.List;
    import java.util.stream.Collectors;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.gclouddatastore.repository.EnableGcloudDatastoreRepositories;
    import org.springframework.data.gclouddatastore.repository.GcloudDatastoreRepository;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.bind.annotation.GetMapping;

    interface PersonRepository extends GcloudDatastoreRepository<Person, Long> {
        List<Person> findByEmailAddressAndLastName(
                String emailAddress, String lastName);
    }

    @EnableAutoConfiguration
    @EnableGcloudDatastoreRepositories
    public class Application {
        @RestController
        public static class Controller {
            @Autowired
            PersonRepository personRepository;

            @GetMapping("/")
            public String root() {
                Person person1 = new Person(1, "Jane", "Doe", "jane@example.com");
                personRepository.save(person1);

                List<Person> persons = personRepository
                    .findByEmailAddressAndLastName("jane@example.com", "Doe");
                return
                    persons.stream().map(Person::getFirstName).collect(Collectors.joining("\n"));
            }
        }

        public static void main(String[] args) throws Exception {
            SpringApplication.run(Application.class, args);
        }
    }
