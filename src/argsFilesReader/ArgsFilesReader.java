package src.argsFilesReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import src.Constants;
import src.Container_Files;
import src.Handler_Arguments;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.utilities.IORead_Stats;

public class ArgsFilesReader {
	
	private final CommentAtor ca = CommentAtor.getInstance();
	
	public ArgsFilesReader() { }
	
	/**
	 * <p>Reads settings from a file.</p>
	 * <dl>
	 * <dt>Comments</dt><dd>Start with a <code>#</code> as first character on the line.
	 * <dt>Arguments</dt><dd>Line starting with <code>args=</code></dd>
	 * <dt>Source directory</dt><dd>Line starting with <code>dir=</code>.
	 * Attention: Setting the source directory does not work for the files listed in 
	 * this config file. The <code>dir=</code> is valid for all files below 
	 * util a new <code>dir=</code> entry is met.</dd>
	 * <dt>Files</dt><dd>Any other line. The filename is the first entry in the line 
	 * (without spaces). Possible additional arguments: See {@link Constants.Arguments.FileArgs}.</dd>
	 * <dt>Examples</dt><dd><code>secret.txt nositemap</dd><dd><code>.htaccess nobuild nositemap</dd> 
	 * </dl>
	 * @param filename
	 * @return Arguments given in the file
	 * @since wiki2xhtml 2.4
	 * TODO 0 Doc arg list
	 */
	public String read(String filename) {
		String args = "";
		String dir = "";
		String file;
		boolean sitemap;
		boolean parse;
		
		try {
			StringBuffer content = IORead_Stats.readSBuffer(new File(filename));
			BufferedReader b = new BufferedReader(new StringReader(content.toString()));
			String line;
			
			ca.ol("Reading args file %s.\n", CALevel.V_MSG, filename);
			
			for (line = b.readLine(); line != null; line = b.readLine()) {
				if (line.startsWith("#")) {
					// Comment.
					continue;
				}
				if (line.startsWith("args=")) {
					args += " " + line.substring("args=".length());
					ca.ol("Arguments read: %s\n", CALevel.V_MSG, args);
				} else if (line.startsWith("dir=")) {
					dir = line.substring("dir=".length()).trim() + File.separatorChar;
					ca.ol("Dir: " + dir, CALevel.V_MSG);
				} else {
					// File.
					ca.ol("File read: %s\n", CALevel.V_MSG, line);
					sitemap = true;
					parse = true;
					
					String[] largs = line.split("\\s", 2);
					if (largs.length > 0) {
						file = dir + largs[0];
						ca.ol("File is %s\n", CALevel.V_MSG, file);
						if (largs.length > 1) {
							if (largs[1].contains(Constants.Arguments.FileArgs.noSitemap)) {
								sitemap = false;
								ca.ol("No sitemap entry for %s\n", CALevel.MSG, file);
							}
							if (largs[1].contains(Constants.Arguments.FileArgs.noParse)) {
								parse = false;
								ca.ol("Will not parse %s\n", CALevel.MSG, file);
							}
						}
						Container_Files.getInstance().addFile(file, sitemap, parse);
					}
				}
			}
			
		} catch (IOException e) {
			return null;
		}
		
		args = Handler_Arguments.handleReplaceArguments(args);
		ca.ol("Added arguments from %s: %s\n", CALevel.MSG, filename, args);
		return args;
	}

}
