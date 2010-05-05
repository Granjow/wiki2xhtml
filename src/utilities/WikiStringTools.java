package src.utilities;

public final class WikiStringTools {

	public static String disableWikilinks(String s) {
		s = s.replace("[", "&#91;");
		s = s.replace("]", "&#93;");
		return s;
	}

}
