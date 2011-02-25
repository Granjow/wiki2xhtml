package src;


import java.io.IOException;

import src.project.WikiProject;
import src.project.WikiProject.InvalidOutputDirectoryLocationException;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;




public class Main {

	
	public static void main(String[] args) throws IllegalOptionValueException, UnknownOptionException, IOException, InvalidOutputDirectoryLocationException {

		WikiProject project = null;
		
		project = new Wiki2xhtmlArgsParser().readArguments(project, args);
		assert project != null;
		
		project.make();
		
		
	}
	
}
