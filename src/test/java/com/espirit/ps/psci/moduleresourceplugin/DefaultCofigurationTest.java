package com.espirit.ps.psci.moduleresourceplugin;

import org.junit.Assert;
import org.junit.Test;

public class DefaultCofigurationTest {

	private static final Object DEFAULT_SCOPE = TestHelper.getPrivateField(new DefaultConfiguration(), "DEFAULT_SCOPE");
	private static final Object DEFAULT_ISOLATED = TestHelper.getPrivateField(new DefaultConfiguration(), "DEFAULT_ISOLATED");
	private static final Object DEFAULT_COMPONENTS = TestHelper.getPrivateField(new DefaultConfiguration(), "DEFAULT_COMPONENTS");
	private static final Object DEFAULT_PATH = TestHelper.getPrivateField(new DefaultConfiguration(), "DEFAULT_PATH");


	@Test
	public void getScopeTest() {
		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
		TestHelper.injectToPrivateField(defaultConfiguration, "scope", null);
		Assert.assertEquals(DEFAULT_SCOPE, defaultConfiguration.getScope());

		TestHelper.injectToPrivateField(defaultConfiguration, "scope", "");
		Assert.assertEquals(DEFAULT_SCOPE, defaultConfiguration.getScope());

		TestHelper.injectToPrivateField(defaultConfiguration, "scope", "server");
		Assert.assertEquals("server", defaultConfiguration.getScope());
	}


	@Test
	public void getComponentsTest() {
		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
		TestHelper.injectToPrivateField(defaultConfiguration, "components", null);
		Assert.assertEquals(DEFAULT_COMPONENTS, defaultConfiguration.getComponents());

		TestHelper.injectToPrivateField(defaultConfiguration, "components", "");
		Assert.assertTrue(defaultConfiguration.getComponents().isEmpty());

		TestHelper.injectToPrivateField(defaultConfiguration, "components", "global");
		Assert.assertEquals(1, defaultConfiguration.getComponents().size());
		Assert.assertTrue(defaultConfiguration.getComponents().contains("global"));

		TestHelper.injectToPrivateField(defaultConfiguration, "components", "global,,web");
		Assert.assertEquals(2, defaultConfiguration.getComponents().size());
		Assert.assertTrue(defaultConfiguration.getComponents().contains("global"));
		Assert.assertTrue(defaultConfiguration.getComponents().contains("web"));
	}


	@Test
	public void isIsolatedTest() {
		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
		TestHelper.injectToPrivateField(defaultConfiguration, "isolated", null);
		Assert.assertEquals(DEFAULT_ISOLATED, defaultConfiguration.isIsolated());

		TestHelper.injectToPrivateField(defaultConfiguration, "isolated", true);
		Assert.assertTrue(defaultConfiguration.isIsolated());
	}


	@Test
	public void getPathTest() {
		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
		TestHelper.injectToPrivateField(defaultConfiguration, "path", null);
		Assert.assertEquals(DEFAULT_PATH, defaultConfiguration.getPath());

		TestHelper.injectToPrivateField(defaultConfiguration, "path", "");
		Assert.assertEquals("", defaultConfiguration.getPath());

		TestHelper.injectToPrivateField(defaultConfiguration, "path", "myPath/");
		Assert.assertEquals("myPath/", defaultConfiguration.getPath());
	}


	@Test
	public void toStringTest() {
		DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
		Assert.assertEquals(String.format("default [scope: %s, isolated: %s, path: %s, components: %s]", DEFAULT_SCOPE, DEFAULT_ISOLATED, DEFAULT_PATH, DEFAULT_COMPONENTS), defaultConfiguration.toString());
	}

}
