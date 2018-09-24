package com.espirit.ps.psci.moduleresourceplugin;

import org.apache.maven.artifact.Artifact;

public class Resource {

	private final String identifier;
	private final DefaultConfiguration defaultConfiguration;
	private final ResourceConfiguration resourceConfiguration;
	private String filename;
	private String version;


	public Resource(Artifact artifact, DefaultConfiguration configuration, ResourceConfiguration resourceConfiguration) {
		this.defaultConfiguration = configuration;
		this.resourceConfiguration = resourceConfiguration;
		identifier = String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId());
		filename = artifact.getFile().getName();
		version = artifact.getVersion();
	}


	@Override
	public String toString() {
		return String.format("resource [identifier: %s, scope: %s, isolated: %s, version: %s, path: %s, filename:%s]", identifier, getScope(false), getIsolated(false, false), getVersion(), getPath(), filename);
	}


	public String getResourceString(String component, boolean isWeb, boolean isIsolated) {
		boolean allowed = false;
		if (resourceConfiguration != null && !resourceConfiguration.getComponents().isEmpty()) {
			allowed = resourceConfiguration.getComponents().contains(component);
		} else {
			allowed = defaultConfiguration.getComponents().contains(component);
		}
		if (allowed) {
			return String.format("<resource name=\"%s\"%s%s%s>%s%s</resource>%n", identifier, getScope(isWeb), getIsolated(isWeb, isIsolated), getVersion(), getPath(), filename);
		}
		return null;
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


	private String getPath() {
		if (resourceConfiguration != null && resourceConfiguration.getPath() != null) {
			return resourceConfiguration.getPath();
		} else {
			return defaultConfiguration.getPath();
		}
	}
}
