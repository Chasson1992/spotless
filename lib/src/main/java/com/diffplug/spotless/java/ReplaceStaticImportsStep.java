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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ReplaceStaticImportsStep {

	private ReplaceStaticImportsStep() {}

	private static final String NAME = "replaceStaticImports";
	private static final String STATIC_IMPORTS = "import static";

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
			return unixStr -> replaceStaticImports(unixStr, classpath);
		}

		private String replaceStaticImports(String unixStr, Set<File> classpath) {
			String[] lines = unixStr.split("((?<=\n))");

			// Map of static method names to their references
			Map<String, String> methodMap = new HashMap<>();

			for (int i = 0; i < lines.length - 1; i++) {
				String line = lines[i];

				if(line.startsWith(STATIC_IMPORTS)) {

					String[] items = line.split("\\.");
					String fullyQualifiedName = line.replace(STATIC_IMPORTS, "").replace(";", "");
					String methodName = items[items.length-1].replace(";", "");
					String className = items[items.length-2];

					if (methodName.contains("*")) {
						methodMap.putAll(buildClasspathMethodMap(classpath, fullyQualifiedName, className));
					}

					methodMap.put(methodName, className);


					// Repair import without static and with class name

					// Find instances that the static import was used and append class name
				}
			}

			System.out.println("Method map =====> \n" + methodMap);
			return unixStr;
		}

		private Map<String,String> buildClasspathMethodMap(Set<File> classpath, String fullyQualifiedName, String className) {
			HashMap<String, String> map = new HashMap<>();

			// Format the FQN to match Jar file entry
			fullyQualifiedName =
				fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.')).trim();
			String formattedFQN = fullyQualifiedName.replace('.', '/') + ".class";

			for (File file : classpath) {
				if (!file.getName().endsWith(".jar")) continue;

				try ( JarFile jar = new JarFile(file);
					  URLClassLoader classLoader = new URLClassLoader( new URL[]{ file.toURI().toURL() }) ){

					JarEntry classFile = jar.getJarEntry(formattedFQN);
					if (classFile != null) {
						Class<?> clazz = classLoader.loadClass(fullyQualifiedName);
						Method[] methods = clazz.getMethods();
						for (int i = 0; i < methods.length -1; i++) {
							map.put(methods[i].getName(), className);
						}
					}
				} catch (IOException e) {
					throw new RuntimeException("Could not load the Jar file for wildcard imports located at "
						+ file.getAbsolutePath());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}

			return map;
		}
	}
}
