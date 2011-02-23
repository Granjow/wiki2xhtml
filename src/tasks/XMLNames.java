package src.tasks;

import src.Resources;
import src.project.file.WikiFile;
import src.tasks.Tasks.Task;
import src.utilities.XMLTools;


/**
 * <p>Removes all characters that are not valid XML Name characters within <code>&lt;xmlname&gt;</code> tags.</p>
 * <p>Can e.g. be used when wanting to make sure there a no wrong characters within an XHTML property.</p>
 * @see {@link XMLTools#getXmlNameChar}
 * @see {@link Resources.XmlNames}
 */
public class XMLNames extends WikiTask {
	public Task desc() {
		return Task.XMLNames;
	}
	public WikiTask nextTask() {
		return new WikiReferences();
	}
	public void parse(WikiFile file) {
		StringBuffer out = new StringBuffer();
		StringBuffer in = file.getContent();
		int last = 0, first, second;
		while ((first = in.indexOf(Resources.xmlNameOpen, last)) >= 0) {
			out.append(in.substring(last, first));
			second = in.indexOf(Resources.xmlNameClose);
			if (second < 0) {
				System.err.printf("No closing tag found for %s in %s!\n", Resources.xmlNameOpen, file.name);
				break;
			}
			out.append(XMLTools.getXmlNameChar(in.substring(first + Resources.xmlNameOpen.length(), second)));
			last = second + Resources.xmlNameClose.length();
		}
		if (out.length() > 0) {
			out.append(in.substring(last));
			file.setContent(out);
		}
	}
	

}
