package org.kevoree.tools.eclipse;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class KevScriptEditorScanner extends RuleBasedScanner {
	private static RGB COMMENT = new RGB(128, 128, 128);

	public KevScriptEditorScanner() {
		super();
		setRules(extractRules());
	}

	private static RGB KEYWORD = new RGB(140, 10, 210);
	private static RGB DEFAULT = new RGB(0, 0, 0);
	private static RGB STRING = new RGB(0, 153, 0);

	private IRule[] extractRules() {
		IToken keyword = new Token(new TextAttribute(new Color(
				Display.getCurrent(), KEYWORD), null, SWT.BOLD));
		IToken comment = new Token(new TextAttribute(new Color(
				Display.getCurrent(), COMMENT), null, SWT.ITALIC));
		IToken defaut = new Token(new TextAttribute(new Color(
				Display.getCurrent(), DEFAULT)));
		IToken string = new Token(new TextAttribute(new Color(
				Display.getCurrent(), STRING)));

		WordRule rule = new WordRule(new IWordDetector() {
			@Override
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}

			@Override
			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}
		}, defaut);
		for (String k : KevScriptKeyWords.KEYWORDS) {
			rule.addWord(k, keyword);
		}
		IRule[] rules = new IRule[4];
		rules[0] = rule;
		rules[1] = new SingleLineRule("//", null, comment);
		rules[2] = new MultiLineRule("/*", "*/", comment);
		rules[3] = new SingleLineRule("\"", "\"", string);
		return rules;
	}
}
