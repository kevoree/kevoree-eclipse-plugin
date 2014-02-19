package org.kevoree.tools.eclipse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KevScriptWordProvider {
	public List<String> suggest(String word) {
		ArrayList<String> wordBuffer = new ArrayList<String>();
		for (String s : KevScriptKeyWords.KEYWORDS)
			if (s.startsWith(word))
				wordBuffer.add(s);
		Collections.sort(wordBuffer);
		return wordBuffer;
	}
}