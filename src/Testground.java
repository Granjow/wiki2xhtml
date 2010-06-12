package src;

import src.project.WikiProject;
import src.project.WikiProject.FallbackFile;
import src.project.WikiProject.FallbackFile.NoFileFoundException;

public class Testground {

	public static void main(String[] args) throws NoFileFoundException {
		WikiProject proj = new WikiProject(".");
		FallbackFile ff = proj.locate("tplImage.txt");
		System.out.println(ff.pathInfo());
		System.out.println("abc".hashCode());
		System.out.println("abc".hashCode());
		System.out.println(GenerateID.getMD5("abc"));
		System.out.println(GenerateID.md5sum("abc"));
	}
	
}
