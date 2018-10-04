package com.espirit.ps.psci.moduleresourceplugin;

import org.apache.maven.artifact.Artifact;

public class GenerationResource {

	private final String identifier;
	private final DefaultConfiguration defaultConfiguration;
	private final Resource resourceConfiguration;
	private final String dependencyScope;
	private String filename;
	private String version;


	public GenerationResource(Artifact artifact, DefaultConfiguration configuration, Resource resourceConfiguration) {
		this.dependencyScope = artifact.getScope();
		this.defaultConfiguration = configuration;
		this.resourceConfiguration = resourceConfiguration;
		identifier = String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId());
		if (artifact.getFile() != null) {
			this.filename = artifact.getFile().getName();
		} else {
			this.filename = "";
		}
		version = artifact.getVersion();
	}


	public String getIdentifier() {
		return identifier;
	}


	public Resource getResourceConfiguration() {
		return resourceConfiguration;
	}


	public String getDependencyScope() {
		return dependencyScope;
	}


	private String getVersion() {
		return String.format(" version=\"%s\"", version);
	}


	private String getMode(Boolean isWeb, Boolean isIsolated) {
		if (isWeb || !isIsolated) {
			return "";
		}
		boolean isolated;
		if (resourceConfiguration != null && resourceConfiguration.isIsolated() != null) {
			isolated = resourceConfiguration.isIsolated();
		} else {
			isolated = defaultConfiguration.isIsolated();
		}
		return String.format(" mode=\"%s\"", isolated ? "isolated" : "legacy");
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
		if ("".equals(filename) && (resourceConfiguration == null || resourceConfiguration.getPath() == null)) {
			allowed = false;
		} else if (!isIsolated && resourceConfiguration != null && resourceConfiguration.isExluded()) {
			allowed = false;
		} else if (resourceConfiguration != null && !resourceConfiguration.getComponents().isEmpty()) {
			allowed = resourceConfiguration.getComponents().contains(component);
		} else {
			allowed = defaultConfiguration.getComponents().contains(component);
		}
		if (allowed) {
			return getResourceString(isWeb, isIsolated);
		}
		return null;
	}


	public String getResourceString(boolean isWeb, boolean isIsolated) {
		if ("".equals(filename) && (resourceConfiguration == null || resourceConfiguration.getPath() == null)) {
			return null;
		}
		return String.format("<resource name=\"%s\"%s%s%s%s%s>%s%s</resource>%n", identifier, getScope(isWeb), getMode(isWeb, isIsolated), getVersion(), getMinVersion(), getMaxVersion(), getPath(), filename);
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
		return String.format("resource [identifier: %s, scope: %s, isolated: %s, version: %s, path: %s, filename:%s]", identifier, getScope(false), getMode(false, false), getVersion(), getPath(), filename);
	}
}
