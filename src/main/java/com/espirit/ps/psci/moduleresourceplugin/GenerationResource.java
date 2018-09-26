package com.espirit.ps.psci.moduleresourceplugin;

import org.apache.maven.artifact.Artifact;

public class GenerationResource {

	private final String identifier;
	private final DefaultConfiguration defaultConfiguration;
	private final Resource resourceConfiguration;
	private String filename;
	private String version;


	public GenerationResource(Artifact artifact, DefaultConfiguration configuration, Resource resourceConfiguration) {
		this.defaultConfiguration = configuration;
		this.resourceConfiguration = resourceConfiguration;
		identifier = String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId());
		filename = artifact.getFile().getName();
		version = artifact.getVersion();
	}


	public String getIdentifier() {
		return identifier;
	}


	public Resource getResourceConfiguration() {
		return resourceConfiguration;
	}


	private String getVersion() {
		return String.format(" version=\"%s\"", version);
	}


	private String getIsolated(Boolean isWeb, Boolean isIsolated) {
		if (isWeb || !isIsolated) {
			return "";
		}
		boolean isolated;
		if (resourceConfiguration != null && resourceConfiguration.isIsolated() != null) {
			isolated = resourceConfiguration.isIsolated();
		} else {
			isolated = defaultConfiguration.isIsolated();
		}
		return String.format(" isolated=\"%s\"", isolated);
	}


	private String getScope(boolean isWeb) {
		if (isWeb) {
			return "";
		}
		String scope;
		if (resourceConfiguration != null && resourceConfiguration.getScope() != null) {
			scope = resourceConfiguration.getScope();
		} else {
			scope = defaultConfiguration.getScope();
		}
		return String.format(" scope=\"%s\"", scope);
	}


	private String getMinVersion() {
		if (resourceConfiguration != null && resourceConfiguration.getMinVersion() != null && resourceConfiguration.getMinVersion().trim().length() > 0) {
			return String.format(" minVersion=\"%s\"", resourceConfiguration.getMinVersion());
		} else if (defaultConfiguration.useDefaultMinVersion()) {
			return String.format(" minVersion=\"%s\"", version);
		} else {
			return "";
		}
	}


	private String getMaxVersion() {
		if (resourceConfiguration != null && resourceConfiguration.getMaxVersion() != null) {
			return String.format(" maxVersion=\"%s\"", resourceConfiguration.getMaxVersion());
		}
		return "";
	}


	private String getPath() {
		if (resourceConfiguration != null && resourceConfiguration.getPath() != null) {
			return resourceConfiguration.getPath();
		} else {
			return defaultConfiguration.getPath();
		}
	}


	public String getResourceString(String component, boolean isWeb, boolean isIsolated) {
		boolean allowed = false;
		if (resourceConfiguration != null && !resourceConfiguration.getComponents().isEmpty()) {
			allowed = resourceConfiguration.getComponents().contains(component);
		} else {
			allowed = defaultConfiguration.getComponents().contains(component);
		}
		if (allowed) {
			return String.format("<resource name=\"%s\"%s%s%s%s%s>%s%s</resource>%n", identifier, getScope(isWeb), getIsolated(isWeb, isIsolated), getVersion(), getMinVersion(), getMaxVersion(), getPath(), filename);
		}
		return null;
	}


	@Override
	public int hashCode() {
		return identifier.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GenerationResource) {
			return identifier.equals(((GenerationResource) obj).identifier);
		}
		return super.equals(obj);
	}


	@Override
	public String toString() {
		return String.format("resource [identifier: %s, scope: %s, isolated: %s, version: %s, path: %s, filename:%s]", identifier, getScope(false), getIsolated(false, false), getVersion(), getPath(), filename);
	}


	public void merge(GenerationResource resource) throws DifferenScopeException {
		if (!this.equals(resource)) {
			throw new NotMergableException(String.format("resources with different identifiers not mergable [identifier1: %s, identifier2: %s]", identifier, resource.identifier));
		}
		if (!resourceConfiguration.getComponents().equals(resource.getResourceConfiguration().getComponents())) {
			resourceConfiguration.getComponents().addAll(resource.getResourceConfiguration().getComponents());
		}
		if (!resourceConfiguration.getScope().equals(resource.getResourceConfiguration().getScope())) {
			if (!"server".equals(resourceConfiguration.getScope())) {
				resourceConfiguration.setScope(resource.getResourceConfiguration().getScope());
			}
			throw new DifferenScopeException(String.format("resource-scope changed [%s, %s]", resourceConfiguration, resource.getResourceConfiguration()));
		}
	}
}
