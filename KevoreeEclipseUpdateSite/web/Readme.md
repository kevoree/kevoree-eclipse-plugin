#  Kevoree Eclipse Plugin

Basic useful feature list:

 * A project template that automatically activate Kevoree and m2e (maven) nature
 * A Kevoree Nature that automatically activate a specific builder
 * A set of Kevoree entity templates to quickly create Components, Groups, Channels and KevScripts
 * A KevScript textual editor with completion
 * A Kevscript graphical editor
 * A preference page for setting the Kevoree version used
 * A specific builder to automatically generate the model.


	0. Initial requirement 
1. You need a Java JDK at least version 6 with a JAVA_HOME env variable 
2. Start from an eclipse with Xtend and maven plugin, you can use this one https://www.eclipse.org/xtend/download.html


	1. First Step (Install the plugin)
 
 Add an update site in Eclipse (http://coreff.kevoree.org/site/)
 
 ![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeInstallPlugin.png)

	2. Step two (Create Kevoree Project)

When it is installed, you can create a new Kevoree Project. (File -> New Project -> Kevoree -> New Kevoree Project)

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeCreateProject.png)

Next you must set the Project Name

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeCreateProjectName.png)

Finally,  you get a project with three natures, (Java, Maven and Kevoree). The POM is automatically created

 ![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeProjectCreated.png)

	3. Step three. (Add Kevoree Entity) 

Now you can create Kevoree entities (components, channels, groups, kevscripts, ...)  (File -> New -> Kevoree -> ...). we provide templates for Java and Xtend programming languages. (C++, C, Scala, Kotlin, should arrive soon).

 ![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeCreateKevoreeEntity.png)

If you select a new Java Component, you must provide a Java package and a Name for your component. 

 ![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeCreateComponent.png)
 
 You will automatically get the following Java class. 

 ![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeComponentCreated.png)


	4. Step four. (Edit Kevoree script)

We provide also two KevScript editor. One is a textual editor with completion. The second one is a graphical editor. To work with the graphical editor, the component definition must exist in your maven repository (do not forget to do a mvn install to push the definition in the repository. Do not also forget to include your maven component artefact (include mvn:org.kevoree.project:HelloWorlKevoree:latest) to be able to use it (add node0.hello : HelloWorldComponent)

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeKevscriptEditors.png)

	5. Step five. (Run the Kevoree Script)
    
Now you can easily build a launcher for your kevscript. 

1. Select the project (do not forget the * to get all the available project). 
2. Select the kevscript to run
3. Select the node name to start

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeCreateRunner.png)

You will get the log in the console

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeCRunner.png)

	6. Step six. (Manage the runtime system)

You can ow open your chrome based browser (http://editor.kevoree.org/) and manage your application.

	7. Other stuffs. (Builder, preferences page)
    
We also provide a specific builder that automatically generate the model when the project changed. 

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeEclipseBuilder.png)

We also create a preference page for Kevoree to set the Kevoree version you want to use. It is only for the plugin (builder, runner, ...) . The Kevoree version you use for a specific project is set within the pom.xml)

![KevoreeProjectCreated](https://raw.github.com/kevoree/kevoree-eclipse-plugin/master/KevoreeEclipseUpdateSite/web/KevoreeEclipsePreference.png)

	8. Import a Kevoree project.
    
If you have to import an existing Kevoree project, you can just import it as an existing maven project and add the Kevoree Nature (right clic on the project, configure -> add Kevoree Nature)

Have fun...
