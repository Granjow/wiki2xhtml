package src.project;

import src.Constants;
import src.project.FallbackFile.NoFileFoundException;
import src.utilities.IOUtils;

public class WikiStyle {

	public final WikiProject _project;
	
	public WikiStyle(WikiProject project) {
		_project = project;
	}
	
	public final FallbackFile pageTemplate() throws NoFileFoundException {
		return _project.locate(Constants.Templates.sTplPage);
	}
	
	public final void copyFiles() {
		IOUtils.copyWithRsync(_project.styleDirectory(), _project.styleOutputDirectory(), String.format("(%s->%s)   ", _project.projectDirectory().getName(), _project.styleOutputDirectory().getName()));
	}
	
}
