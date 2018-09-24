package com.espirit.ps.psci.moduleresourceplugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugins.annotations.Parameter;

public class DefaultConfiguration {

	private static final String DEFAULT_SCOPE = "module";
	private static final Set<String> DEFAULT_COMPONENTS = Collections.singleton("global");
	private static final boolean DEFAULT_ISOLATED = true;
	private static final String DEFAULT_PATH = "lib/";

	@Parameter
	private String scope;

	@Parameter
	private String components;

	@Parameter
	private Boolean isolated;

	@Parameter
	private String path;


	public String getScope() {
		if (scope == null || "".equals(scope)) {
			return DEFAULT_SCOPE;
		}
		return scope;
	}


	public Set<String> getComponents() {
		if (components == null) {
			return DEFAULT_COMPONENTS;
		}
		if (!components.contains(",")) {
			if (components.trim().length() == 0) {
				return Collections.emptySet();
			}
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


	public boolean isIsolated() {
		if (isolated == null) {
			return DEFAULT_ISOLATED;
		}
		return isolated;
	}


	public String getPath() {
		if (path == null) {
			return DEFAULT_PATH;
		}
		return path;
	}


	@Override
	public String toString() {
		return String.format("default [scope: %s, isolated: %s, path: %s, components: %s]", getScope(), isIsolated(), getPath(), getComponents());
	}
}