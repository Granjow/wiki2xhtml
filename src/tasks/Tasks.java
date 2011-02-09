package src.tasks;

public class Tasks {
	
	public static enum Task {
		
		Preparse ("Preparsing"),
		Settings ("Settings"),
		RemoveNowiki ("Remove Nowiki Content"),
//		Templates ("Templates"), // Already contained in the parser functions.
		ParserFunctions ("Parser Functions"),
		Tables ("Tables"),
		Headings ("Headings"),
		Images ("Images"),
		Links ("Links"),
		Lists ("Lists"),
		Galleries ("Galleries"),
		InsertNowiki ("Insert Nowiki Content"),
		XMLNames ("Convert XML names");
		
		public final String name;
		
		private Task(String name) {
			this.name = name;
		}
	}

}
