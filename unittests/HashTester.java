package unittests;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import src.utilities.FileChangesMap;
import src.utilities.IOWrite;

public class HashTester extends junit.framework.TestCase {

	
	@Test
	public void testUnchanged() throws IOException {
		File dir = buildTempDir();
		File f1 = buildTempFile(dir);
		IOWrite.writeString(f1, "Unchanged", false);

		FileChangesMap map = new FileChangesMap(dir, ".hashes");
		assertFalse(map.queryUnchanged(f1.getName()));
		map.update(f1.getName());
		assertTrue(map.queryUnchanged(f1.getName()));
		
		map = new FileChangesMap(dir, ".hashes");
		assertTrue(map.queryUnchanged(f1.getName()));
	}
	
	@Test
	public void testChanged() throws IOException {
		File dir = buildTempDir();
		File f1 = buildTempFile(dir);
		File f2 = buildTempFile(dir);
		IOWrite.writeString(f1, "Unchanged", false);
		IOWrite.writeString(f2, "Unchanged", false);

		FileChangesMap map = new FileChangesMap(dir, ".hashes");
		assertFalse(map.queryUnchanged(f1.getName()));
		map.update(f1.getName());
		assertTrue(map.queryUnchanged(f1.getName()));
		map.update(f2.getName());
		assertTrue(map.queryUnchanged(f2.getName()));
		
		IOWrite.writeString(f2, "Changed", false);
		assertFalse(map.queryUnchanged(f2.getName()));
		
		map = new FileChangesMap(dir, ".hashes");
		assertTrue(map.queryUnchanged(f1.getName()));
		assertFalse(map.queryUnchanged(f2.getName()));
	}
	
	@Test
	public void testIncludeUnchanged() throws IOException {
		File dir = buildTempDir();
		File f1 = buildTempFile(dir);
		File f2 = buildTempFile(dir);
		IOWrite.writeString(f1, "Unchanged", false);
		IOWrite.writeString(f2, "Unchanged", false);

		FileChangesMap map = new FileChangesMap(dir, ".hashes");
		map.updateInclude(f1.getName(), f2.getName());
		map.update(f1.getName());
		assertFalse(map.queryUnchanged(f1.getName()));
		map.updateIncludedHashes();
		assertTrue(map.queryUnchanged(f1.getName()));
		
		map = new FileChangesMap(dir, ".hashes");
		assertTrue(map.queryUnchanged(f1.getName()));
		System.gc();
	}
	
	@Test
	public void testIncludeChanged() throws IOException {
		File dir = buildTempDir();
		File f1 = buildTempFile(dir);
		File f2 = buildTempFile(dir);
		IOWrite.writeString(f1, "Unchanged", false);
		IOWrite.writeString(f2, "Unchanged", false);

		FileChangesMap map = new FileChangesMap(dir, ".hashes");
		map.updateInclude(f1.getName(), f2.getName());
		map.update(f1.getName());
		map.updateIncludedHashes();
		assertTrue(map.queryUnchanged(f1.getName()));
		
		map = new FileChangesMap(dir, ".hashes");
		assertTrue(map.queryUnchanged(f1.getName()));
		IOWrite.writeString(f2, "Changed", false);
		assertFalse(map.queryUnchanged(f1.getName()));
		
		IOWrite.writeString(f2, "Changed again", false);
		assertFalse(map.queryUnchanged(f1.getName()));
		map.updateInclude(f1.getName(), f2.getName());
		assertFalse(map.queryUnchanged(f1.getName()));
		map.updateIncludedHashes();
		assertTrue(map.queryUnchanged(f1.getName()));
		
	}

	
	private static final File buildTempFile(final File dir) throws IOException {
		File f = File.createTempFile("myfile", "ext", dir);
		f.deleteOnExit();
		return f;
	}
	
	private static final File buildTempDir() throws IOException {
		File f = File.createTempFile("wiki2xhtml", "dir");
		f.delete();
		f.mkdir();
		f.deleteOnExit();
		return f;
	}

}
