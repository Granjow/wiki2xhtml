package src.tasks;

public class Tasks {
	
	public static enum Task {
		
		Preparse ("Preparsing"),
		Settings ("Settings"),
		RemoveNowiki ("Remove Nowiki Content"),
		Templates ("Templates"),
		ParserFunctions ("Parser Functions"),
		Tables ("Tables"),
		Headings ("Headings"),
		Images ("Images"),
		Links ("Links"),
		Lists ("Lists"),
		InsertNowiki ("Insert Nowiki Content"),
		XMLNames ("Convert XML names");
		
		public final String name;
		
		private Task(String name) {
			this.name = name;
		}
	}

}
