package com.espirit.ps.psci.moduleresourceplugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, aggregator = false, executionStrategy = "always", requiresOnline = true,
	requiresProject = true, threadSafe = false)
public class GenerateModule extends AbstractMojo {

	private static final String VAR_NAME_RESOURCES_RUNTIME = "resourcesRuntime";

	private static final String VAR_NAME_RESOURCES_MODULE = "resourcesModule";

	private static final String VAR_NAME_RESOURCES = "resources";

	/**
	 * The current maven project.
	 */
	@Parameter(defaultValue = "${project}", readonly = false, required = true)
	private MavenProject project;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	private MavenSession session;

	/**
	 * the remote artifact repositories
	 */
	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
	private List<ArtifactRepository> remoteArtifactRepositories;

	/**
	 * The dependency tree builder to use.
	 */
	@Component(hint = "default")
	private DependencyGraphBuilder dependencyGraphBuilder;

	/**
	 * the local artifact repository
	 */
	@Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
	private ArtifactRepository localRepository;

	/**
	 * Contains the full list of projects in the reactor.
	 */
	@Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
	private List<MavenProject> reactorProjects;

	/**
	 * Used to resolve the dependency maven project.
	 */
	@Component
	private MavenProjectBuilder mavenProjectBuilder;

	/**
	 * Own configuration
	 */
	@Parameter
	private List<Resource> resources;

	@Parameter
	private DefaultConfiguration defaultConfiguration;


	@Override
	public void execute() throws MojoExecutionException {
		if (defaultConfiguration == null) {
			defaultConfiguration = new DefaultConfiguration();
		}

		if (resources == null) {
			resources = Collections.emptyList();
		}

		Set<String> components = collectComponents();
		Map<String, String> values = createEmptyResources(components);

		printInfo(components, values);

		fillResources(components, values);
		fillProjectProperties(values);
	}


	protected Set<String> collectComponents() {
		Set<String> components = new HashSet<>();
		components.addAll(defaultConfiguration.getComponents());

		for (Resource resourceConfiguration : resources) {
			components.addAll(resourceConfiguration.getComponents());
		}
		return components;
	}


	protected static Map<String, String> createEmptyResources(Set<String> components) {
		Map<String, String> emptyValues = new TreeMap<>();

		emptyValues.put(VAR_NAME_RESOURCES, "");
		emptyValues.put(VAR_NAME_RESOURCES_MODULE, "");
		emptyValues.put(VAR_NAME_RESOURCES_RUNTIME, "");

		for (String component : components) {
			String legacyComponent = String.format("module.resources.%s.legacy", component);
			emptyValues.put(legacyComponent, "");
			String isolatedComponent = String.format("module.resources.%s.isolated", component);
			emptyValues.put(isolatedComponent, "");
			String legacyWebComponent = String.format("module.resources.%s.legacy.web", component);
			emptyValues.put(legacyWebComponent, "");
			String isolatedWebComponent = String.format("module.resources.%s.isolated.web", component);
			emptyValues.put(isolatedWebComponent, "");
		}
		return emptyValues;
	}


	private void fillResources(Set<String> components, Map<String, String> values) throws MojoExecutionException {
		try {
			Set<GenerationResource> collectedResources = getResources();
			for (String component : components) {
				for (GenerationResource resource : collectedResources) {
					processResource(resource, component, values, false, false);
					processResource(resource, component, values, false, true);
					processResource(resource, component, values, true, false);
					processResource(resource, component, values, true, true);
				}
			}
			for (GenerationResource resource : collectedResources) {
				processOldResource(resource, values);
			}
		} catch (DependencyGraphBuilderException e) {
			throw new MojoExecutionException("error while collecting dependencies", e);
		}
	}


	private Set<GenerationResource> getResources() throws DependencyGraphBuilderException {
		Set<GenerationResource> modulResources = new HashSet<>();
		final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(this.session.getProjectBuildingRequest());
		buildingRequest.setProject(this.project);

		final DependencyNode rootNode = this.dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null, this.reactorProjects);

