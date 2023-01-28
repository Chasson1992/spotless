package com.diffplug.spotless.java;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.ResourceHarness;

import com.diffplug.spotless.SerializableEqualityTester;

import com.diffplug.spotless.StepHarness;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ReplaceStaticImportsStepTest extends ResourceHarness {

	@Test
	void basicReplaceStaticImportStepTest() {
		FormatterStep step = ReplaceStaticImportsStep.create();
		StepHarness.forStep(step).testResource(
			"java/replacestaticimports/ReplaceStaticImportsSimpleUnformatted.test",
			"java/replacestaticimports/ReplaceStaticImportsSimpleFormatted.test");
	}

	@Test
	void wildcardReplaceStaticImportStepTest() {
		String[] classpathStrings = System.getProperty("java.class.path").split(File.pathSeparator);
		Set<File> jarFiles = Arrays.stream(classpathStrings).map(File::new).collect(Collectors.toSet());

		FormatterStep step = ReplaceStaticImportsStep.createWithClasspathSet(jarFiles);
		StepHarness.forStep(step).testResource(
			"java/replacestaticimports/ReplaceStaticImportsWildcardUnformatted.test",
			"java/replacestaticimports/ReplaceStaticImportsWildcardFormatted.test");
	}

	@Test
	void equality() {
		new SerializableEqualityTester() {
			@Override
			protected void setupTest(API api) { api.areDifferentThan(); }

			@Override
			protected FormatterStep create() { return ReplaceStaticImportsStep.create(); }
		}.testEquals();
	}
}
