package src;


import java.io.IOException;

import src.project.WikiProject;
import src.project.WikiProject.FallbackFile;
import src.utilities.Base64;

public class Testground {

	public static void main(String[] args) throws IOException {
		WikiProject proj = new WikiProject(".");
		FallbackFile ff = proj.locate("tplImage.txt");
		System.out.println(ff.pathInfo());
		System.out.println("abc".hashCode());
		System.out.println("abc".hashCode());
		System.out.println(GenerateID.getMD5("abc"));
		System.out.println(GenerateID.md5sum("abc"));
		
		System.out.println(Base64.encodeBytes("abc".getBytes()));
		System.out.println(new String(Base64.decode("YWJj")));
	}
	
}