		Resource rootNodeConfiguration = getResourceConfiguration(rootNode.getArtifact());
		processDependency(modulResources, rootNodeConfiguration, rootNode);

		for (DependencyNode dependencyNode : rootNode.getChildren()) {
			Artifact rootArtifact = dependencyNode.getArtifact();
			if ("provided".equals(rootArtifact.getScope()) || "test".equals(rootArtifact.getScope())) {
				continue;
			}

			Resource configuration = getResourceConfiguration(rootArtifact);
			CollectingDependencyNodeVisitor visitor = new CollectingDependencyNodeVisitor();
			dependencyNode.accept(visitor);

			for (DependencyNode childDependencyNode : visitor.getNodes()) {
				processDependency(modulResources, configuration, childDependencyNode);
			}
		}

		return modulResources;
	}


	private void processDependency(Set<GenerationResource> modulResources, Resource configuration, DependencyNode childDependencyNode) {
		Artifact artifact = childDependencyNode.getArtifact();

		if ("provided".equals(artifact.getScope()) || "test".equals(artifact.getScope())) {
			return;
		}

		Resource childConfiguration = getResourceConfiguration(artifact);
		if (childConfiguration == null && configuration != null) {
			childConfiguration = configuration.copyForChild();
		}

		modulResources.add(new GenerationResource(artifact, defaultConfiguration, childConfiguration));
	}


	private void processResource(GenerationResource resource, String component, Map<String, String> values, Boolean isWeb, Boolean isIsolated) {
		String resourceString = resource.getResourceString(component, isWeb, isIsolated);
		if (resourceString != null) {
			String componentKey;
			if (isWeb) {
				if (isIsolated) {
					componentKey = String.format("module.resources.%s.isolated.web", component);
				} else {
					componentKey = String.format("module.resources.%s.legacy.web", component);
				}
			} else {
				if (isIsolated) {
					componentKey = String.format("module.resources.%s.isolated", component);
				} else {
					componentKey = String.format("module.resources.%s.legacy", component);
				}
			}
			values.put(componentKey, values.get(componentKey) + resourceString);
		}
	}


	private void processOldResource(GenerationResource resource, Map<String, String> values) {
		String resourceString = resource.getResourceString(true, false);
		if (resourceString == null) {
			return;
		}
		if ("compile".equals(resource.getDependencyScope())) {
			values.put(VAR_NAME_RESOURCES, values.get(VAR_NAME_RESOURCES) + resourceString);
			values.put(VAR_NAME_RESOURCES_MODULE, values.get(VAR_NAME_RESOURCES_MODULE) + resourceString);
		} else if ("runtime".equals(resource.getDependencyScope())) {
			values.put(VAR_NAME_RESOURCES_RUNTIME, values.get(VAR_NAME_RESOURCES_RUNTIME) + resourceString);
		}
	}


	private void fillProjectProperties(Map<String, String> values) {
		for (Entry<String, String> entry : values.entrySet()) {
			project.getProperties().put(entry.getKey(), entry.getValue());
			if (this.getLog().isDebugEnabled()) {
				this.getLog().debug(String.format("%s:%n%s", entry.getKey(), entry.getValue()));
			}
		}
	}


	private Resource getResourceConfiguration(Artifact artifact) {
		String identifier = String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId());

		for (Resource resourceConfiguration : resources) {
			if (identifier.equals(resourceConfiguration.getIdentifier())) {
				return resourceConfiguration;
			}
		}

		return null;
	}


	private void printInfo(Set<String> components, Map<String, String> values) {
		if (this.getLog().isDebugEnabled()) {
			this.getLog().debug(defaultConfiguration.toString());
			this.getLog().debug("additional resource configurations:");
			if (!resources.isEmpty()) {
				this.getLog().debug("-----");
				for (Resource resourceConfiguration : resources) {
					this.getLog().debug(resourceConfiguration.toString());
				}
				this.getLog().debug("-----");
			}
			this.getLog().debug(String.format("additional components %s", components));
		}
		this.getLog().info(String.format("possible keys %s", values.keySet()));
	}
}
