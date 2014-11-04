package org.kevoree.tools.eclipse

import java.util.ArrayList
import java.util.Arrays
import java.util.List
import org.eclipse.jface.text.ITextViewer
import org.eclipse.jface.text.contentassist.CompletionProposal
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.jface.text.contentassist.IContentAssistProcessor
import org.eclipse.jface.text.contentassist.IContextInformation
import org.eclipse.jface.text.contentassist.IContextInformationValidator

class KevScriptEditorContentAssistProcessor implements
		IContentAssistProcessor {
	new() {
		this.wordProvider = new KevScriptWordProvider();
	}

	private KevScriptWordProvider wordProvider;
	private String lastError;

	 override def ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {
		var document = textViewer.getDocument();
		var currOffset = documentOffset - 1;
		try {
			var currWord = "";
			var char currChar = document
							.getChar(currOffset);
			while (currOffset > 0
					&& !(Character.isWhitespace(currChar ) || currChar == ';')) {
				currWord = currChar + currWord;
				currOffset = currOffset-1;
				currChar = document
							.getChar(currOffset);
			}
			System.err.println(currWord);
			val cur = currWord
			var ICompletionProposal[] proposals;
			var List<String> suggestions = new ArrayList<String>();
			suggestions.addAll(Arrays.asList(KevScriptKeyWords.KEYWORDS).filter[sug | sug.startsWith(cur)]);

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

	private def ICompletionProposal[] buildProposals(List<String> suggestions,
			String replacedWord, int offset) throws Exception {
		val List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		var int index = 0;
		suggestions.forEach[currSuggestion |
				var CompletionProposal cp = new CompletionProposal(currSuggestion,
					offset, replacedWord.length(), currSuggestion.length(),
					null, currSuggestion, null, null);
			proposals.add(cp);
		]
		return proposals;
	}

	
	override def IContextInformation[] computeContextInformation(
			ITextViewer itextviewer, int i) {
		lastError = "No Context Information available";
		return null;
	}

	
	override def char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	
	override def char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	
	override def IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	override def String getErrorMessage() {
		return lastError;
	}
	
}