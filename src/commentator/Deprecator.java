package src.commentator;
import src.project.file.WikiFile;

public class Deprecator {

	public Deprecator() {}
	
	public boolean onlySummary = true;
	
	private StringBuffer allRemovedForTemplate = new StringBuffer(); 
	
	public void removedForTemplateMessage(String warning, WikiFile source) {
		if (!onlySummary) {
			System.err.println("In " + source.projectAbsoluteName() + ":");
			System.err.println("UNSUPPORTED: " + warning);
			System.err.println("Please use arguments via the templating function directly.");
		}
		
		allRemovedForTemplate.append("In " + source.projectAbsoluteName() + ": " + warning + "\n");
	}
	
	public void printAllWarnings()
	{
		System.err.println("The following arguments are now UNSUPPORTED in wiki2xhtml and templates:\n" + allRemovedForTemplate);
	}
	

}
