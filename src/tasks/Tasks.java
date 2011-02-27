package src.tasks;

public class Tasks {
	
	public static enum Task {
		
		Preparse ("Preparser", "Preparsing"),
		Settings ("Settings", "Settings"),
		RemoveNowiki ("RemoveNowiki", "Remove Nowiki Content (<nowiki> tags)"),
		TOC ("TOC", "Table of Contents ({{:tplTOC.txt|...}})"),
		ParserFunctions ("ParserFunctions", "Parser Functions and templates ({{#if:...|...}})"),
		Tables ("Tables", "Tables ({| ... |}"),
		Headings ("Headings", "Headings (== Heading ==)"),
		Images ("Images", "Images ([[Image:...]])"),
		Links ("Links", "Links ([[example.html Example]]) and Anchors (~~there~~)"),
		Lists ("Lists", "Lists (* ...)"),
		Galleries ("Galleries", "Galleries (<gallery> tags)"),
		Formattings ("Formattings", "Formattings (Bold, Italic ...)"),
		Tags ("Tags", "Tags ({{$Version}} ...)"),
		Paragraphs ("Paragraphs", "Create Paragraphs"),
		InsertNowiki ("InsertNowiki", "Insert Nowiki Content (<nowiki> tags)"),
		XMLNames ("XMLNames", "Convert text between <xmlname> tags to XML names"),
		References ("References", "References (<ref> and <references/> tags)"),
		PageTemplate ("PageTemplate", "Page template"),
		Cleanup ("Cleanup", "Cleanup"),
		;
		
		public final String name;
		public final String description;
		
		private Task(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}

}
