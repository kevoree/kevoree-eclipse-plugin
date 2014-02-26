
package org.kevoree.tools.eclipse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.waxeye.parser.ParseError;


/**
 * @author Olivier Barais
 */
public class MarkingErrorHandler 
{

	public static final String ERROR_MARKER_ID = "kevoree-eclipse-plugin.kevscripterror";

	private IFile file;
	private IDocument document;

	public MarkingErrorHandler(IFile file, IDocument document)
	{
		super();
		this.file = file;
		this.document = document;
	}

	public void removeExistingMarkers()
	{
		try
		{
			file.deleteMarkers(ERROR_MARKER_ID, true, IResource.DEPTH_ZERO);
		}
		catch (CoreException e1)
		{
			e1.printStackTrace();
		}
	}

	public void markError(ParseError e) {
		
		Map map = new HashMap();
		
		int lineNumber = e.getLine();
		int columnNumber = e.getColumn();
		String errorMessage = e.toString();

		int position = e.getPosition();
		
		KevScriptValidationError validationError = new KevScriptValidationError();
		validationError.setLineNumber(lineNumber);
		validationError.setColumnNumber(columnNumber);
		validationError.setErrorMessage(errorMessage);
		
		
		MarkerUtilities.setLineNumber(map, lineNumber);
		MarkerUtilities.setMessage(map, e.toString());
		map.put(IMarker.LOCATION, file.getFullPath().toString());

		Integer charStart = getCharStart(lineNumber, columnNumber);
		if (charStart != null)
			map.put(IMarker.CHAR_START, charStart);

		Integer charEnd = getCharEnd(lineNumber, columnNumber);
		if (charEnd != null)
			map.put(IMarker.CHAR_END, charEnd);
		
		
		//System.err.println(charStart + " " +charEnd);
		

		map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));

		try
		{
			MarkerUtilities.createMarker(file, map, ERROR_MARKER_ID);
		}
		catch (CoreException ee)
		{
			ee.printStackTrace();
		}
		
	}
	

	private Integer getCharEnd(int lineNumber, int columnNumber)
	{
		try
		{
			return new Integer(document.getLineOffset(lineNumber - 1) + columnNumber);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Integer getCharStart(int lineNumber, int columnNumber)
	{
		try
		{
			int lineStartChar = document.getLineOffset(lineNumber - 1);
			return new Integer(lineStartChar);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			return null;
		}
	}



}