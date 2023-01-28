package com.diffplug.spotless.java;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.ResourceHarness;

import com.diffplug.spotless.SerializableEqualityTester;

import org.junit.jupiter.api.Test;

public class ReplaceStaticImportsStepTest extends ResourceHarness {

	@Test
	void basicReplaceStaticImportStepTest() throws Throwable {
		FormatterStep step = ReplaceStaticImportsStep.create();
		assertOnResources(step,
			"java/replacestaticimports/ReplaceStaticImportsSimpleUnformatted.test",
			"java/replacestaticimports/ReplaceStaticImportsSimpleFormatted.test");
	}

	@Test
	void wildcardReplaceStaticImportStepTest() throws Throwable {
		FormatterStep step = ReplaceStaticImportsStep.create();
		assertOnResources(step,
			"java/replacestaticimports/ReplaceStaticImportsWildcardUnformatted.test",
			"java/replacestaticimports/ReplaceStaticImportsWildcardFormatted.test");
	}

	@Test
	void equality() throws Exception {
		new SerializableEqualityTester() {
			@Override
			protected void setupTest(API api) { api.areDifferentThan(); }

			@Override
			protected FormatterStep create() { return ReplaceStaticImportsStep.create(); }
		}.testEquals();
	}
}
