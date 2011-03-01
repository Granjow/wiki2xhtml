package src;


import java.io.File;
import java.io.IOException;

import src.project.FallbackFile;
import src.project.WikiProject;
import src.project.WikiProject.InvalidLocationException;
import src.project.WikiProject.InvalidOutputDirectoryLocationException;
import src.project.file.LocalWikiFile;
import src.project.file.WikiFile;
import src.utilities.IOWrite;

public class Testground {

	public static void main(String[] args) throws IOException, InvalidLocationException, InvalidOutputDirectoryLocationException {
		WikiProject proj = new WikiProject(".");
		FallbackFile ff = proj.locate("tplImage.txt");
		System.out.println(ff.pathInfo());
		
		File f = new File("/tmp/w2xtest/index.txt");
		IOWrite.writeString(f, "Hallo.<ref>Das ist ein ''schräger'' Test.</ref>\n" +
				"<gallery>\n" +
				"Image:test.jpg\n" +
				"Text\n" +
				"// Comment\n" +
				"</gallery>\n" +
				"<references/>", false);
		f.mkdirs();
		WikiProject wp = new WikiProject("/tmp/w2xtest");
		WikiFile wf = new LocalWikiFile(wp, f.getName(), false);
		wp.addFile(wf);
		wp.make();
	}
	
}
