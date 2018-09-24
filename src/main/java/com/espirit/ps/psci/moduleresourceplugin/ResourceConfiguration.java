package com.espirit.ps.psci.moduleresourceplugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugins.annotations.Parameter;

public class ResourceConfiguration {

	@Parameter(required = true)
	private String identifier;

	@Parameter
	private String scope;

	@Parameter
	private String components;

	@Parameter
	private Boolean isolated;

	@Parameter
	private boolean exclude;

	@Parameter
	private String path;


	public String getIdentifier() {
		return identifier;
	}


	public String getScope() {
		return scope;
	}


	public Set<String> getComponents() {
		if (components == null) {
			return Collections.emptySet();
		}
		if (!components.contains(",")) {
			return Collections.singleton(components);
		}

		Set<String> componentSet = new HashSet<>();
		for (String component : components.split(",")) {
			componentSet.add(component.trim());
		}
		return componentSet;
	}


	public Boolean isIsolated() {
		return isolated;
	}


	public String getPath() {
		return path;
	}


	@Override
	public String toString() {
		return String.format("resource [identifier: %s, scope: %s, components: %s, exclude: %s, path: %s]", identifier, scope, components, exclude, path);
	}
}
