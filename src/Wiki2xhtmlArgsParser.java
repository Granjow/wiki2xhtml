/*
 *   Copyright (C) 2007-2011 Simon A. Eugster <simon.eu@gmail.com>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package src;

import java.io.File;
import java.io.PrintStream;

import src.argsFilesReader.ArgsFilesReader;
import src.project.WikiProject;
import src.project.WikiProject.InvalidLocationException;
import src.project.file.LocalWikiFile;
import src.utilities.Indent;
import jargs.gnu.CmdLineParser;

public class Wiki2xhtmlArgsParser extends CmdLineParser {

	private static final PrintStream o = System.out;
	
	public final Option inputDir;
	public final Option outputDir;
	public final Option styleDir;
	public final Option styleOutputDir;
	
	public final Option menuFile;
	public final Option commonFile;
	
	
	public final Option incremental;
//	public final Option stdout;
	
	public final Option showHelp;
	public final Option showVersion;
//	public final Option checkUpdate;

//	public final Option lang;
//	public final Option noupdatecheck;
//	public final Option verbose;
//	public final Option debug;
//	public final Option onlyCode;

	
	public Wiki2xhtmlArgsParser() {
		inputDir = addStringOption('i', "input-dir");
		inputDir.setDescription("Input directory containing all source files");
		outputDir = addStringOption('o', "output-dir");
		outputDir.setDescription("Output directory for the whole project");
		styleDir = addStringOption('s', "style");
		styleDir.setDescription("Style directory containing the style files (templates, CSS files, etc.)");
		styleOutputDir = addStringOption("style-output-dir");
		styleOutputDir.setDescription("Output directory for the style files. Must be a sub-directory of the output directory.");
		
		menuFile = addStringOption('m', "menu");
		menuFile.setDescription("Describes the menu of the page");
		commonFile = addStringOption('c', "common");
		commonFile.setDescription("File containing the common settings like the header, the homelink, etc.");
		
		incremental = addBooleanOption("incremental");
		incremental.setDescription("Re-builds changed files only. Attention: Does not consider template files; don't use this flag if templates have changed.");
//		stdout = addBooleanOption("stdout");
		
		showHelp = addBooleanOption('h', "help");
		showHelp.setDescription("Displays this help message");
		
		showVersion = addBooleanOption("version");
		showVersion.setDescription("Display the wiki2xhtml version number");
//		checkUpdate = addBooleanOption("www");
	}
	
	/**
	 * @param project If <code>null</code>, then a new project will be created.
	 */
	public WikiProject readArguments(WikiProject project, String[] args, File relativeTo) throws IllegalOptionValueException, UnknownOptionException {
		
		parse(args);
		
		String filename;
		String alternative;
		boolean success;

		if ((Boolean)getOptionValue(showHelp, Boolean.FALSE, false)) {
			System.out.println(help());
			System.exit(0);
		}
		if ((Boolean)getOptionValue(showVersion, Boolean.FALSE, false)) {
			System.out.println(version());
			System.exit(0);
		}
		
		String prefix = (relativeTo == null) ? "" : relativeTo.getAbsolutePath() + File.separator;
		
		
		// Project directory
		if (project == null) {
			project = new WikiProject((String)getOptionValue(
					inputDir, 
					Constants.Directories.workingDir,
					false
					));
			o.println("New WikiProject created.");
			o.println("Project working dir is: " + project.projectDirectory().getAbsolutePath());
		} else {
			if (getOptionValue(inputDir, false) != null) {
				project.setProjectDirectory(new File(prefix + (String)getOptionValue(inputDir, false)));
			}
		}
		
		
		project.argsParser = this;
		
		
		// Output directory
		if (project.outputDirectory() != null) {
			alternative = project.outputDirectory().getAbsolutePath();
		} else {
			alternative = Constants.Directories.workingDir + File.separator + Constants.Directories.target;
		}
		filename = (String)getOptionValue(outputDir, false);
		if (filename == null) { filename = alternative; }
		else { filename = prefix + filename; }
		success = project.setOutputDirectory(new File(filename));
		o.printf("Output directory is: %s (Success: %s)\n", project.outputDirectory().getAbsolutePath(), success);
		
		
		// Style directory
		if (project.styleDirectory() != null) {
			alternative = project.styleDirectory().getAbsolutePath();
		} else {
			alternative = project.projectDirectory().getAbsolutePath() + File.separator + Constants.Directories.style;
		}
		filename = (String)getOptionValue(styleDir, false);
		if (filename == null) { filename = alternative; }
		else { filename = prefix + filename; }
		project.setStyleDirectory(new File(filename));
		o.println("Style directory is: " + project.styleDirectory().getAbsolutePath());
		
		
		// Style output directory
		project.setStyleOutputDirectory((String)getOptionValue(styleOutputDir, project.styleOutputDirectoryName(), false));
		o.println("Style output directory is: " + project.styleOutputDirectory().getAbsolutePath());
		
		
		// Input files
		File f;
		for (String s : getRemainingArgs()) {
			
			if (s.endsWith(".args") && new File(s).exists()) {
				// .args files do not need to be prefixed! Check without first.
				f = new File(s);
			} else {
				f = new File(project.projectDirectory().getAbsolutePath() + File.separator + s);
			}
			
			if (f.exists()) {
				
				if (s.endsWith(".args")) {
					// Read the argument file
					o.println("Args file: " + f.getAbsolutePath());
					File parentFile = f.getAbsoluteFile().getParentFile(); // f.getParentFile() alone is == null for e.g. f = new File("filenameOnly.args")
					o.println("Parent file of " + f.getAbsolutePath() + ": " + (parentFile == null ? "null" : parentFile.getAbsolutePath()));
					if (!parentFile.equals(project.projectDirectory())) {
						project.setProjectDirectory(parentFile);
						o.printf("Project directory set, based on the location of the args file (%s), to %s\n", 
								f.getAbsolutePath(),
								project.projectDirectory().getAbsolutePath(),
								project.styleOutputDirectory().getAbsolutePath()
								);
					}
					ArgsFilesReader.readArgsFile(project, f);
					
				} else {
					// Add as input file
					try {
						project.addFile(new LocalWikiFile(project, s, true));
//						o.println("File added: " + f.getAbsolutePath());
					} catch (InvalidLocationException e) {
						e.printStackTrace();
						o.printf("File %s (%s) could not be added: %s\n", s, f.getAbsolutePath(), e.getMessage());
					}
					
				}
				
			} else {
				o.println("Error: " + f.getAbsolutePath() + " does not exist, not added.");
			}
		}
		
		return project;
	}
	
	public String help() {
		StringBuilder sb = new StringBuilder();
		sb.append(version());
		sb.append("Usage:\twiki2xhtml [OPTIONS] [FILES]\n\twiki2xhtml yourfile.args\n\n");
		sb.append(super.help());
		sb.append(".args File\tProject file containing all important information (files to parse, flags)");
		return Indent.indent(sb.toString(), 3);
	}
	
	public String version() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("wiki2xhtml %s (%s) (GPL)\n(c) Simon A. Eugster <simon.eu@gmail.com>\n", Constants.Wiki2xhtml.versionNumber, Constants.Wiki2xhtml.versionDate));
		return sb.toString();
		
	}
	
}
