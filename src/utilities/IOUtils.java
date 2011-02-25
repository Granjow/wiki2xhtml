package src.utilities;

import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.util.regex.*;


/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

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
 *
 */

/**
 *
 * Various input/output utilities.
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class IOUtils {
	
	
	public static final boolean copyWithRsync(File src, File dst) {
		
		boolean worked = false;

		File resourcesFile = new File(src.getAbsolutePath() + File.separator + "resources.txt");
		if (resourcesFile.exists() && resourcesFile.isFile()) {
			System.out.printf("Reading files to copy from resources file (%s) ...\n", resourcesFile.getAbsolutePath());
			try {
				dst.mkdirs();
				String cmd = String.format("rsync -uav %s %s --include-from=%s", 
						src.getAbsolutePath() + File.separator, 
						dst.getAbsolutePath() + File.separator, 
						resourcesFile.getAbsolutePath());
				System.out.println("Copying style files: " + cmd);
				Process rsync = Runtime.getRuntime().exec(cmd);
				
				try {
					rsync.waitFor();
					InputStream stdout = rsync.getInputStream();
					InputStreamReader outReader = new InputStreamReader(stdout);
					BufferedReader br = new BufferedReader(outReader);
					String line;
					while ( (line = br.readLine()) != null) {
						System.out.println("\t" + line);
					}
					br = new BufferedReader(new InputStreamReader(rsync.getErrorStream()));

					while ( (line = br.readLine()) != null) {
						System.err.println("\t" + line);
					}
					
					worked = rsync.exitValue() == 0;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("Please check whether rsync is installed!");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.printf("Resources file (%s) does not exist, not copying additional files.\n", resourcesFile.getAbsolutePath());
		}
		
		return worked;
	}

	public static boolean copyFile(InputStream is, OutputStream os) {
		boolean ok = true;
		try {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

		} catch (IOException e) {
			System.err.println(e);
			ok = false;
		}

		/* also executed when error occurred */
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
		return ok;
	}
	
	/**
	 * See #copy(File, File, java.io.FileFilter). Copies only one file/directory.
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copy(final File src, final File dest) throws IOException {
		copy(src, dest, new java.io.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return false;
			}
		});
	}
	
	/**
	 * Copies files and directories. To copy recursively edit the FileFilter to return true for directories.
	 * @since wiki2xhtml 3.4
	 * 
	 * @param src
	 * @param target
	 * @param fileFilter
	 */
	public static void copy(final File src, final File target, final java.io.FileFilter fileFilter) throws IOException {
		
		if (src.isFile()) {
			File dest;
			if (target.exists() && target.isDirectory()) {
				dest = new File(target.getAbsolutePath() + File.separator + target.getName());
			} else {
				dest = target;
			}
			copyFile(src, dest);
		} else if (src.isDirectory()) {
			// Create target directory
			if (!target.exists()) target.mkdirs();
			
			for (File f : src.listFiles(fileFilter)) {
				copy(f, new File(target.getAbsolutePath() + File.separator + f.getName()), fileFilter);
			}
		} else {
			throw new IOException(src.getAbsolutePath() + " is neither file nor directory.");
		}
	}

	/**
	 * Copies a file
	 *
	 * @param in Input file
	 * @param out Output file
	 * @return 0 with no errors, otherwise -1
	 */
	private static boolean copyFile(File in, File out) {
		boolean ok = true;
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(in);
			os = new FileOutputStream(out);
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

		} catch (IOException e) {
			System.err.println(e);
			ok = false;
		}

		/* also executed when error occurred */
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
		return ok;
	}

	/**
	 * Removes an extension from a file
	 *
	 * @param fExt -
	 *            File to remove extension from
	 * @return File without an extension (from last point on)
	 */
	public static File removeFileExtension(File fExt) {
		File noExtension = null;
		String filename = fExt.getName();

		int dotPos = filename.lastIndexOf('.'); // get position of last point in
		// Filename
		if (dotPos <= 0) {
			noExtension = fExt; // No '.' found, in fact no extension
		} else {
			noExtension = new File(fExt.getParentFile(), filename.substring(0, dotPos));
			// getParentFile: returns file's path
		}

		return noExtension;
	}

	/**
	 * Returns the file extension.
	 *
	 * @param filename
	 * @return
	 */
	public static String getFileExtensionS(String filename) {
		String ext = new String();

		int dotPos = filename.lastIndexOf('.');
		if (dotPos > 0) {
			ext = filename.substring(dotPos, filename.length());
		}

		return ext;
	}

	/**
	 * Adds an extension to a File.
	 *
	 * @param file File to add the extension
	 * @param ext Extension, with or without .
	 * @return File with extension added to filename, if necessary
	 */
	public static File addFileExtension(File file, String ext) {
		if (file == null) {
			return null;
		}

		File withExtension = null;

		String filename = file.getName();

		if (filename.endsWith(ext)) {
			return file;
		} else {
			if (ext.startsWith(".") && filename.endsWith("."))
				filename = filename + ext.substring(1, ext.length());
			else { // less than two dots in filename and extension
				if (ext.startsWith(".") || filename.endsWith("."))
					filename = filename + ext;
				else
					// no dot neither in filename nor in extension, so add it
					filename = filename + '.' + ext;
			}
			withExtension = new File(file.getParentFile(), filename);
		}

		return withExtension;
	}

	/**
	 * @param extensions available extension
	 * @param directory the working directory (null for default)
	 * @param title the title (null for default)
	 * @param append append selected extension
	 * @return The selected file or null
	 */
	public static File openInFileDialog(String extension, File directory, String title, boolean append) {
		String[] s = new String[1];
		s[0] = extension;
		if (append) {
			return addFileExtension(openInFileDialog(s, directory, title, append), extension);
		} else {
			return openInFileDialog(s, directory, title, append);
		}
	}

	/**
	 * @param extensions available extensions
	 * @param directory the working directory (null for default
	 * @param title the title (null for default)
	 * @param append append selected extension
	 * @return The selected file or null
	 */
	public static File openInFileDialog(String[] extensions, File directory, String title, boolean append) {
		JFileChooser jfc = new JFileChooser();
		if (title == null) {
			title = new String("Open File");
		}
		jfc.setDialogTitle(title);
		if (directory != null && directory.exists() && directory.isDirectory()) {
			jfc.setCurrentDirectory(directory);
		}
		for (int i = 0; i < extensions.length; i++) {
			final String ext = extensions[i];
			jfc.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory()
						   || f.getName().toLowerCase().endsWith(ext);
				}

				public String getDescription() {
					if (ext.startsWith(".")) {
						return new StringBuffer("*" + ext).toString();
					} else {
						return new StringBuffer("*." + ext).toString();
					}
				}
			});
		}
		int returnValue = jfc.showOpenDialog(null); // showSaveDialog()
		// Opens a file choice dialogue

		File datei;

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			// File selected
			datei = jfc.getSelectedFile();

			String ext = new String();
			String filter = jfc.getFileFilter().getDescription();
			Pattern pat;
			Matcher mat;
			boolean found = false;

			if (append) {
				for (int i = 0; i < extensions.length; i++) {
					pat = Pattern.compile(extensions[i]);
					mat = pat.matcher(filter);
					if (mat.find()) {
						found = true;
						ext = extensions[i];
						break;
					}
				}

				System.out.println(found);
				if (found) {
					datei = addFileExtension(datei, ext);
				}
			}

			return datei;
		} else {
			return null;
		}
	}

	/**
	 * @author Simon Eugster
	 * @param append -
	 *            append selected extension
	 * @return selected file
	 */
	public static File openInFileDialog(boolean append) {
		String[] s = { ".txt", ".html" };
		return openInFileDialog(s, null, null, append);
	}


	/**
	 * @param extension - availible extension
	 * @param directory - the working directory (null for default
	 * @param title - the title (null for default)
	 * @param append - append selected extension
	 * @return The selected file or null
	 */
	public static File openOutFileDialog(String extension, File directory, String title, boolean append) {
		String[] s = new String[1];
		s[0] = extension;
		if (append) {
			File f = openOutFileDialog(s, directory, title, append);
			if (f != null && !f.exists()) {
				f = addFileExtension(f, extension);
			}
			return f;
		} else {
			return openOutFileDialog(s, directory, title, append);
		}
	}

	/**
	 * @param extensions - available extensions
	 * @param directory - the working directory (null for default
	 * @param title - the title (null for default)
	 * @param append - append selected extension
	 * @return The selected file or null
	 */
	public static File openOutFileDialog(String[] extensions, File directory, String title, boolean append) {
		JFileChooser jfc = new JFileChooser();
		if (title == null) {
			title = new String("Save File");
		}
		jfc.setDialogTitle("Save File");
		jfc.setDialogType(JFileChooser.SAVE_DIALOG);
		if (directory != null && directory.exists() && directory.isDirectory()) {
			jfc.setCurrentDirectory(directory);
		}

		for (int i = 0; i < extensions.length; i++) {
			final String ext = extensions[i];
			jfc.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory()
						   || f.getName().toLowerCase().endsWith(ext);
				}

				public String getDescription() {
					if (ext.startsWith(".")) {
						return new StringBuffer("*" + ext).toString();
					} else {
						return new StringBuffer("*." + ext).toString();
					}
				}
			});
		}

		int returnVal = jfc.showSaveDialog(null); // showSaveDialog()

		File datei;

		if (returnVal == JFileChooser.APPROVE_OPTION) { // File selected
			datei = jfc.getSelectedFile();

			String ext = new String();
			String filter = jfc.getFileFilter().getDescription();
			Pattern pat;
			Matcher mat;
			boolean found = false;

			if (append) {
				for (int i = 0; i < extensions.length; i++) {
					pat = Pattern.compile(extensions[i]);
					mat = pat.matcher(filter);
					if (mat.find()) {
						found = true;
						ext = extensions[i];
						break;
					}
				}

//				o.println(found);
				if (found) {
					datei = addFileExtension(datei, ext);
				}
			}

			return datei;
		} else {
			return null;
		}
	}

	/**
	 * @param append - Append selected Extension or not
	 * @return selected file
	 */
	public static File openOutFileDialog(boolean append, File directory) {
		String[] s = { ".html", ".txt" };
		return openOutFileDialog(s, directory, null, append);
	}

	/**
	 * Returns the short path name and eventually removes a part of the path
	 * (usually java.class.dir)
	 *
	 * @param f
	 * @param remove
	 * @return
	 */
	public static String getShortPath(File f, String remove) {
		StringBuffer shortPath = new StringBuffer();

		try {
			shortPath = new StringBuffer(f.getCanonicalPath());
		} catch (IOException e) {
			System.err.println(e);
		}

		if (remove.length() > 0) {
			if (shortPath.toString().startsWith(remove)) {
				shortPath.delete(0, remove.length());
			}
		}

		return shortPath.toString();
	}

	/**
	 * Checks whether a file is binary by looking for null-Bytes.
	 *
	 * @param f - input file to check
	 * @return true, if it is a binary file
	 */
	public static boolean binaryCheck(File f) {
		boolean bin = false;

		try {
			BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(f));
			byte[] b = new byte[4096];
			int nBytes;
			tryLoop: do {
				nBytes = bis.read(b, 0, b.length);
				for (int i = 0; i < nBytes; i++) {
					if (b[i] == 0) {
						bin = true;
						break tryLoop;
					}
				}
			} while (nBytes > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bin;
	}
}