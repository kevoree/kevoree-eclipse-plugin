package org.kevoree.tools.eclipse.builder

import org.eclipse.jface.preference.IPreferenceStore
import org.kevoree.tools.eclipse.Activator
import org.kevoree.tools.eclipse.preferences.PreferenceConstants

class PomCreator {
	
	def static String getKevs(String projectname){
		val template = '''
//sample of KevScript configuration
repo "http://repo1.maven.org/maven2"
include mvn:org.kevoree.library.java:org.kevoree.library.java.javaNode:release
include mvn:org.kevoree.library.java:org.kevoree.library.java.ws:release
add node0 : JavaNode
add sync : WSGroup
attach node0 sync
		'''
		return template
		}
	
	def static String getPom(String projectname){
					val store = Activator.getDefault().getPreferenceStore();
			val version =store.getString(PreferenceConstants.P_STRING);
		
		
		val template = '''
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>org.kevoree.project</groupId>
	<artifactId>«projectname»</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<name>«projectname»</name>
		<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <xtend.version>2.5.0</xtend.version>
	    <kevoree.version>«version»</kevoree.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.kevoree</groupId>
            <artifactId>org.kevoree.annotation.api</artifactId>
            <version>${kevoree.version}</version>
        </dependency>
        <dependency>
            <groupId>org.kevoree</groupId>
            <artifactId>org.kevoree.api</artifactId>
            <version>${kevoree.version}</version>
        </dependency>
        		<dependency>
			<groupId>org.eclipse.xtend</groupId>
			<artifactId>org.eclipse.xtend.lib</artifactId>
			<version>${xtend.version}</version>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.10</version>
			<scope>test</scope>
		</dependency>
        
    </dependencies>
	
	<build>
		<plugins>
		      <plugin>
                <groupId>org.kevoree.tools</groupId>
                <artifactId>org.kevoree.tools.mavenplugin</artifactId>
                <version>${kevoree.version}</version>
                <extensions>true</extensions>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <nodename>node0</nodename>
                    <model>src/main/kevs/main.kevs</model>
                </configuration>
            </plugin>
			<plugin>
				<groupId>org.eclipse.xtend</groupId>
				<artifactId>xtend-maven-plugin</artifactId>
				<version>${xtend.version}</version>
				<executions>
					<execution>
						<goals>
							<goal>compile</goal>
							<goal>testCompile</goal>
							<goal>xtend-install-debug-info</goal>
							<goal>xtend-test-install-debug-info</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.0</version>
				<configuration>
					<source>1.5</source>
					<target>1.5</target>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>
		
		'''
		
		return template
		
	}
	
}