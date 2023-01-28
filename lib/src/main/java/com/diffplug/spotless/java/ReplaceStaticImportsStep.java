/*
 * Copyright 2022 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.spotless.java;

import com.diffplug.spotless.FormatterFunc;
import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.SerializableFileFilter;

import javax.print.attribute.standard.MediaSize;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ReplaceStaticImportsStep {

	private ReplaceStaticImportsStep() {}

	private static final String NAME = "replaceStaticImports";
	private static final String STATIC_IMPORTS = "import static %s;";

	public static boolean isReplaceStaticImportsStep(FormatterStep step) {
		return step.getName().equals(NAME);
	}

	public static FormatterStep create() {
		return FormatterStep.create(NAME, new State(Collections.emptySet()), State::toFormatter);
	}

	public static FormatterStep createWithClasspathSet(Set<File> classpathTargets) {
		return FormatterStep.create(NAME, new State(classpathTargets), State::toFormatter);
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		private final Set<File> classpath;

		State(Set<File> classpath) {
			this.classpath = classpath;
		}

		FormatterFunc toFormatter() {
			return State::replaceStaticImports;
		}

		private static String replaceStaticImports(String unixStr) {
			String[] lines = unixStr.split("((?<=\n))");

			for (int i = 0; i < lines.length - 1; i++) {
				String line = lines[i];
				if(line.contains(STATIC_IMPORTS)) {
					String[] items = line.split("\\.");
					String memberName = items[items.length-1].replace(";", "");
					String className = items[items.length-2];

					// If wildcard import, get all possible methods from classpath

					// Repair import without static and with class name

					// Find instances that the static import was used and append class name
				}
			}
			return unixStr;
		}

		/** Generate a map of method names to their static classes */
		private Map<String, String> staticMethodMap() {
			Map<String, String> methodMap = new HashMap<>();

			return methodMap;
		}
	}
}
