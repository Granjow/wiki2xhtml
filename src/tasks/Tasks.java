package src.tasks;

public class Tasks {
	
	public static enum Task {
		
		Preparse ("Preparser", "Preparsing"),
		Settings ("Settings", "Settings"),
		RemoveNowiki ("RemoveNowiki", "Remove Nowiki Content"),
//		Templates ("Templates"), // Already contained in the parser functions.
		ParserFunctions ("ParserFunctions", "Parser Functions"),
		Tables ("Tables", "Tables"),
		Headings ("Headings", "Headings"),
		Images ("Images", "Images"),
		Links ("Links", "Links"),
		Lists ("Lists", "Lists"),
		Galleries ("Galleries", "Galleries"),
		Formattings ("Formattings", "Formattings"),
		Paragraphs ("Paragraphs", "Create Paragraphs"),
		InsertNowiki ("InsertNowiki", "Insert Nowiki Content"),
		XMLNames ("XMLNames", "Convert XML names"),
		References ("References", "References"),
		PageTemplate ("PageTemplate", "Page template"),
		Cleanup ("Cleanup", "Cleanup");
		
		public final String name;
		public final String description;
		
		private Task(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}

}
