package com.espirit.ps.psci.moduleresourceplugin;

import java.io.File;
import org.apache.maven.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ResourceTest {

	private static Artifact artifact;

	static {
		artifact = Mockito.mock(Artifact.class);
		Mockito.when(artifact.getGroupId()).thenReturn("groupId");
		Mockito.when(artifact.getArtifactId()).thenReturn("artifactId");
		Mockito.when(artifact.getVersion()).thenReturn("0.8.15");
		File artifactFile = Mockito.mock(File.class);
		Mockito.when(artifactFile.getName()).thenReturn("filename.ext");
		Mockito.when(artifact.getFile()).thenReturn(artifactFile);
	}


	@Test
	public void getResourceString() {
		Resource resource;
		resource = new Resource(artifact, new DefaultConfiguration(), null);
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, false));
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" version=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", true, true));

		Assert.assertNull(resource.getResourceString("hurz", false, false));

		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		resource = new Resource(artifact, new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, false));

		TestHelper.injectToPrivateField(resourceConfiguration, "components", "global");
		Assert.assertEquals(String.format("<resource name=\"groupId:artifactId\" scope=\"module\" version=\"0.8.15\">lib/filename.ext</resource>%n"), resource.getResourceString("global", false, false));
	}


	@Test
	public void toStringTest() {
		Resource resource = new Resource(artifact, new DefaultConfiguration(), null);
		Assert.assertEquals("resource [identifier: groupId:artifactId, scope:  scope=\"module\", isolated: , version:  version=\"0.8.15\", path: lib/, filename:filename.ext]", resource.toString());
	}


	@Test
	public void getVersionTest() {
		Resource resource = new Resource(artifact, new DefaultConfiguration(), new ResourceConfiguration());
		Assert.assertEquals(" version=\"0.8.15\"", TestHelper.invokePrivateMethod(resource, "getVersion"));
	}


	@Test
	public void getIsolatedTest() {
		Resource resource;

		resource = new Resource(artifact, new DefaultConfiguration(), null);
		Assert.assertEquals(" isolated=\"true\"", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.TRUE));
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getIsolated", Boolean.FALSE, Boolean.FALSE));

		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		resource = new Resource(artifact, new DefaultConfiguration(), resourceConfiguration);
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
		Resource resource;

		resource = new Resource(artifact, new DefaultConfiguration(), null);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getScope", true));
		Assert.assertEquals(" scope=\"module\"", TestHelper.invokePrivateMethod(resource, "getScope", false));

		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		resource = new Resource(artifact, new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getScope", true));
		Assert.assertEquals(" scope=\"module\"", TestHelper.invokePrivateMethod(resource, "getScope", false));

		TestHelper.injectToPrivateField(resourceConfiguration, "scope", "server");
		Assert.assertEquals("", TestHelper.invokePrivateMethod(resource, "getScope", true));
		Assert.assertEquals(" scope=\"server\"", TestHelper.invokePrivateMethod(resource, "getScope", false));
	}


	@Test
	public void getPathTest() {
		Resource resource;

		resource = new Resource(artifact, new DefaultConfiguration(), null);
		Assert.assertEquals("lib/", TestHelper.invokePrivateMethod(resource, "getPath"));

		ResourceConfiguration resourceConfiguration = new ResourceConfiguration();
		resource = new Resource(artifact, new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("lib/", TestHelper.invokePrivateMethod(resource, "getPath"));

		TestHelper.injectToPrivateField(resourceConfiguration, "path", "myPath/");
		resource = new Resource(artifact, new DefaultConfiguration(), resourceConfiguration);
		Assert.assertEquals("myPath/", TestHelper.invokePrivateMethod(resource, "getPath"));
	}
}