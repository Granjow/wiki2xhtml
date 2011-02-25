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
import jargs.gnu.CmdLineParser;

public class Wiki2xhtmlArgsParser extends CmdLineParser {

	private static final PrintStream o = System.out;
	
	public final Option inputDir;
	public final Option outputDir;
	public final Option styleDir;
	public final Option styleOutputDir;
	
	public final Option menuFile;
	public final Option commonFile;
	public final Option footerFile;
	
	
	public final Option incremental;
	public final Option stdout;
	
	public final Option showHelp;
	public final Option checkUpdate;

//	public final Option generateHelpfiles;
//	public final Option lang;
//	public final Option noupdatecheck;
//	public final Option updateMenuFile;
//	public final Option gui;
//	public final Option verbose;
//	public final Option debug;
//	public final Option onlyCode;
//	public final Option removeLinebreaks;
//	public final Option standardSettings;

	
	public Wiki2xhtmlArgsParser() {
		inputDir = addStringOption('i', "input-dir");
		outputDir = addStringOption('o', "output-dir");
		styleDir = addStringOption('s', "style");
		styleOutputDir = addStringOption("style-output-dir"); // Must be relative to the project directory
		
		menuFile = addStringOption('m', "menu");
		commonFile = addStringOption('c', "common");
		footerFile = addStringOption('f', "footer");
		
//		generateHelpfiles = addStringOption("helpfiles");
		
		incremental = addBooleanOption("incremental");
		stdout = addBooleanOption("stdout");
		
		showHelp = addBooleanOption('h', "help");
		checkUpdate = addBooleanOption("www");
	}
	
	/**
	 * @param project If <code>null</code>, then a new project will be created.
	 */
	public WikiProject readArguments(WikiProject project, String[] args) throws IllegalOptionValueException, UnknownOptionException {
		
		parse(args);
		
		String alternative;
		boolean success;
		
		
		// Project directory
		if (project == null) {
			project = new WikiProject((String)getOptionValue(
					inputDir, 
					Constants.Directories.workingDir
					));
			o.println("New WikiProject created.");
			o.println("Project working dir is: " + project.projectDirectory().getAbsolutePath());
		} else {
			if (getOptionValue(inputDir) != null) {
				project.setProjectDirectory(new File((String)getOptionValue(inputDir)));
			}
		}
		
		
		// Output directory
		if (project.outputDirectory() != null) {
			alternative = project.outputDirectory().getAbsolutePath();
		} else {
			alternative = Constants.Directories.workingDir + File.separator + Constants.Directories.target;
		}
		success = project.setOutputDirectory(new File((String)getOptionValue(
				outputDir, 
				alternative
				)));
		o.printf("Output directory is: %s (Success: %s)\n", project.outputDirectory().getAbsolutePath(), success);
		
		
		// Style directory
		if (project.styleDirectory != null) {
			alternative = project.styleDirectory.getAbsolutePath();
		} else {
			alternative = project.projectDirectory().getAbsolutePath() + File.separator + Constants.Directories.style;
		}
		project.styleDirectory = new File((String)getOptionValue(
				styleDir, 
				alternative
				));
		o.println("Style directory is: " + project.styleDirectory.getAbsolutePath());
		
		
		// Style output directory
		project.setStyleOutputDirectory((String)getOptionValue(styleOutputDir, project.styleOutputDirectoryName()));
		o.println("Style output directory is: " + project.styleOutputDirectory().getAbsolutePath());
		
		
		// Input files
		File f;
		for (String s : getRemainingArgs()) {
			
			f = new File(project.projectDirectory().getAbsolutePath() + File.separator + s);
			
			if (f.exists()) {
				
				if (s.endsWith(".args")) {
					// Read the argument file
					o.println("Args file: " + f.getAbsolutePath());
					ArgsFilesReader.readArgsFile(project, f);
					
				} else {
					// Add as input file
					try {
						project.addFile(new LocalWikiFile(project, s, true, true));
						o.println("File added: " + f.getAbsolutePath());
					} catch (InvalidLocationException e) {
						e.printStackTrace();
						o.printf("File %s (%s) could not be added: %s\n", s, f.getAbsolutePath(), e.getMessage());
					}
					
				}
				
			} else {
				o.println("Error: " + f.getAbsolutePath() + " does not exist, not added.");
			}
		}
		
		System.out.println("Footer: " + getOptionValue(footerFile));
		
		return project;
	}
	
}
