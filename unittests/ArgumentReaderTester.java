package unittests;

import src.argumentHandler.*;

import junit.framework.TestCase;

public class ArgumentReaderTester extends TestCase {
	
	public void testReadArguments() {
		new TestObject("a=b", "a", "b");
		new TestObject("firstArg", "0", "firstArg");
		new TestObject("secondArg", "1", "secondArg", 1);
		new TestObject("hello world = no argument", "0", "hello world = no argument");
		new TestObject(" keySpaced  =  argSpaced   ", "keySpaced", "argSpaced   ");
	}
	
	private class TestObject {
		
		private ArgumentItem ai;
		
		TestObject(String input, String key, String value) {
			this(input, key, value, 0);
		}
		TestObject(String input, String key, String value, int index) {
			ai = new ArgumentItem(input, index);
			assertEquals(key, ai.name);
			assertEquals(value, ai.argument);
			
		}
	}

}
