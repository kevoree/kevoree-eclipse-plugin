package org.kevoree.tools.eclipse.wizard

import org.eclipse.jdt.core.IPackageFragment

class XtendKevoreeCreatorUtil {

	private new() {
	}

	private static XtendKevoreeCreatorUtil _instance;

	def static XtendKevoreeCreatorUtil getInstance() {
		if (_instance == null) {
			_instance = new XtendKevoreeCreatorUtil();
		}
		return _instance;
	}

	def String createPackageDeclaration(String typeName, IPackageFragment packageFragment, String lineSeparator) {
		var sb = new StringBuffer();
		if (packageFragment.getElementName() != null && !packageFragment.getElementName().equals("")) {
			sb.append("package ");
			sb.append(packageFragment.getElementName());
		}
		return sb.toString();
	}

	def String createKevoreeComponentXtend(String componentName) {
		val template = '''
import org.kevoree.ContainerRoot
import org.kevoree.annotation.ComponentType
import org.kevoree.annotation.Input
import org.kevoree.annotation.KevoreeInject
import org.kevoree.annotation.Output
import org.kevoree.annotation.Param
import org.kevoree.annotation.Start
import org.kevoree.annotation.Stop
import org.kevoree.api.KevScriptService
import org.kevoree.api.ModelService
import org.kevoree.api.Port
import org.kevoree.cloner.DefaultModelCloner

@ComponentType
class «componentName» {
	
	@Start
	public def startComponent() {
		println("Start");
	}

	@Stop
	public def stopComponent() {
		println("Stop");
	}

	@Input
	public def consumeHello(Object o) {
		println("Received " + o.toString());
		if (o instanceof String) {
			var msg = o as String;
			println("HelloConsumer received: " + msg);
		}
	}

	@Output
	private Port simplePort;

	@Param(defaultValue="2000")
	@Property
	private int myparameter = 2000;

	//Init the variables (from inside a component)
	var cloner = new DefaultModelCloner();

	@KevoreeInject
	private KevScriptService kevScriptService;

	@KevoreeInject
	private ModelService modelService;

	def adaptComponent() {
		//Get the current Model
		var model = modelService.getCurrentModel();
		// Clone the model to make it changeable
		var ContainerRoot localModel = cloner.clone(model.getModel()) as ContainerRoot
		
		
		// Apply the script on the current model, to get a new configuration
		kevScriptService.execute("//kevscripttoapply", localModel)

		//Ask the platform to apply the new model; register an optional callback to know when the adaptation is finised.
		modelService.update(localModel, [e | println("ok")])
		
	}

}		
					'''

		return template

	}
	
	def String createKevoreeGroup(String groupName){
		val template = '''
import org.kevoree.ContainerRoot
import org.kevoree.annotation.GroupType
import org.kevoree.annotation.KevoreeInject
import org.kevoree.annotation.Library
import org.kevoree.annotation.Start
import org.kevoree.annotation.Stop
import org.kevoree.api.ModelService
import org.kevoree.api.handler.ModelListener

@GroupType
@Library(name="Java")
class «groupName» implements ModelListener {

	@KevoreeInject
	ModelService modelService;

	@Start
	def void start() {
	}

	@Stop
	def void stop() {
	}

	override def boolean preUpdate(ContainerRoot currentModel, ContainerRoot proposedModel) {
		return true;
	}

	override def boolean initUpdate(ContainerRoot currentModel, ContainerRoot proposedModel) {
		return true;
	}

	override def boolean afterLocalUpdate(ContainerRoot currentModel, ContainerRoot proposedModel) {
		return true;
	}

	override def void modelUpdated() {
	}

	override def void preRollback(ContainerRoot currentModel, ContainerRoot proposedModel) {
	}

	override def void postRollback(ContainerRoot currentModel, ContainerRoot proposedModel) {
	}

}
		'''
		return template
	}
	

	def String createKevoreeChannel(String channelName){
		val template = '''
import org.kevoree.annotation.ChannelType
import org.kevoree.annotation.KevoreeInject
import org.kevoree.annotation.Library
import org.kevoree.api.Callback
import org.kevoree.api.ChannelContext
import org.kevoree.api.ChannelDispatch
import org.kevoree.api.Port

@ChannelType
@Library(name = "Java")
public class «channelName» implements ChannelDispatch {

    @KevoreeInject
    ChannelContext channelContext;
	
	def override  ^dispatch(Object payload, Callback callback) {
		for (Port p : channelContext.getLocalPorts()) {
            p.call(payload, callback);
        }
	}
}
		'''
		return template
	}

}
