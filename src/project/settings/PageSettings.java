package src.project.settings;

import src.Constants.SettingsE;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;

public class PageSettings extends Settings<SettingsE, String> {

	public void append_(final SettingsE property, final String value) {
		if (contains(property)) {
			set_(property, get_(property) + value);
		} else {
			set_(property, value);
		}
	}
	
	public String nullValue() {
		return "null";
	}
	
	boolean valid(SettingsE property, String value) {
		boolean ok = true;
		// Manage special cases
		switch (property) {
		case galleryImagesPerLine: //TODO really here?
			try {
				// Don't use negative values
				byte b = Byte.parseByte(value);
				if (b < 0) {
					throw new NegativeValueException(value);
				}
			} catch (NumberFormatException e) {
				ok = false;
				e.printStackTrace();
				CommentAtor.getInstance().ol("Number format exception with " + value + ": " + e.getMessage(), CALevel.ERRORS);
			} catch (NegativeValueException e) {
				ok = false;
				CommentAtor.getInstance().ol(e.getMessage(), CALevel.ERRORS);
			}
			break;
		}
		return ok;
	}
	

	public static class NegativeValueException extends Exception {
		private static final long serialVersionUID = 1L;

		public NegativeValueException(String s) {
			super("Negative value obtained: " + s);
		}
	}
}
