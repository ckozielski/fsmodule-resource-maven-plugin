# module-resource-plugin
Das Plugin ermittelt alle im Projekt verwendeten Abhängigkeiten und bereite diese für eine Ausgabe in der modul.xml auf. Für unterschiedliche Resourcen können eigene Konfigurationen vorgenommen werden. Hierbei vererbt sich eine Konfiguration auch immer auf die transitiven Abhängigkeiten. Hier muss jedoch beachtet werden, das transitive Abhängigkeiten immer nur einmal in die Liste der Resourcen aufgenommen werden. Somit ist es z.B. nicht möglich diese an einerstelle im Scope "server" und an einer anderen Stelle im Scope "module" auszugeben.
Die einzelnen Abhängigkeiten werden in sogenannten Komponenten gebündelt, wobei eine Abhängigkeit in unterschiedlichen Komponenten aufgelistet werden kann. Für jede Komponente werden 4 Ausgabevarianten (legacy, legacy.web, isolated, isolated.web) erzeugt.
Gibt es für eine Abhängigkeit keine Konfiguration, so greift die angegebene default-Konfiguration bzw. die im Plugin hinterlegte.
## Plugin-Konfiguration
Das Plugin kann über das "configration"-TAG in der pom.xml konfiguriert werden. In der Konfiguration kann zum einen die default-Konfiguration als auch die Konfiguration für einzelne Abhängigkeiten angegeben werden.
### defaultConfiguration
    <!-- entspricht der default-konfiguration, die über das plugin gesetzt wird -->
	<defaultConfiguration>
		<scope>module</scope>
		<components>global</components>
		<isolated>true</isolated>
		<path>lib/</path>
		<useDefaultMinVersion>true</useDefaultMinVersion>
	</defaultConfiguration>
#### scope
Scope innerhalb des Firstspirit-Servers für die einzelnen Abhängigkeiten innerhalb des Moduls
#### components
Zuordnung der Abhängigkeiten zu einzelnen Komponenten innerhalb des Moduls. Pro Komponenten könnend die folgenden Varianten abgerufen werden. Die folgenden Attribute, sofern sie konfiguriert sind werden in allen Varianten erzeugt
* name
* version
* minVersion
* maxVersion

Varianten und Unterschiede
* **module.resources.[component].legacy**
Abhängigkeiten, die als exluded markiert sind, werden nicht ausgegeben. Es wird zusätzlich das Attribute "scope" ausgegeben.
* **module.resources.[component].legacy.web**
Abhängigkeiten, die als exluded markiert sind, werden nicht ausgegeben.
* **module.resources.[component].isolated**
Abhängigkeiten, die als exluded markiert sind, werden ausgegeben. Das Attribute "isolated" wird ausgegeben, wenn die Abhängigkeit entsprechend markiert wurde.
* **module.resources.[component].isolated.web**
Abhängigkeiten, die als exluded markiert sind, werden ausgegeben.

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
Der aus GroupId und ArtifactId zusammengesetzte Identifier für die angegebene Abhängig.
#### scope
Abweichender Scope innerhalb des Firstspirit-Servers für die Abhängigkeiten innerhalb des Moduls
#### components
Abweichende Komponenten, in denen die Abhängigkeit ausgegeben wird.
#### isolated
Gibt an, die diese Abhängigkeit über den Isolated Classloader geladen wird.
#### exlude
Unterdrückt die ausgabe der Abhängigkeit bei der "legacy" Ausgabe.
#### path
Pfad zu JAR-Datei innerhalb des FSM.
**HINWEIS: Hierdurch wird die Datei nicht an der entsprechenden Stelle im FSM gespeichert.**
#### minVersion
Angabe der Minimal-Version in der diese Abhängigkeit benötigt wird. Überschreibt das Verhalten der "useDefaultMinVersion" aus der default-Konfiguration.
#### maxVersion
Angabe der Maximalen-Version in der diese Abhängigkeit von dem Modul genutzt werden kann.
## Beispielkonfiguration
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
## Einschränkungen
Sollte eine Transitive Abhängigkeit bei 2 oder mehr Abhängigkeiten mit unterschiedlichen Konfigurationen benötigt werden, so muss für diese eine eigene Konfiguration angelegt werden, die für alle Ihre aufrufenden Abhängigkeiten gültig ist.