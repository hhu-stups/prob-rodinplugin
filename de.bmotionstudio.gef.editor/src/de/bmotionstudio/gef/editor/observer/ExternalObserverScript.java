/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.core.resources.IFile;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverExternalObserverScript;

public class ExternalObserverScript extends Observer {

	public static String ID = "de.bmotionstudio.gef.editor.observer.ExternalObserverScript";

	private transient GroovyObject groovyObject;

	private String scriptPath;

	// private String language;

	@Override
	public void check(Animation animation, BControl control) {

		try {

			if (groovyObject == null) {
				IFile pFile = control.getVisualization().getProjectFile();
				String myPath = (pFile.getProject().getLocation() + "/" + scriptPath)
						.replace("file:", "");
				ClassLoader parent = getClass().getClassLoader();
				GroovyClassLoader loader = new GroovyClassLoader(parent);
				Class<?> groovyClass;
				groovyClass = loader.parseClass(new File(myPath));
				// let's call some method on an instance
				groovyObject = (GroovyObject) groovyClass.newInstance();
			}

			Object[] args = { animation, control };
			groovyObject.invokeMethod("check", args);

		} catch (CompilationFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ObserverWizard getWizard(BControl control) {
		return new WizardObserverExternalObserverScript(control, this);
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

}
