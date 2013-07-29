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

import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffee.mill.utils.OptionsHelper;
import org.nanoko.coffee.mill.utils.node.NPM;

import java.io.File;
import java.util.Map;

/**
 * Processor handling CoffeeScript to JavaScript compilation.
 * It handles <tt>.coffee</tt> files.
 */
public class NodeBasedCoffeeScriptCompilationProcessor extends DefaultProcessor {


	private File source;
	private File destination;

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

        new NPM.Install(mojo).install("coffee-script", "1.6.3");
    }

	public boolean accept(File file) {
		return isFileContainedInDirectory(file, source)  && file.getName().endsWith(".coffee")  && file.isFile();
	}


	@Override
	public void processAll() throws ProcessorException {
		if (! source.exists()) {
			return;
		}
        new NPM.Execution(mojo).npm("coffee-script").command("coffee").args("--compile", "--map", "--output",
                destination.getAbsolutePath(), source.getAbsolutePath()).execute();
	}

	private File getOutputJSFile(File input) {
		String jsFileName = input.getName().substring(0, input.getName().length() - ".coffee".length()) + ".js";
		String path = input.getParentFile().getAbsolutePath().substring(source.getAbsolutePath().length());
		return new File(destination, path + "/" + jsFileName);
	}

	private void compile(File file) throws ProcessorException {
		File out = getOutputJSFile(file);
		getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());

        new NPM.Execution(mojo).npm("coffee-script").command("coffee").args("--compile", "--map", "--output",
                out.getParentFile().getAbsolutePath(), file.getAbsolutePath()).execute();
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
}
