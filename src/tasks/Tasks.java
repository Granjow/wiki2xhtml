package src.tasks;

public class Tasks {
	
	public static enum Task {
		
		Preparse ("Preparsing"),
		Settings ("Settings"),
		RemoveNowiki ("Remove Nowiki Content"),
		Links ("Links"),
		Lists ("Lists"),
		InsertNowiki ("Insert Nowiki Content");
		
		public final String name;
		
		private Task(String name) {
			this.name = name;
		}
	}

}
