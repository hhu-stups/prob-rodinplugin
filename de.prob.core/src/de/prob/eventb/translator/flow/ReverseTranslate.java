package de.prob.eventb.translator.flow;

import java.util.HashMap;

public class ReverseTranslate {

	public static class Symbol {

		public final String combo;
		public final String unicode;

		public Symbol(final String string, final String string2) {
			this.combo = string;
			this.unicode = string2;
		}

	}

	static Symbol[] x = new Symbol[] { new Symbol("|>>", "\u2a65"),
			new Symbol("|>", "\u25b7"), new Symbol("\\/", "\u222a"),
			new Symbol("/\\", "\u2229"), new Symbol("|->", "\u21a6"),
			new Symbol("-->", "\u2192"), new Symbol("/<<:", "\u2284"),
			new Symbol("/<:", "\u2288"), new Symbol("/:", "\u2209"),
			new Symbol("<=>", "\u21d4"), new Symbol("=>", "\u21d2"),
			new Symbol("&", "\u2227"), new Symbol("!", "\u2200"),
			new Symbol("#", "\u2203"), new Symbol("/=", "\u2260"),
			new Symbol("<=", "\u2264"), new Symbol(">=", "\u2265"),
			new Symbol("<<:", "\u2282"), new Symbol("<:", "\u2286"),
			new Symbol("<<->>", "\ue102"), new Symbol("<<->", "\ue100"),
			new Symbol("<->>", "\ue101"), new Symbol("<->", "\u2194"),
			new Symbol(">->>", "\u2916"), new Symbol("+->", "\u21f8"),
			new Symbol(">+>", "\u2914"), new Symbol(">->", "\u21a3"),
			new Symbol("+>>", "\u2900"), new Symbol("->>", "\u21a0"),
			new Symbol("{}", "\u2205"), new Symbol("\\", "\u2216"),
			new Symbol("**", "\u00d7"), new Symbol("<+", "\ue103"),
			new Symbol("><", "\u2297"), new Symbol("||", "\u2225"),
			new Symbol("~", "\u223c"), new Symbol("<<|", "\u2a64"),
			new Symbol("<|", "\u25c1"), new Symbol("%", "\u03bb"),
			new Symbol("..", "\u2025"), new Symbol(".", "\u00b7"),
			new Symbol("-", "\u2212"), new Symbol("*", "\u2217"),
			new Symbol("/", "\u00f7"), new Symbol(":=", "\u2254"),
			new Symbol("::", ":\u2208"), new Symbol(":|", ":\u2223"),
			new Symbol(":", "\u2208"), new Symbol("|", "\u2223"),
			new Symbol("NAT", "\u2115"), new Symbol("POW", "\u2119"),
			new Symbol("INT", "\u2124"), new Symbol("INTER", "\u22c2"),
			new Symbol("UNION", "\u22c3"), new Symbol("or", "\u2228"),
			new Symbol("not", "\u00ac"), new Symbol("true", "\u22a4"),
			new Symbol("false", "\u22a5"), new Symbol("circ", "\u2218"),
			new Symbol("oftype", "\u2982") };
	private static final HashMap<String, String> translationMap = new HashMap<String, String>();

	static {
		for (Symbol symbol : x) {
			translationMap.put(symbol.unicode, symbol.combo);
		}
	}

	public static String reverseTranslate(final String input) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			String s = "" + input.charAt(i);
			final String replacement = translationMap.get(s);
			if (replacement != null) {
				result.append(replacement);
			} else {
				result.append(s);
			}
		}
		return result.toString();
	}
}
