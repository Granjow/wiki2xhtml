package src;

import com.jdotsoft.jarloader.JarClassLoader;

/**
 * This class is only necessary when wiki2xhtml is in a jar file.
 * As wiki2xhtml is using the gettext commons library, which is
 * a jar file, and accessing jar files in jar files isn't possible
 * with Java right now, a separate class loader is necessary.
 */
public class LauncherForJar {

	/**
	 * Start the main method
	 */
	public static void main(String[] args) {
		JarClassLoader jcl = new JarClassLoader();
		try {
			jcl.invokeMain("src.UserInterface", args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}