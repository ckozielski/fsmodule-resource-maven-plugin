package com.espirit.ps.psci.moduleresourceplugin;

import java.util.Collections;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;

public class GenerateModuleTest {

	// @Test
	public void executeTest() {
		try {
			new GenerateModule().execute();
			Assert.assertTrue(true);
		} catch (MojoExecutionException e) {
			Assert.assertFalse(false);
		}
	}


	@Test
	public void collectComponentsTest() {
		GenerateModule generateModule = new GenerateModule();
		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
		TestHelper.injectToPrivateField(generateModule, "defaultConfiguration", defaultConfiguration);
		TestHelper.injectToPrivateField(generateModule, "resources", Collections.emptyList());
		Assert.assertFalse(generateModule.collectComponents().isEmpty());

		TestHelper.injectToPrivateField(defaultConfiguration, "components", "");
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		TestHelper.injectToPrivateField(generateModule, "resources", Collections.singletonList(resourceConfiguration));
		Assert.assertTrue(generateModule.collectComponents().isEmpty());
	}


	@Test
	public void createEmptyResources() {
		Map<String, String> emptyResources;

		emptyResources = GenerateModule.createEmptyResources(Collections.emptySet());
		Assert.assertTrue(emptyResources.isEmpty());

		emptyResources = GenerateModule.createEmptyResources(Collections.singleton("global"));
		Assert.assertEquals(3, emptyResources.size());
		Assert.assertTrue(emptyResources.containsKey("global.isolated"));
		Assert.assertEquals("", emptyResources.get("global.isolated"));
		Assert.assertTrue(emptyResources.containsKey("global.legacy"));
		Assert.assertEquals("", emptyResources.get("global.legacy"));
		Assert.assertTrue(emptyResources.containsKey("global.web"));
		Assert.assertEquals("", emptyResources.get("global.web"));
		// GenerateModule generateModule = new GenerateModule();
		// Map<String, String> emptyValues = new TreeMap<>();
		// for (String component : components) {
		// String legacyComponent = String.format("%s.legacy", component);
		// emptyValues.put(legacyComponent, "");
		// String isolatedComponent = String.format("%s.isolated", component);
		// emptyValues.put(isolatedComponent, "");
		// String webComponent = String.format("%s.web", component);
		// emptyValues.put(webComponent, "");
		// }
		// return emptyValues;
	}

}
