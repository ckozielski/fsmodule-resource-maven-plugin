package com.espirit.ps.psci.moduleresourceplugin;

import org.junit.Test;
import junit.framework.Assert;

public class ResourceConfigurationTest {

	@Test
	public void getIdentifierTest() {
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		TestHelper.injectToPrivateField(resourceConfiguration, "identifier", "groupId:artifactId");
		Assert.assertEquals("groupId:artifactId", resourceConfiguration.getIdentifier());
	}


	@Test
	public void getScopeTest() {
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		TestHelper.injectToPrivateField(resourceConfiguration, "scope", "server");
		Assert.assertEquals("server", resourceConfiguration.getScope());
	}


	@Test
	public void getComponentsTest() {
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();

		TestHelper.injectToPrivateField(resourceConfiguration, "components", null);
		Assert.assertTrue(resourceConfiguration.getComponents().isEmpty());

		TestHelper.injectToPrivateField(resourceConfiguration, "components", "");
		Assert.assertEquals(1, resourceConfiguration.getComponents().size());
		Assert.assertTrue(resourceConfiguration.getComponents().contains(""));

		TestHelper.injectToPrivateField(resourceConfiguration, "components", "global");
		Assert.assertEquals(1, resourceConfiguration.getComponents().size());
		Assert.assertTrue(resourceConfiguration.getComponents().contains("global"));

		TestHelper.injectToPrivateField(resourceConfiguration, "components", "global,,web");
		Assert.assertEquals(2, resourceConfiguration.getComponents().size());
		Assert.assertTrue(resourceConfiguration.getComponents().contains("global"));
		Assert.assertTrue(resourceConfiguration.getComponents().contains("web"));
	}


	@Test
	public void isIsolatedTest() {
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		Assert.assertNull(resourceConfiguration.isIsolated());
		TestHelper.injectToPrivateField(resourceConfiguration, "isolated", true);
		Assert.assertTrue(resourceConfiguration.isIsolated());
		TestHelper.injectToPrivateField(resourceConfiguration, "isolated", false);
		Assert.assertFalse(resourceConfiguration.isIsolated());
	}


	@Test
	public void getPathTest() {
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		TestHelper.injectToPrivateField(resourceConfiguration, "path", "myPath/");
		Assert.assertEquals("myPath/", resourceConfiguration.getPath());
	}


	@Test
	public void toStringTest() {
		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		Assert.assertEquals("resource [identifier: null, scope: null, components: null, exclude: false, path: null]", resourceConfiguration.toString());
	}
}
