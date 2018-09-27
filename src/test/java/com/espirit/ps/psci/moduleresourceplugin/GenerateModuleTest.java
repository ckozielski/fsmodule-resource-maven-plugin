package com.espirit.ps.psci.moduleresourceplugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
		Resource resourceConfiguration = new Resource();
		TestHelper.injectToPrivateField(generateModule, "resources", Collections.singletonList(resourceConfiguration));
		Assert.assertTrue(generateModule.collectComponents().isEmpty());
	}


	@Test
	public void createEmptyResourcesTest() {
		Map<String, String> emptyResources;

		emptyResources = GenerateModule.createEmptyResources(Collections.emptySet());
		Assert.assertEquals(3, emptyResources.size());
		Assert.assertTrue(emptyResources.containsKey("resources"));
		Assert.assertEquals("", emptyResources.get("resources"));
		Assert.assertTrue(emptyResources.containsKey("resourcesModule"));
		Assert.assertEquals("", emptyResources.get("resourcesModule"));
		Assert.assertTrue(emptyResources.containsKey("resourcesRuntime"));
		Assert.assertEquals("", emptyResources.get("resourcesRuntime"));

		emptyResources = GenerateModule.createEmptyResources(Collections.singleton("global"));
		Assert.assertEquals(7, emptyResources.size());
		Assert.assertTrue(emptyResources.containsKey("module.resources.global.isolated"));
		Assert.assertEquals("", emptyResources.get("module.resources.global.isolated"));
		Assert.assertTrue(emptyResources.containsKey("module.resources.global.legacy"));
		Assert.assertEquals("", emptyResources.get("module.resources.global.legacy"));
		Assert.assertTrue(emptyResources.containsKey("module.resources.global.legacy.web"));
		Assert.assertEquals("", emptyResources.get("module.resources.global.legacy.web"));
		Assert.assertTrue(emptyResources.containsKey("module.resources.global.isolated.web"));
		Assert.assertEquals("", emptyResources.get("module.resources.global.isolated.web"));
		Assert.assertTrue(emptyResources.containsKey("resources"));
		Assert.assertEquals("", emptyResources.get("resources"));
		Assert.assertTrue(emptyResources.containsKey("resourcesModule"));
		Assert.assertEquals("", emptyResources.get("resourcesModule"));
		Assert.assertTrue(emptyResources.containsKey("resourcesRuntime"));
		Assert.assertEquals("", emptyResources.get("resourcesRuntime"));
	}


	@Test
	public void processDependencyTest() throws MojoExecutionException {
		HashSet<GenerationResource> resources;

		GenerateModule generateModule = new GenerateModule();
		TestHelper.injectToPrivateField(generateModule, "defaultConfiguration", new DefaultConfiguration());
		TestHelper.injectToPrivateField(generateModule, "resources", Collections.emptyList());
		Resource resourceConfiguration = new Resource();
		DependencyNode dependencyNode = Mockito.mock(DependencyNode.class);
		Artifact artifact = TestHelper.createArtifact();
		Mockito.when(dependencyNode.getArtifact()).thenReturn(artifact);

		resources = new HashSet<GenerationResource>();
		TestHelper.invokePrivateMethod(generateModule, "processDependency", resources, resourceConfiguration, dependencyNode);
		Assert.assertEquals(1, resources.size());

		resources = new HashSet<GenerationResource>();
		Mockito.when(artifact.getScope()).thenReturn("provided");
		TestHelper.invokePrivateMethod(generateModule, "processDependency", resources, resourceConfiguration, dependencyNode);
		Assert.assertEquals(0, resources.size());

		resources = new HashSet<GenerationResource>();
		Mockito.when(artifact.getScope()).thenReturn("test");
		TestHelper.invokePrivateMethod(generateModule, "processDependency", resources, resourceConfiguration, dependencyNode);
		Assert.assertEquals(0, resources.size());

		resources = new HashSet<GenerationResource>();
		TestHelper.injectToPrivateField(resourceConfiguration, "identifier", "groupId:artifactId");
		TestHelper.injectToPrivateField(generateModule, "resources", Collections.singletonList(resourceConfiguration));
		Mockito.when(artifact.getScope()).thenReturn("compile");
		TestHelper.invokePrivateMethod(generateModule, "processDependency", resources, resourceConfiguration, dependencyNode);
		Assert.assertEquals(1, resources.size());

	}


	@SuppressWarnings("unchecked")
	@Test
	public void processResourceTest() {
		GenerationResource resource;
		Map<String, String> values;
		GenerateModule generateModule = new GenerateModule();
		resource = new GenerationResource(TestHelper.createArtifact(), new DefaultConfiguration(), null);

		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		TestHelper.invokePrivateMethod(generateModule, "processResource", resource, "global", values, false, false);
		Assert.assertFalse("".equals(values.get("module.resources.global.legacy")));
		TestHelper.invokePrivateMethod(generateModule, "processResource", resource, "global", values, false, true);
		Assert.assertFalse("".equals(values.get("module.resources.global.isolated")));
		TestHelper.invokePrivateMethod(generateModule, "processResource", resource, "global", values, true, false);
		Assert.assertFalse("".equals(values.get("module.resources.global.legacy.web")));
		TestHelper.invokePrivateMethod(generateModule, "processResource", resource, "global", values, true, true);
		Assert.assertFalse("".equals(values.get("module.resources.global.isolated.web")));

		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), null);
		TestHelper.invokePrivateMethod(generateModule, "processResource", resource, "global", values, true, true);
		Assert.assertEquals("", values.get("module.resources.global.isolated.web"));
	}


	@SuppressWarnings("unchecked")
	@Test
	public void processOldResourceTest() {
		GenerationResource resource;
		Map<String, String> values;

		GenerateModule generateModule = new GenerateModule();

		Artifact createArtifact = TestHelper.createArtifact();
		Mockito.when(createArtifact.getScope()).thenReturn("compile");
		resource = new GenerationResource(createArtifact, new DefaultConfiguration(), null);
		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		TestHelper.invokePrivateMethod(generateModule, "processOldResource", resource, values);
		Assert.assertFalse("".equals(values.get("resources")));
		Assert.assertFalse("".equals(values.get("resourcesModule")));
		Assert.assertEquals("", values.get("resourcesRuntime"));

		Mockito.when(createArtifact.getScope()).thenReturn("runtime");
		resource = new GenerationResource(createArtifact, new DefaultConfiguration(), null);
		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		TestHelper.invokePrivateMethod(generateModule, "processOldResource", resource, values);
		Assert.assertFalse("".equals(values.get("resourcesRuntime")));
		Assert.assertEquals("", values.get("resources"));
		Assert.assertEquals("", values.get("resourcesModule"));

		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		resource = new GenerationResource(TestHelper.createArtifact(null), new DefaultConfiguration(), null);
		TestHelper.invokePrivateMethod(generateModule, "processOldResource", resource, values);
		Assert.assertEquals("", values.get("resources"));
		Assert.assertEquals("", values.get("resourcesModule"));
		Assert.assertEquals("", values.get("resourcesRuntime"));

		Mockito.when(createArtifact.getScope()).thenReturn("provided");
		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		resource = new GenerationResource(createArtifact, new DefaultConfiguration(), null);
		TestHelper.invokePrivateMethod(generateModule, "processOldResource", resource, values);
		Assert.assertEquals("", values.get("resources"));
		Assert.assertEquals("", values.get("resourcesModule"));
		Assert.assertEquals("", values.get("resourcesRuntime"));

		Mockito.when(createArtifact.getScope()).thenReturn("");
		values = (Map<String, String>) TestHelper.invokePrivateMethod(generateModule, "createEmptyResources", Collections.singleton("global"));
		resource = new GenerationResource(createArtifact, new DefaultConfiguration(), null);
		TestHelper.invokePrivateMethod(generateModule, "processOldResource", resource, values);
		Assert.assertEquals("", values.get("resources"));
		Assert.assertEquals("", values.get("resourcesModule"));
		Assert.assertEquals("", values.get("resourcesRuntime"));
	}

}
