package src.tasks;

public class Tasks {
	
	public static enum Task {
		
		Links ("Links"),
		Lists ("Lists");
		
		public final String name;
		
		private Task(String name) {
			this.name = name;
		}
	}

}
