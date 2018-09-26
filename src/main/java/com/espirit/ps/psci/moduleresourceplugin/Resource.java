package com.espirit.ps.psci.moduleresourceplugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugins.annotations.Parameter;

public class Resource {

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

	@Parameter
	private String minVersion;

	@Parameter
	private String maxVersion;


	@Override
	public int hashCode() {
		return identifier.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Resource)) {
			return super.equals(obj);
		}
		return identifier.equals(((Resource) obj).identifier);
	}


	public String getIdentifier() {
		return identifier;
	}


	public String getScope() {
		return scope;
	}


	public void setScope(String scope) {
		this.scope = scope;
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
			if (component.trim().length() > 0) {
				componentSet.add(component.trim());
			}
		}
		return componentSet;
	}


	public Boolean isIsolated() {
		return isolated;
	}


	public String getPath() {
		return path;
	}


	public String getMinVersion() {
		return minVersion;
	}


	public String getMaxVersion() {
		return maxVersion;
	}


	@Override
	public String toString() {
		return String.format("resource [identifier: %s, scope: %s, components: %s, exclude: %s, path: %s, minVersion: %s, maxVersion: %s]", identifier, scope, components, exclude, path, minVersion, maxVersion);
	}
}
