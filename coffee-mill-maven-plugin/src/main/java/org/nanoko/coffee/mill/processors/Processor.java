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

import java.io.File;
import java.util.Map;

public interface Processor {


    public void configure(AbstractCoffeeMillMojo millMojo, Map<String, Object> options);

    public void processAll() throws ProcessorException;

    public void tearDown();

    public boolean accept(File file);

    public void fileCreated(File file) throws ProcessorException;

    public void fileUpdated(File file) throws ProcessorException;

    public void fileDeleted(File file) throws ProcessorException;

    class ProcessorException extends Exception {

		private static final long serialVersionUID = 1421637223171144784L;
		
		public ProcessorException(String message) {
            super(message);
        }
        public ProcessorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class ProcessorWarning {
        public final File file;
        public final int line;
        public final int character;
        public final String evidence;
        public final String reason;

        public ProcessorWarning(File file, int line, int character, String evidence, String reason) {
            this.file = file;
            this.line = line;
            this.character = character;
            this.evidence = evidence;
            this.reason = reason;
        }
    }
}
