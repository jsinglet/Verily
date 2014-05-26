<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>${projectName}</groupId>
    <artifactId>${projectName}</artifactId>
    <packaging>jar</packaging>
    <version>1.0-SNAPSHOT</version>




    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
            <version>2.3.14</version>
        </dependency>

        <dependency>
            <groupId>Verily</groupId>
            <artifactId>parser</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>


        <dependency>
            <groupId>Verily</groupId>
            <artifactId>core</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.8.1</version>
            <scope>test</scope>
        </dependency>


        <dependency>
            <groupId>org.jmlspecs</groupId>
            <artifactId>jmlruntime</artifactId>
            <version>20140525</version>
            <scope>system</scope>
            <systemPath>${r"${project.basedir}"}/lib/jmlruntime.jar</systemPath>
        </dependency>


    </dependencies>


    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.3.2</version>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                    <showDeprecation>false</showDeprecation>
                    <showWarnings>false</showWarnings>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
