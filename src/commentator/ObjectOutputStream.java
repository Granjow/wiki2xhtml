package src.commentator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import src.commentator.Logger.PrintScope;

public class ObjectOutputStream extends PrintStream {

	public ObjectOutputStream() {
		super(System.out);
	}
	
	private ObjectOutputStream(OutputStream arg0) {
		super(arg0);
	}
	
	public static void main(String[] args) {
		try {
		JFrame jf = new JFrame();
		final JTextArea jaf = new JTextArea();
		jf.getContentPane().add(jaf);
		jf.setVisible(true);
		jaf.setText("hallo.");
		jf.pack();
		jf.setSize(400, 400);
		
		ObjectOutputStream oos = new ObjectOutputStream();
		oos.print("halloæ¼");
		
		PrintStream jps = new PrintStream(new OutputStream() {
			public void write(int arg0) throws IOException {
				System.out.println("Char as int: " + arg0);
				char[] c = Character.toChars(arg0);
				System.out.print("Char tochar: ");
				for (char cc : c) { System.out.print(cc); }
				System.out.println();
				jaf.append(Character.toChars(arg0).toString()); //((char) arg0)+""
			}
		});
		
		File f = File.createTempFile("test", ".txt");
		f.deleteOnExit();
		PrintStream ps = new PrintStream(f);
		System.err.println(f.getAbsolutePath());
		
		Logger.attachOutput(jps, PrintScope.OUT);
		Logger.attachOutput(oos, PrintScope.OUT);
		Logger.attachOutput(System.err, PrintScope.ERR);
		Logger.attachOutput(ps, PrintScope.OUT);
		Logger.print("abc¼æ}");
		System.in.read();
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
}
