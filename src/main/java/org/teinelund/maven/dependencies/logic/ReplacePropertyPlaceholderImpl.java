package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.teinelund.maven.dependencies.domain.PomImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ReplacePropertyPlaceholderImpl implements ReplacePropertyPlaceholder {

    private InformationSink informationSink;
    private CommandLineOptions options;
    private MavenPomFileConnector mavenPomFileConnector;

    public ReplacePropertyPlaceholderImpl(InformationSink informationSink, CommandLineOptions options,
                                          MavenPomFileConnector mavenPomFileConnector) {
        this.informationSink = informationSink;
        this.options = options;
        this.mavenPomFileConnector = mavenPomFileConnector;
    }

    @Override
    public void replacePlaceholders(Collection<PomImpl> poms) {
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Replace Property Placeholder...");
        }
        for (PomImpl pom : poms) {
            String groupId = pom.getDependency().getGroupId();
            String version = pom.getDependency().getVersion();
            replacePropertyVariablesInPom(pom, groupId, version, new HashMap<String, String>());

        }
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("End Replace Property Placeholder.");
        }
        this.mavenPomFileConnector.connectMavenPomFiles(poms);
    }


    void replacePropertyVariablesInPom(final PomImpl pom, final String parentGroupId, final String parentVersion,
                                       Map<String,String> parentProperties) {
        if (pom.getDependency().getGroupId().equals(Application.getDependencyPlaceholder())) {
            pom.getDependency().replaceGroupId(parentGroupId);
        }
        if (pom.getDependency().getVersion().equals(Application.getDependencyPlaceholder())) {
            pom.getDependency().replaceVersion(parentVersion);
        }

        pom.addProperties(parentProperties);

        for (Dependency dependency : pom.getDependencies()) {
            if (dependency.getGroupId().equals(Application.getGroupIdPlaceholder())) {
                dependency.replaceGroupId(pom.getDependency().getGroupId());
            }
            if (dependency.getVersion().equals(Application.getVersionPlaceholder())) {
                dependency.replaceVersion(pom.getDependency().getVersion());
            }
            if (dependency.getVersion().startsWith("$") ||
                    dependency.getGroupId().startsWith("$")) {
                for (String key : pom.getProperties().keySet()) {
                    if (dependency.getGroupId().equals("${" + key + "}"))
                        dependency.replaceGroupId(pom.getProperty(key));
                    if (dependency.getVersion().equals("${" + key + "}"))
                        dependency.replaceVersion(pom.getProperty(key));
                }
            }
        }

        for (PomImpl childPom : pom.getPomsThatDependsOnThisPomImpl()) {
            String groupId = pom.getDependency().getGroupId();
            String version = pom.getDependency().getVersion();
            replacePropertyVariablesInPom(childPom, groupId, version, pom.getProperties());
        }
        
    }

}
