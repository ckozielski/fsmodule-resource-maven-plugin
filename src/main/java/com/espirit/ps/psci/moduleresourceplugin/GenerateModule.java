package com.espirit.ps.psci.moduleresourceplugin;

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
	@Parameter(name = "resources")
	private List<ResourceConfiguration> resources;

	@Parameter
	private DefaultConfiguration defaultConfiguration;


	@Override
	public void execute() throws MojoExecutionException {
		Set<String> components = collectComponents();
		Map<String, String> values = createEmptyResources(components);

		printInfo(components, values);

		fillResources(components, values);
		fillProjectProperties(values);
	}


	private Set<String> collectComponents() {
		Set<String> components = new HashSet<>();
		components.addAll(defaultConfiguration.getComponents());

		for (ResourceConfiguration resourceConfiguration : resources) {
			components.addAll(resourceConfiguration.getComponents());
		}

		return components;
	}


	private Map<String, String> createEmptyResources(Set<String> components) {
		Map<String, String> emptyValues = new TreeMap<>();
		for (String component : components) {
			String legacyComponent = String.format("%s.legacy", component);
			emptyValues.put(legacyComponent, "");
			String isolatedComponent = String.format("%s.isolated", component);
			emptyValues.put(isolatedComponent, "");
			String webComponent = String.format("%s.web", component);
			emptyValues.put(webComponent, "");
		}
		return emptyValues;
	}


	private void fillResources(Set<String> components, Map<String, String> values) throws MojoExecutionException {
		try {
			for (String component : components) {
				for (Resource resource : getResources()) {
					processResource(resource, component, values, false, false);
					processResource(resource, component, values, false, true);
					processResource(resource, component, values, true, true);
				}
			}
		} catch (DependencyGraphBuilderException e) {
			throw new MojoExecutionException("error while collecting dependencies", e);
		}
	}


	private Set<Resource> getResources() throws DependencyGraphBuilderException {
		Set<Resource> modulResources = new HashSet<>();
		final ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(this.session.getProjectBuildingRequest());
		buildingRequest.setProject(this.project);

		final DependencyNode rootNode = this.dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null, this.reactorProjects);
		for (DependencyNode dependencyNode : rootNode.getChildren()) {
			Artifact rootArtifact = dependencyNode.getArtifact();
			if ("provided".equals(rootArtifact.getScope()) || "test".equals(rootArtifact.getScope())) {
				continue;
			}

			ResourceConfiguration configuration = getResourceConfiguration(rootArtifact);
			CollectingDependencyNodeVisitor visitor = new CollectingDependencyNodeVisitor();
			dependencyNode.accept(visitor);

			for (DependencyNode childDependencyNode : visitor.getNodes()) {
				Artifact artifact = childDependencyNode.getArtifact();

				if ("provided".equals(artifact.getScope()) || "test".equals(artifact.getScope())) {
					continue;
				}

				ResourceConfiguration childConfiguration = getResourceConfiguration(artifact);
				if (childConfiguration == null) {
					childConfiguration = configuration;
				}

				modulResources.add(new Resource(artifact, defaultConfiguration, childConfiguration));
			}
		}

		return modulResources;
	}


	private void processResource(Resource resource, String component, Map<String, String> values, boolean isWeb, boolean isIsolated) {
		String resourceString = resource.getResourceString(component, isWeb, isIsolated);
		if (resourceString != null) {
			String componentKey;
			if (isWeb) {
				componentKey = String.format("%s.web", component);
			} else if (isIsolated) {
				componentKey = String.format("%s.isolated", component);
			} else {
				componentKey = String.format("%s.legacy", component);
			}
			values.put(componentKey, values.get(componentKey) + resourceString);
		}
	}


	private void fillProjectProperties(Map<String, String> values) {
		for (Entry<String, String> entry : values.entrySet()) {
			project.getProperties().put("module.resources." + entry.getKey(), entry.getValue());
		}
	}


	private ResourceConfiguration getResourceConfiguration(Artifact artifact) {
		String identifier = String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId());

		for (ResourceConfiguration resourceConfiguration : resources) {
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
				for (ResourceConfiguration resourceConfiguration : resources) {
					this.getLog().debug(resourceConfiguration.toString());
				}
				this.getLog().debug("-----");
			}
			this.getLog().debug(String.format("additional components %s", components));
		}
		this.getLog().info(String.format("possible keys %s", values.keySet()));
	}
}
