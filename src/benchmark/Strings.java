package src.benchmark;

import src.utilities.StringTools;

public class Strings {
	
	private static final int it = 100000;
	
	private static final StringBuffer sb4096 = new StringBuffer("Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. " +
			"Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben des Alphabets enthalten -" +
			" man nennt diese Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige " +
			"Satzteile eingebaut (AVAIL® and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel fast jede " +
			"Schrift gut aus. Quod errat demonstrandum. Seit 1975 fehlen in den meisten Testtexten die Zahlen, weswegen nach TypoGb. 204 § ab dem Jahr 2034 Zahlen in 86 der " +
			"Texte zur Pflicht werden. Nichteinhaltung wird mit bis zu 245 € oder 368 $ bestraft. Genauso wichtig in sind mittlerweile auch Âçcèñtë, die in neueren Schriften " +
			"aber fast immer enthalten sind. Ein wichtiges aber schwierig zu integrierendes Feld sind OpenType-Funktionalitäten. Je nach Software und Voreinstellungen können " +
			"eingebaute Kapitälchen, Kerning oder Ligaturen (sehr pfiffig) nicht richtig dargestellt werden. Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben " +
			"da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben " +
			"des Alphabets enthalten - man nennt diese Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte " +
			"auch fremdsprachige Satzteile eingebaut (AVAIL® and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel " +
			"fast jede Schrift gut aus. Quod errat demonstrandum. Seit 1975 fehlen in den meisten Testtexten die Zahlen, weswegen nach TypoGb. 204 § ab dem Jahr 2034 Zahlen in 86 " +
			"der Texte zur Pflicht werden. Nichteinhaltung wird mit bis zu 245 € oder 368 $ bestraft. Genauso wichtig in sind mittlerweile auch Âçcèñtë, die in neueren Schriften aber " +
			"fast immer enthalten sind. Ein wichtiges aber schwierig zu integrierendes Feld sind OpenType-Funktionalitäten. Je nach Software und Voreinstellungen können eingebaute " +
			"Kapitälchen, Kerning oder Ligaturen (sehr pfiffig) nicht richtig dargestellt werden. Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie " +
			"sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben des Alphabets enthalten - " +
			"man nennt diese Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige Satzteile " +
			"eingebaut (AVAIL® and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel fast jede Schrift gut aus. Quod " +
			"errat demonstrandum. Seit 1975 fehlen in den meisten Testtexten die Zahlen, weswegen nach TypoGb. 204 § ab dem Jahr 2034 Zahlen in 86 der Texte zur Pflicht werden. " +
			"Nichteinhaltung wird mit bis zu 245 € oder 368 $ bestraft. Genauso wichtig in sind mittlerweile auch Âçcèñtë, die in neueren Schriften aber fast immer enthalten sind. " +
			"Ein wichtiges aber schwierig zu integrierendes Feld sind OpenType-Funktionalitäten. Je nach Software und Voreinstellungen können eingebaute Kapitälchen, Kerning oder " +
			"Ligaturen (sehr pfiffig) nicht richtig dargestellt werden. Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal " +
			"benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben des Alphabets enthalten - man nennt diese " +
			"Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige Satzteile eingebaut (AVAIL® " +
			"and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel fast jede Schrift gut aus. Quod errat demonstrandum. " +
			"Seit 1975 fehle");
	
	public static void testSubstring(int offset, int len, int vary) {
		System.out.printf("Offset: %d, Length: %d, Vary by: %d, Total length: %d\nIterations: %d\n", 
				offset, len, vary, sb4096.length(), it);
		long start, end;
		@SuppressWarnings("unused")
		StringBuffer sb;
		start = System.currentTimeMillis();
		for (int i = 0; i < it; i++) {
			sb = new StringBuffer(sb4096.subSequence(offset+i%vary, offset+len+i%vary));
		}
		end = System.currentTimeMillis();
		System.out.printf("StringBuffer to StringBuffer with subSequence: %s\n", StringTools.formatTimeMilliseconds(end-start));
		

		start = System.currentTimeMillis();
		for (int i = 0; i < it; i++) {
			sb = new StringBuffer(sb4096.substring(offset+i%vary, offset+len+i%vary));
		}
		end = System.currentTimeMillis();
		System.out.printf("StringBuffer to StringBuffer with substring: %s\n", StringTools.formatTimeMilliseconds(end-start));
		
		@SuppressWarnings("unused")
		String s;
		start = System.currentTimeMillis();
		for (int i = 0; i < it; i++) {
			s = sb4096.subSequence(offset+i%vary, offset+len+i%vary).toString();
		}
		end = System.currentTimeMillis();
		System.out.printf("StringBuffer to String with subSequence: %s\n", StringTools.formatTimeMilliseconds(end-start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < it; i++) {
			s = sb4096.substring(offset+i%vary, offset+len+i%vary);
		}
		end = System.currentTimeMillis();
		System.out.printf("StringBuffer to String with substring: %s\n", StringTools.formatTimeMilliseconds(end-start));
		
		System.out.println();
	}
	
	public static void createInstances() {
		long start, end;
		
		start = System.currentTimeMillis();
		for (int i = 0; i < it*100; i++) {
			new String();
		}
		end = System.currentTimeMillis();
		System.out.printf("new String(): %s\n", StringTools.formatTimeMilliseconds(end-start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < it*100; i++) {
			new StringBuffer();
		}
		end = System.currentTimeMillis();
		System.out.printf("new StringBuffer(): %s\n", StringTools.formatTimeMilliseconds(end-start));
	}
	
	public static void main(String[] args) {
		createInstances();
//		testSubstring(1234, 1111, 1000);
//		testSubstring(1234, 111, 100);
	}

}
