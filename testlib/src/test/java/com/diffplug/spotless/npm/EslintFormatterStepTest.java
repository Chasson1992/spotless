/*
 * Copyright 2016-2022 DiffPlug
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
package com.diffplug.spotless.npm;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.diffplug.common.collect.ImmutableMap;
import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.StepHarnessWithFile;
import com.diffplug.spotless.TestProvisioner;
import com.diffplug.spotless.tag.NpmTest;

@NpmTest
class EslintFormatterStepTest {

	private final Map<String, String> combine(Map<String, String> m1, Map<String, String> m2) {
		Map<String, String> combined = new TreeMap<>(m1);
		combined.putAll(m2);
		return combined;
	}

	@NpmTest
	@Nested
	class EslintJavascriptFormattingStepTest extends NpmFormatterStepCommonTests {

		private final Map<String, Map<String, String>> devDependenciesForRuleset = ImmutableMap.of(
				"custom_rules", EslintFormatterStep.defaultDevDependenciesForTypescript(),
				"styleguide/airbnb", combine(EslintFormatterStep.defaultDevDependencies(), EslintFormatterStep.PopularStyleGuide.JS_AIRBNB.devDependencies()),
				"styleguide/google", combine(EslintFormatterStep.defaultDevDependencies(), EslintFormatterStep.PopularStyleGuide.JS_GOOGLE.devDependencies()),
				"styleguide/standard", combine(EslintFormatterStep.defaultDevDependencies(), EslintFormatterStep.PopularStyleGuide.JS_STANDARD.devDependencies()),
				"styleguide/xo", combine(EslintFormatterStep.defaultDevDependencies(), EslintFormatterStep.PopularStyleGuide.JS_XO.devDependencies()));

		@ParameterizedTest(name = "{index}: eslint can be applied using ruleset {0}")
		@ValueSource(strings = {"custom_rules", "styleguide/airbnb", "styleguide/google", "styleguide/standard", "styleguide/xo"})
		void formattingUsingRulesetsFile(String ruleSetName) throws Exception {
			String filedir = "npm/eslint/javascript/" + ruleSetName + "/";

			String testDir = "formatting_ruleset_" + ruleSetName.replace('/', '_') + "/";
			//			File testDirFile = newFolder(testDir);

			final File eslintRc = createTestFile(filedir + ".eslintrc.js");
			//			final File eslintRc = setFile(buildDir().getPath() + "/.eslintrc.js").toResource(filedir + ".eslintrc.js");

			final String dirtyFile = filedir + "javascript-es6.dirty";
			File dirtyFileFile = setFile(testDir + "test.js").toResource(dirtyFile);
			final String cleanFile = filedir + "javascript-es6.clean";

			final FormatterStep formatterStep = EslintFormatterStep.create(
					devDependenciesForRuleset.get(ruleSetName),
					TestProvisioner.mavenCentral(),
					projectDir(),
					buildDir(),
					npmPathResolver(),
					new EslintConfig(eslintRc, null));

			try (StepHarnessWithFile stepHarness = StepHarnessWithFile.forStep(formatterStep)) {
				stepHarness.testResource(dirtyFileFile, dirtyFile, cleanFile);
			}
		}
	}

	@NpmTest
	@Nested
	class EslintTypescriptFormattingStepTest extends NpmFormatterStepCommonTests {

		private final Map<String, Map<String, String>> devDependenciesForRuleset = ImmutableMap.of(
				"custom_rules", EslintFormatterStep.defaultDevDependenciesForTypescript(),
				"styleguide/standard_with_typescript", combine(EslintFormatterStep.defaultDevDependenciesForTypescript(), EslintFormatterStep.PopularStyleGuide.TS_STANDARD_WITH_TYPESCRIPT.devDependencies()),
				"styleguide/xo", combine(EslintFormatterStep.defaultDevDependenciesForTypescript(), EslintFormatterStep.PopularStyleGuide.TS_XO_TYPESCRIPT.devDependencies()));

		@ParameterizedTest(name = "{index}: eslint can be applied using ruleset {0}")
		@ValueSource(strings = {"custom_rules", "styleguide/standard_with_typescript", "styleguide/xo"})
		void formattingUsingRulesetsFile(String ruleSetName) throws Exception {
			String filedir = "npm/eslint/typescript/" + ruleSetName + "/";

			String testDir = "formatting_ruleset_" + ruleSetName.replace('/', '_') + "/";
			//			File testDirFile = newFolder(testDir);

			final File eslintRc = createTestFile(filedir + ".eslintrc.js");
			//			final File eslintRc = setFile(buildDir().getPath() + "/.eslintrc.js").toResource(filedir + ".eslintrc.js");

			//setFile(testDir + "/test.ts").toResource(filedir + "typescript.dirty");
			File tsconfigFile = null;
			if (existsTestResource(filedir + "tsconfig.json")) {
				tsconfigFile = setFile(testDir + "tsconfig.json").toResource(filedir + "tsconfig.json");
			}
			final String dirtyFile = filedir + "typescript.dirty";
			File dirtyFileFile = setFile(testDir + "test.ts").toResource(dirtyFile);
			final String cleanFile = filedir + "typescript.clean";

			final FormatterStep formatterStep = EslintFormatterStep.create(
					devDependenciesForRuleset.get(ruleSetName),
					TestProvisioner.mavenCentral(),
					projectDir(),
					buildDir(),
					npmPathResolver(),
					new EslintTypescriptConfig(eslintRc, null, tsconfigFile));

			try (StepHarnessWithFile stepHarness = StepHarnessWithFile.forStep(formatterStep)) {
				stepHarness.testResource(dirtyFileFile, dirtyFile, cleanFile);
			}
		}
	}

	@NpmTest
	@Nested
	class EslintInlineConfigTypescriptFormattingStepTest extends NpmFormatterStepCommonTests {

		@Test
		void formattingUsingInlineXoConfig() throws Exception {
			String filedir = "npm/eslint/typescript/standard_rules_xo/";

			String testDir = "formatting_ruleset_xo_inline_config/";

			final String esLintConfig = String.join("\n",
					"{",
					"	env: {",
					"		browser: true,",
					"		es2021: true,",
					"	},",
					"	extends: 'xo/browser',",
					"	overrides: [",
					"		{",
					"			extends: [",
					"				'xo-typescript',",
					"			],",
					"			files: [",
					"				'*.ts',",
					"				'*.tsx',",
					"			],",
					"		},",
					"	],",
					"	parser: '@typescript-eslint/parser',",
					"	parserOptions: {",
					"		ecmaVersion: 'latest',",
					"		sourceType: 'module',",
					"		project: './tsconfig.json',",
					"	},",
					"	rules: {",
					"	},",
					"}");

			File tsconfigFile = setFile(testDir + "tsconfig.json").toResource(filedir + "tsconfig.json");
			final String dirtyFile = filedir + "typescript.dirty";
			File dirtyFileFile = setFile(testDir + "test.ts").toResource(dirtyFile);
			final String cleanFile = filedir + "typescript.clean";

			final FormatterStep formatterStep = EslintFormatterStep.create(
					combine(EslintFormatterStep.PopularStyleGuide.TS_XO_TYPESCRIPT.devDependencies(), EslintFormatterStep.defaultDevDependenciesForTypescript()),
					TestProvisioner.mavenCentral(),
					projectDir(),
					buildDir(),
					npmPathResolver(),
					new EslintTypescriptConfig(null, esLintConfig, tsconfigFile));

			try (StepHarnessWithFile stepHarness = StepHarnessWithFile.forStep(formatterStep)) {
				stepHarness.testResource(dirtyFileFile, dirtyFile, cleanFile);
			}
		}
	}
}
