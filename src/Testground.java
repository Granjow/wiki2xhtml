package src;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import src.project.FallbackFile;
import src.project.WikiProject;
import src.project.WikiProject.InvalidLocationException;
import src.project.WikiProject.InvalidOutputDirectoryLocationException;
import src.project.file.LocalWikiFile;
import src.project.file.WikiFile;
import src.utilities.IOWrite;

@SuppressWarnings("unused")
public class Testground {

	public static void main(String[] args) throws IOException, InvalidLocationException, InvalidOutputDirectoryLocationException {
		Date now = new Date();
		System.out.println(now.toString());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd E hh:mm:ss");
		System.out.println(formatter.format(now));
	}
	
	private void sampleProject() throws IOException, InvalidLocationException, InvalidOutputDirectoryLocationException {
		WikiProject proj = new WikiProject(".");
		FallbackFile ff = proj.locateDefault("tplImage.txt", null);
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
