/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * The syntax colouring of theory mapping files (.ptm), with the colours of
 * Eclipse's Java editor (for light and dark themes): comments
 * <code>// ...</code>, the keywords <code>operator</code> and
 * <code>internal</code>, the operators' names <code>"..."</code>, and the
 * tags <code>{...}</code> and B formulas <code>{$...$}</code>.
 */
public class MappingFileScanner extends RuleBasedScanner {

	public MappingFileScanner() {
		final boolean dark = isDark();
		final IToken comment = token("comment", dark, new RGB(98, 151, 85),
				new RGB(63, 127, 95), SWT.ITALIC);
		final IToken keyword = token("keyword", dark, new RGB(204, 108, 29),
				new RGB(127, 0, 85), SWT.BOLD);
		final IToken name = token("name", dark, new RGB(23, 198, 163),
				new RGB(42, 0, 255), SWT.NORMAL);
		final IToken tag = token("tag", dark, new RGB(102, 225, 248),
				new RGB(0, 0, 192), SWT.BOLD);
		final IToken formula = token("formula", dark, new RGB(230, 219, 116),
				new RGB(0, 128, 128), SWT.NORMAL);

		final WordRule keywords = new WordRule(new IWordDetector() {
			@Override
			public boolean isWordStart(final char c) {
				return Character.isLetter(c);
			}

			@Override
			public boolean isWordPart(final char c) {
				return Character.isLetterOrDigit(c) || c == '_';
			}
		}, Token.UNDEFINED);
		keywords.addWord("operator", keyword);
		keywords.addWord("internal", keyword);

		setRules(new IRule[] { new EndOfLineRule("//", comment),
				new SingleLineRule("\"", "\"", name, '\\'),
				new SingleLineRule("{$", "$}", formula),
				new SingleLineRule("{", "}", tag), keywords });
	}

	/** whether the text editor's background is dark (e.g. a dark theme) */
	private static boolean isDark() {
		final Display display = Display.getCurrent() != null ? Display
				.getCurrent() : Display.getDefault();
		final Color bg = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		final RGB rgb = bg.getRGB();
		return 0.299 * rgb.red + 0.587 * rgb.green + 0.114 * rgb.blue < 128;
	}

	/** the colours are kept in JFace's registry, which disposes them */
	private static IToken token(final String name, final boolean dark,
			final RGB forDark, final RGB forLight, final int style) {
		final String key = "de.prob.ui.ptm." + name
				+ (dark ? ".dark" : ".light");
		final ColorRegistry registry = JFaceResources.getColorRegistry();
		if (!registry.hasValueFor(key))
			registry.put(key, dark ? forDark : forLight);
		return new Token(new TextAttribute(registry.get(key), null, style));
	}
}
