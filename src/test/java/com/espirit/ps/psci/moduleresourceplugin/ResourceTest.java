package com.espirit.ps.psci.moduleresourceplugin;

import org.junit.Test;
import junit.framework.Assert;

public class ResourceTest {

	@Test
	public void getIdentifierTest() {
		Resource resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "identifier", "groupId:artifactId");
		Assert.assertEquals("groupId:artifactId", resourceConfiguration.getIdentifier());
	}


	@Test
	public void getScopeTest() {
		Resource resourceConfiguration = new Resource();
		resourceConfiguration.setScope("server");
		Assert.assertEquals("server", resourceConfiguration.getScope());
	}


	@Test
	public void getComponentsTest() {
		Resource resourceConfiguration;
		resourceConfiguration = new Resource();

		TestHelper.injectToPrivateField(resourceConfiguration, "components", null);
		Assert.assertTrue(resourceConfiguration.getComponents().isEmpty());

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "components", "");
		Assert.assertEquals(1, resourceConfiguration.getComponents().size());
		Assert.assertTrue(resourceConfiguration.getComponents().contains(""));

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "components", "global");
		Assert.assertEquals(1, resourceConfiguration.getComponents().size());
		Assert.assertTrue(resourceConfiguration.getComponents().contains("global"));

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "components", "global,,web");
		Assert.assertEquals(2, resourceConfiguration.getComponents().size());
		Assert.assertTrue(resourceConfiguration.getComponents().contains("global"));
		Assert.assertTrue(resourceConfiguration.getComponents().contains("web"));
	}


	@Test
	public void isIsolatedTest() {
		Resource resourceConfiguration = new Resource();
		Assert.assertNull(resourceConfiguration.isIsolated());
		TestHelper.injectToPrivateField(resourceConfiguration, "isolated", true);
		Assert.assertTrue(resourceConfiguration.isIsolated());
		TestHelper.injectToPrivateField(resourceConfiguration, "isolated", false);
		Assert.assertFalse(resourceConfiguration.isIsolated());
	}


	@Test
	public void getPathTest() {
		Resource resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "path", "myPath/");
		Assert.assertEquals("myPath/", resourceConfiguration.getPath());
	}


	@Test
	public void toStringTest() {
		Resource resourceConfiguration = new Resource();
		Assert.assertEquals("resource [identifier: null, scope: null, components: null, exclude: false, path: null, minVersion: null, maxVersion: null]", resourceConfiguration.toString());
	}


	@Test
	public void hashCodeTest() {
		Resource resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "identifier", "groupId:artifactId");
		Assert.assertEquals("groupId:artifactId".hashCode(), resourceConfiguration.hashCode());
	}


	@Test
	public void equalsTest() {
		Resource resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "identifier", "groupId:artifactId");
		Assert.assertFalse(resourceConfiguration.equals(this));

		Resource resourceConfiguration2 = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration2, "identifier", "groupId2:artifactId2");
		Assert.assertFalse(resourceConfiguration.equals(resourceConfiguration2));

		Resource resourceConfiguration3 = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration3, "identifier", "groupId:artifactId");
		Assert.assertTrue(resourceConfiguration.equals(resourceConfiguration3));
	}
}
