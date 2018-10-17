# module-resource-plugin

The plugin detects all dependencies used in the project and prepare them for output in the modul(-isolated).xml. Different configurations can be made for different resources. A configuration always inherits to the transitive dependencies. However, it should be noted that transitive dependencies are included only once in the list of resources. Thus, it is e.g. not possible to output them at one point in the scope "server" and at another point in the scope "module".

The individual dependencies are bundled in so-called components, whereby a dependency can be listed in different components. For each component, 4 output variants (legacy, legacy.web, isolated, isolated.web) are generated.
If there is no configuration for a dependency, the specified default configuration or the one stored in the plug-in will take effect.


## plugin configuration

The plugin can be configured via the _configration_ tag in the pom.xml. In the configuration, the default configuration as well as the configuration for individual dependencies can be specified.

### defaultConfiguration

    <!-- this example is exactly the same like the embedded configuration of the plugin. -->
	<defaultConfiguration>
		<scope>module</scope>
		<components>global</components>
		<isolated>true</isolated>
		<path>lib/</path>
		<useDefaultMinVersion>true</useDefaultMinVersion>
	</defaultConfiguration>

#### scope

Defines the scope for the (transient) dependency used in the module definition.

#### components

Assignment of dependencies to individual components within the module. For each component, the following variants can be retrieved. The following attributes, if configured, are generated in all variants:

* name
* version
* minVersion
* maxVersion

Variations and differences:

* **module.resources.[component].legacy**
Dependencies marked as excluded will be not generated. In addition, the attribute _scope_ will be added.


* **module.resources.[component].legacy.web**
Dependencies marked as excluded will be not generated.

* **module.resources.[component].isolated**
Dependencies marked as excluded will be generated. In addition, the attribute _isolated_ will be generated when the dependency is marked. 

* **module.resources.[component].isolated.web**
Dependencies marked as excluded will be generated.


### resources -> resource

    <resources>
        ...
        <resource>
            <identifier>com.espirit.ps.psci.workflowhelper:workflow-helper</identifier>
            <scope>server</scope>
            <components>global,service</components>
            <isolated>true</isolated>
            <exclude>true</exclude>
            <path>lib/</path>
            <minVersion>0.8.15</minVersion>
            <maxVersion>4.7.11</maxVersion>
        </resource>
        ...
    </resources>

#### identifier

The Maven GroupId and Artifactid identifier for the dependency separated by a colon (:). You must not define the version!

#### scope

Different scope definition for the (transient) dependency used in the module definition.

#### components

Another definition for the components related to this dependency.

#### isolated

Defines if the dependency will be loaded by the _isolated_ classloader of the _legacy_.

#### exclude

When _excluded_ the dependency will be not generated for the _legacy_ mode. 

#### path

Path to the JAR file inside of the FSM bundle/file.
**Attention: This will not copy the dependency to the right place at your FSM hierarchy.**

#### minVersion

Sets the minimal version definition for this dependency. This will overwrite the setting _useDefaultMinVersion_ from the _default_ configuration area.

#### maxVersion

Sets the maximal version definition for this dependency.

## Example

	<plugin>
		<groupId>com.espirit.ps.psci</groupId>
		<artifactId>module-resource-plugin</artifactId>
		<version>0.1</version>
		<executions>
			<execution>
				<id>generate-module-resources</id>
				<phase>process-resources</phase>
				<goals>
					<goal>generate</goal>
				</goals>
				<configuration>
					<defaultConfiguration>
						<scope>module</scope>
						<components>peter</components>
					</defaultConfiguration>
					<resources>
						<resource>
							<identifier>com.espirit.ps.psci.module:generic-configuration</identifier>
							<isolated>false</isolated>
						</resource>
						<resource>
							<identifier>com.espirit.ps.psci.module:magic-icons</identifier>
							<components>web</components>
						</resource>
						<resource>
							<identifier>com.espirit.ps.psci.workflowhelper:workflow-helper</identifier>
							<components>global,web</components>
							<isolated>true</isolated>
							<exclude>true</exclude>
						</resource>
					</resources>
				</configuration>
			</execution>
		</executions>
	</plugin>



## Limitations

If a transitive dependency is required for two or more dependencies and this dependencies contains different configurations, a separate configuration has to be created for this transitive dependency which is valid for all your calling dependencies.