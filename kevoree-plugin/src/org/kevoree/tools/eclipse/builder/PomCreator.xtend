package org.kevoree.tools.eclipse.builder

import org.kevoree.tools.eclipse.Activator
import org.kevoree.tools.eclipse.preferences.PreferenceConstants

class PomCreator {
	
	def static String getKevs(String projectname){
		val template = '''
//sample of KevScript configuration
add node0: JavaNode/LATEST/LATEST
//add node0.hello: mynamespace.HelloWorld/1/{java: '1.0.0-SNAPSHOT'}
add sync : CentralizedWSGroup/LATEST/LATEST
attach node0 sync
set sync.isMaster/node0="true"

		'''
		return template
		}
	
	def static String getPom(String projectname, boolean xtend){
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
		«IF xtend»
        <xtend.version>2.12.0</xtend.version>
		«ENDIF»
	    <kevoree.version>«version»</kevoree.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.kevoree</groupId>
            <artifactId>org.kevoree.api</artifactId>
            <version>${kevoree.version}</version>
        </dependency>
        «IF xtend »
        		<dependency>
			<groupId>org.eclipse.xtend</groupId>
			<artifactId>org.eclipse.xtend.lib</artifactId>
			<version>${xtend.version}</version>
		</dependency>
		«ENDIF»
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
                <groupId>org.kevoree</groupId>
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
                    <!--<nodename>node0</nodename>-->
                    <kevscript>src/main/kevs/main.kevs</kevscript>
                    <namespace>mynamespace</namespace>
                    <kevoree.version>«version»</kevoree.version>
                </configuration>
            </plugin>
			«IF xtend»
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
			«ENDIF»
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
	<pluginRepositories>
		<pluginRepository>
			<id>snap</id>
			<snapshots><enabled>true</enabled></snapshots>
			<url>http://oss.sonatype.org/content/groups/public/</url>
		</pluginRepository>
	</pluginRepositories>
	<repositories>
		<repository>
			<id>snap1</id>
			<snapshots><enabled>true</enabled></snapshots>
			<url>http://oss.sonatype.org/content/groups/public/</url>
		</repository>
	</repositories>	
</project>
		
		'''
		
		return template
		
	}
	
}
