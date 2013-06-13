/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.processors;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.RhinoException;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.OptionsHelper;
import org.nanoko.coffee.mill.utils.RhinoLauncher;
import org.nanoko.coffee.mill.utils.SlimedRhinoLauncher;

/**
 * Processor handling CoffeeScript to JavaScript compilation.
 * It handles <tt>.coffee</tt> files.
 */
public class CoffeeScriptCompilationProcessor extends DefaultProcessor {


	private File source;
	private File destination;
	private File coffeescript;

	public void tearDown() {
		// Do nothing.
	}

	@Override
	public void configure(AbstractCoffeeMillMojo mojo, Map<String, Object> options) {
		super.configure(mojo, options);
		if (OptionsHelper.getBoolean(options, "test", false)) {
			this.source = mojo.coffeeScriptTestDir;
			this.destination = mojo.getWorkTestDirectory();
		} else {
			this.source = mojo.coffeeScriptDir;
			this.destination = mojo.getWorkDirectory();
		}

		coffeescript = OptionsHelper.getFile(options, "coffeescript");
		if (coffeescript == null || ! coffeescript.isFile()) {
			throw new IllegalArgumentException("The coffeeescript file must exist");
		}
	}

	public boolean accept(File file) {
		return isFileContainedInDirectory(file, source)  && file.getName().endsWith(".coffee")  && file.isFile();
	}


	@Override
	public void processAll() throws ProcessorException {
		if (! source.exists()) {
			return;
		}
		Collection<File> files = FileUtils.listFiles(source, new String[]{"coffee"}, true);
		for (File file : files) {
			if (file.isFile()) {
				compile(file);
			}
		}
	}

	private File getOutputJSFile(File input) {
		String jsFileName = input.getName().substring(0, input.getName().length() - ".coffee".length()) + ".js";
		String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
		return new File(destination, path + "/" + jsFileName);
	}

	private void compile(File file) throws ProcessorException {
		File out = getOutputJSFile(file);
		getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
		try {
			final String data = FileUtils.readFileToString(file);
			final SlimedRhinoLauncher builder = initScriptBuilder();
			final String compileScript = String.format("CoffeeScript.compile(%s, %s);",
					RhinoLauncher.toJSMultiLineString(data),
					"{}"); // No options
			final String result = (String) builder.evaluate(compileScript, "CoffeeScript.compile");
			FileUtils.write(out, result);
		} catch (RhinoException jse) {
			throw new ProcessorException("Compilation Error in " + file.getName() + "@" + jse.lineNumber() +
					" - " + jse.details());
		} catch (IOException e) {
			throw new ProcessorException("Cannot compile " + file.getAbsolutePath(), e);
		}
	}

	@Override
	public void fileCreated(File file) throws ProcessorException {
		compile(file);
	}

	@Override
	public void fileUpdated(File file) throws ProcessorException {
		compile(file);
	}

	@Override
	public void fileDeleted(File file) {
		File theFile = getOutputJSFile(file);
		if (theFile.exists()) {
			theFile.delete();
		}
	}

	/**
	 * Initialize script builder for evaluation.
	 */
	private SlimedRhinoLauncher initScriptBuilder() {
		return SlimedRhinoLauncher.getDaBeast();
	}
}
