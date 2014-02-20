package org.kevoree.tools.eclipse;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.kevoree.kevscript.Parser;
import org.kevoree.kevscript.Type;
import org.waxeye.ast.IAST;
import org.waxeye.input.InputBuffer;
import org.waxeye.parser.ParseResult;

@SuppressWarnings("deprecation")
public class KevScriptEditor extends TextEditor {
	public KevScriptEditor() {
		super();
		KevScriptEditorSourceViewerConfiguration configuration = new KevScriptEditorSourceViewerConfiguration();
		setSourceViewerConfiguration(configuration);
		this.getDocumentProvider();
	}
	
	private IEditorInput input;
	
	protected void doSetInput(IEditorInput newInput) throws CoreException
	{
		super.doSetInput(newInput);
		this.input = newInput;
		validateAndMark();
	}

	protected void editorSaved()
	{
		super.editorSaved();
		//we validate and mark document here
		validateAndMark();

	}

	protected void validateAndMark()
	{
		try
		{
			IDocument document = getInputDocument();
			String text = document.get();
			//System.err.println(text);
			MarkingErrorHandler markingErrorHandler = new MarkingErrorHandler(getInputFile(), document);
			//markingErrorHandler.setDocumentLocator(new LocatorImpl());
			markingErrorHandler.removeExistingMarkers();
			
			Parser parser = new Parser();
			ParseResult<Type> parserResult = parser.parse(new InputBuffer(text.toCharArray()));
	        IAST<Type> ast = parserResult.getAST();
	        if (ast == null) {
	        	markingErrorHandler.markError(parserResult.getError());
	        }
			
			//parser.setErrorHandler(markingErrorHandler);
			//parser.doParse(text);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
	}

	protected IDocument getInputDocument()
	{
		IDocument document = getDocumentProvider().getDocument(input);
		return document;
	}

	protected IFile getInputFile()
	{
		IFileEditorInput ife = (IFileEditorInput) input;
		IFile file = ife.getFile();
		return file;
	}
	
	
	public IEditorInput getInput()
	{
		return input;
	}
	
		@Override
	protected void createActions() {
		super.createActions();
		ResourceBundle resourceBundle = null;
		try {
			resourceBundle = new PropertyResourceBundle(
					new StringBufferInputStream(
							"ContentAssistProposal.label=Content assist\nContentAssistProposal.tooltip=Content assist\nContentAssistProposal.description=Provides Content Assistance"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ContentAssistAction action = new ContentAssistAction(resourceBundle,
				"ContentAssistProposal.", this);
		String id = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		action.setActionDefinitionId(id);
		setAction("ContentAssist", action);
	}
}