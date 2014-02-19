package org.kevoree.tools.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class KevScriptEditorContentAssistProcessor implements
		IContentAssistProcessor {
	KevScriptEditorContentAssistProcessor() {
		this.wordProvider = new KevScriptWordProvider();
	}

	private KevScriptWordProvider wordProvider;
	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {
		IDocument document = textViewer.getDocument();
		int currOffset = documentOffset - 1;
		try {
			String currWord = "";
			char currChar;
			while (currOffset > 0
					&& !(Character.isWhitespace(currChar = document
							.getChar(currOffset)) || currChar == ';')) {
				currWord = currChar + currWord;
				currOffset--;
			}

			ICompletionProposal[] proposals;
			List<String> suggestions = new ArrayList<String>();
			suggestions.addAll(Arrays.asList(KevScriptKeyWords.KEYWORDS));

			{
				proposals = buildProposals(suggestions, currWord,
						documentOffset - currWord.length());
				lastError = null;
			}
			return proposals;
		} catch (Exception e) {
			e.printStackTrace();
			lastError = e.getMessage();
		}
		return null;
	}

	private ICompletionProposal[] buildProposals(List<String> suggestions,
			String replacedWord, int offset) throws Exception {
		ICompletionProposal[] proposals = new ICompletionProposal[suggestions
				.size()];
		int index = 0;
		for (Iterator<String> i = suggestions.iterator(); i.hasNext();) {
			String currSuggestion = (String) i.next();
			CompletionProposal cp = new CompletionProposal(currSuggestion,
					offset, replacedWord.length(), currSuggestion.length(),
					null, currSuggestion, null, null);
			proposals[index] = cp;
			index++;
		}
		return proposals;
	}

	@Override
	public IContextInformation[] computeContextInformation(
			ITextViewer itextviewer, int i) {
		lastError = "No Context Information available";
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return lastError;
	}
}