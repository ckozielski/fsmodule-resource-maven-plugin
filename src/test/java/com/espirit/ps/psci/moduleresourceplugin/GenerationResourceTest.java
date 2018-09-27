package com.espirit.ps.psci.moduleresourceplugin;

import org.junit.Assert;
import org.junit.Test;

public class GenerationResourceTest {

	@Test
	public void getIdentifierTest() {
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), null);
		Assert.assertEquals("groupId:artifactId", resource.getIdentifier());
	}


	@Test
	public void getDependencyScopeTest() {
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), null);
		Assert.assertEquals("compile", resource.getDependencyScope());
	}


	@Test
	public void getResourceConfigurationTest() {
		Resource resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "identifier", "groupId:artifactId");
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("groupId:artifactId", resource.getResourceConfiguration().getIdentifier());
	}


	@Test
	public void getResourceString() {
		GenerationResource resource;
		Resource resourceConfiguration;

		resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), null);
		Assert.assertNull(resource.getResourceString("global", true, true));

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "path", null);
		resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertNull(resource.getResourceString("global", true, true));

		TestHelper.injectToPrivateField(resourceConfiguration, "path", "lib/my-0.8.15.jar");
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/my-0.8.15.jar</resource>%n"), resource.getResourceString("global", false, false));

		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" isolated=\"true\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, true));

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "exclude", Boolean.TRUE);
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertNull(resource.getResourceString("global", false, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" isolated=\"true\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, true));
		Assert.assertNull(resource.getResourceString("global", true, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", true, true));

		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", true, true));

		resourceConfiguration = new Resource();
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", true, true));

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "components", "web");
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertNull(resource.getResourceString("global", false, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("web", false, false));

		resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), null);
		Assert.assertNull(resource.getResourceString(true, true));

		resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(resourceConfiguration, "path", null);
		resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertNull(resource.getResourceString(true, true));

		TestHelper.injectToPrivateField(resourceConfiguration, "path", "lib/my-0.8.15.jar");
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\" minVersion=\"0.8.15\">lib/my-0.8.15.jar</resource>%n"), resource.getResourceString(false, false));
	}


	@Test
	public void toStringTest() {
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals("resource [identifier: groupId:artifactId, scope:  scope=\"module\", isolated: , version:  version=\"0.8.15\", path: lib/, filename:filename.ext]", resource.toString());
	}


	@Test
	public void getVersionTest() {
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), new Resource());
		Assert.assertEquals(" version=\"0.8.15\"", TestHelper.invokePrivateMethod(resource, "getVersion"));
	}


	@Test
	public void getIsolatedTest() {
		GenerationResource resource;

		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals(" isolated=\"true\"", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.TRUE));
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.FALSE));

		Resource resourceConfiguration = new Resource();
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.TRUE, Boolean.TRUE));
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.TRUE, Boolean.FALSE));
		Assert.assertEquals(" isolated=\"true\"", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.TRUE));
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.FALSE));

		TestHelper.injectToPrivateField(resourceConfiguration, "isolated", true);
		Assert.assertEquals(" isolated=\"true\"", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.TRUE));
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.FALSE));
	}


	@Test
	public void getScopeTest() {
		GenerationResource resource;

		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getScope", true));
		Assert.assertEquals(" scope=\"module\"", TestHelper.invokePrivateMethod(resource, "getScope", false));

		Resource resourceConfiguration = new Resource();
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getScope", true));
		Assert.assertEquals(" scope=\"module\"", TestHelper.invokePrivateMethod(resource, "getScope", false));

		TestHelper.injectToPrivateField(resourceConfiguration, "scope", "server");
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getScope", true));
		Assert.assertEquals(" scope=\"server\"", TestHelper.invokePrivateMethod(resource, "getScope", false));
	}


	@Test
	public void getMinVersionTest() {
		GenerationResource resource;
		DefaultConfiguration defaultConfiguration;
		Resource resourceConfiguration;

		defaultConfiguration = new DefaultConfiguration();
		resource = new GenerationResource(TestHelper.createArtifact(), defaultConfiguration, null);
		Assert.assertEquals(" minVersion=\"0.8.15\"", TestHelper.invokePrivateMethod(resource, "getMinVersion"));

		defaultConfiguration = new DefaultConfiguration();
		TestHelper.injectToPrivateField(defaultConfiguration, "useDefaultMinVersion", false);
		resource = new GenerationResource(TestHelper.createArtifact(), defaultConfiguration, null);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getMinVersion"));

		resourceConfiguration = new Resource();
		resource = new GenerationResource(TestHelper.createArtifact(), defaultConfiguration, resourceConfiguration);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getMinVersion"));

		TestHelper.injectToPrivateField(resourceConfiguration, "minVersion", " ");
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getMinVersion"));

		TestHelper.injectToPrivateField(resourceConfiguration, "minVersion", "4.7.11");
		Assert.assertEquals(" minVersion=\"4.7.11\"", TestHelper.invokePrivateMethod(resource, "getMinVersion"));
	}


	@Test
	public void getMaxVersionTest() {
		GenerationResource resource;
		DefaultConfiguration defaultConfiguration;
		Resource resourceConfiguration;

		defaultConfiguration = new DefaultConfiguration();
		resource = new GenerationResource(TestHelper.createArtifact(), defaultConfiguration, null);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getMaxVersion"));

		resourceConfiguration = new Resource();
		resource = new GenerationResource(TestHelper.createArtifact(), defaultConfiguration, resourceConfiguration);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getMaxVersion"));

		TestHelper.injectToPrivateField(resourceConfiguration, "maxVersion", "4.7.11");
		Assert.assertEquals(" maxVersion=\"4.7.11\"", TestHelper.invokePrivateMethod(resource, "getMaxVersion"));
	}


	@Test
	public void getPathTest() {
		GenerationResource resource;

		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals("lib/", TestHelper.invokePrivateMethod(resource, "getPath"));

		Resource resourceConfiguration = new Resource();
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("lib/", TestHelper.invokePrivateMethod(resource, "getPath"));

		TestHelper.injectToPrivateField(resourceConfiguration, "path", "myPath/");
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("myPath/", TestHelper.invokePrivateMethod(resource, "getPath"));
	}


	@Test
	public void hashCodeTest() {
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertEquals("groupId:artifactId".hashCode(), resource.hashCode());
	}


	@Test
	public void equalsTest() {
		GenerationResource resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		Assert.assertFalse(resource.equals(this));

		GenerationResource resource2 = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		TestHelper.injectToPrivateField(resource2, "identifier", "groupId2:artifactId2");
		Assert.assertFalse(resource.equals(resource2));

		GenerationResource resource3 = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);
		TestHelper.injectToPrivateField(resource3, "identifier", "groupId:artifactId");
		Assert.assertTrue(resource.equals(resource3));
	}
}
