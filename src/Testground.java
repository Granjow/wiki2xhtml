package src;


import java.io.File;
import java.io.IOException;

import src.project.WikiProject;
import src.project.WikiProject.FallbackFile;
import src.project.file.LocalWikiFile;
import src.project.file.WikiFile;
import src.utilities.IOWrite;

public class Testground {

	public static void main(String[] args) throws IOException {
		WikiProject proj = new WikiProject(".");
		FallbackFile ff = proj.locate("tplImage.txt");
		System.out.println(ff.pathInfo());
		
		File f = new File("/tmp/w2xtest/index.txt");
		IOWrite.writeString(f, "Hallo.\n" +
				"<gallery>\n" +
				"Image:test.jpg\n" +
				"Text\n" +
				"// Comment\n" +
				"</gallery>", false);
		File fo = new File("/tmp/w2xtest/index.html");
		f.mkdirs();
		WikiProject wp = new WikiProject("/tmp/w2xtest");
		WikiFile wf = new LocalWikiFile(wp, f.getName(), false, true, f, fo);
		wp.addFile(wf);
		wp.make();
	}
	
}
