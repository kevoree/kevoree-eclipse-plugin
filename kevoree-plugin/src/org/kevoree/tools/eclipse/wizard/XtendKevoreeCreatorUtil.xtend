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
			sb.append(lineSeparator);
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
import org.kevoree.factory.DefaultKevoreeFactory
import org.kevoree.factory.KevoreeFactory
import org.eclipse.xtend.lib.annotations.Accessors

@ComponentType(version=1,description="")
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
	@Accessors
	private int myparameter = 2000;

	var KevoreeFactory fact = new DefaultKevoreeFactory()

	@KevoreeInject
	private KevScriptService kevScriptService;

	@KevoreeInject
	private ModelService modelService;

	def adaptComponent() {
		//Get the current Model
		var model = modelService.getCurrentModel();
		// Clone the model to make it changeable
		var ContainerRoot localModel = fact.createModelCloner.clone(model.getModel()) as ContainerRoot
		
		
		// Apply the script on the current model, to get a new configuration
		kevScriptService.execute("//kevscripttoapply", localModel)

		//Ask the platform to apply the new model; register an optional callback to know when the adaptation is finised.
		modelService.update(localModel, [e | println("ok")])
		
	}

}		
					'''

		return template

	}
	
	def String createKevoreeGroupXtend(String groupName){
		val template = '''
import org.kevoree.annotation.GroupType
import org.kevoree.annotation.KevoreeInject
import org.kevoree.annotation.Start
import org.kevoree.annotation.Stop
import org.kevoree.api.ModelService
import org.kevoree.api.handler.ModelListener
import org.kevoree.api.handler.UpdateContext

@GroupType
class «groupName» implements ModelListener {

	@KevoreeInject
	ModelService modelService;

	@Start
	def void start() {
	}

	@Stop
	def void stop() {
	}

	override def boolean preUpdate(UpdateContext context) {
		return true;
	}

	override def boolean initUpdate(UpdateContext context) {
		return true;
	}

	override def boolean afterLocalUpdate(UpdateContext context) {
		return true;
	}

	override def void modelUpdated() {
	}

	override def void preRollback(UpdateContext context) {
	}

	override def void postRollback(UpdateContext context) {
	}

}
		'''
		return template
	}
	

	def String createKevoreeChannelXtend(String channelName){
		val template = '''
import org.kevoree.annotation.ChannelType
import org.kevoree.annotation.KevoreeInject
import org.kevoree.api.Callback
import org.kevoree.api.ChannelContext
import org.kevoree.api.ChannelDispatch
import org.kevoree.api.Port

@ChannelType(version=1,description="")
public class «channelName» implements ChannelDispatch {

    @KevoreeInject
    ChannelContext channelContext;
	
	def override  ^dispatch(String payload, Callback callback) {
		for (Port p : channelContext.getLocalPorts()) {
            p.send(payload, callback);
        }
	}
}
		'''
		return template
	}


	def String createKevoreeComponentJava(String componentName) {
		val template = '''

import org.kevoree.annotation.*;
import org.kevoree.api.*;


@ComponentType(version=1,description="")
public class «componentName» {

    @Param(defaultValue = "Default Content")
    String message;

    @KevoreeInject
    org.kevoree.api.Context context;

    @Output
    org.kevoree.api.Port out;

    @Input
    public void in(Object i) {
        String msg = message+" from "+context.getInstanceName()+"@"+context.getNodeName();
        System.out.println(msg);
        out.send(msg,new Callback() {

			public void onError(Throwable arg0) {
				// TODO Auto-generated method stub				
			}

			public void onSuccess(CallbackResult arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    }

    @Start
    public void start() {}

    @Stop
    public void stop() {}

    @Update
    public void update() {System.out.println("Param updated!");}

}

					'''

		return template

	}
	
	def String createKevoreeGroupJava(String groupName){
		val template = '''
import org.kevoree.annotation.*;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateContext;
import org.kevoree.api.ModelService;

@GroupType(version=1,description="")
public class «groupName» implements ModelListener {

    @KevoreeInject
    public ModelService modelService;

    @Start
    public void start() {}

    @Stop
    public void stop() {}

    public boolean preUpdate(UpdateContext context) {return true;}

    public boolean initUpdate(UpdateContext context) {return true;}

    public boolean afterLocalUpdate(UpdateContext context) {return true;}

    public void modelUpdated() {}

    public void preRollback(UpdateContext context) {}

    public void postRollback(UpdateContext context) {}

}
		'''
		return template
	}
	

	def String createKevoreeChannelJava(String channelName){
		val template = '''
import org.kevoree.annotation.*;
import org.kevoree.api.*;

@ChannelType(version=1,description="")
public class «channelName» implements ChannelDispatch {

    @KevoreeInject
    ChannelContext channelContext;

    @Override
    public void dispatch( String payload,  Callback callback) {
        for (Port p : channelContext.getLocalPorts()) {
            p.send(payload, callback);
        }
	}
}    
		'''
		return template
	}


}
