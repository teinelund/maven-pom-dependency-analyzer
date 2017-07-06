package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.PomImpl;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplacePropertyPlaceholderLinker implements Linker<List<PomImpl>> {

    private CommandLineOptions options;
    private Linker<List<PomImpl>> linker;

    public ReplacePropertyPlaceholderLinker(final CommandLineOptions options, final Linker<List<PomImpl>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<PomImpl> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Replace Property Placeholder...");
        }
        for (PomImpl pomImpl : input) {
            if (pomImpl.getParentPom().isPresent() ) {
                if (pomImpl.getDependency().getGroupId().equals(Application.getGroupIdPlaceholder())) {
                    printErrorMessage(pomImpl, "Can't find ${project.groupId} for pomImpl: ");
                }
                if (pomImpl.getDependency().getGroupId().equals(Application.getDependencyPlaceholder())) {
                    printErrorMessage(pomImpl, "Can't calculate groupId for pomImpl: ");
                }
                if (pomImpl.getDependency().getVersion().equals(Application.getVersionPlaceholder())) {
                    printErrorMessage(pomImpl, "Can't find ${project.version} for pomImpl: ");
                }
                if (pomImpl.getDependency().getVersion().equals(Application.getDependencyPlaceholder())) {
                    printErrorMessage(pomImpl, "Can't calculate version for pomImpl: ");
                }
                String projectGroupId = pomImpl.getDependency().getGroupId();
                String projectVersion = pomImpl.getDependency().getVersion();
                Map<String, String> properties = new HashMap<>();
                replacePropertyVariablesInPom(pomImpl, projectGroupId, projectVersion, properties);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Replace Property Placeholder.");
        }
        this.linker.process(input);
    }

    void printErrorMessage(final PomImpl pomImpl, final String errorMessage) {
        StringBuilder error = new StringBuilder();
        error.append(errorMessage);
        error.append(Application.getNewLine());
        error.append(pomImpl.toString());
        System.out.println(error.toString());
        System.exit(-1);
    }

    void replacePropertyVariablesInPom(final PomImpl pomImpl, final String projectGroupId, final String projectVersion,
                                       Map<String,String> properties) {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("  Process pomImpl : " + pomImpl.getDependency().toString());
        }
        printPropertiesInPom(pomImpl);
        if (pomImpl.getDependency().getGroupId().equals(Application.getGroupIdPlaceholder())) {
            pomImpl.getDependency().replaceGroupId(projectGroupId);
        }
        if (pomImpl.getDependency().getVersion().equals(Application.getVersionPlaceholder())) {
            pomImpl.getDependency().replaceVersion(projectVersion);
        }
        for (Dependency dependency : pomImpl.getDependencies()) {
            String oldDependencyString = dependency.toString();
            boolean isPropertyReplaced = false;
            if (dependency.getVersion().trim().startsWith("${") && dependency.getVersion().trim().endsWith("}")) {
                String propertyPlaceholder = dependency.getVersion().trim().substring(2, dependency.getVersion().trim().length() - 1);
                if (pomImpl.existProperty(propertyPlaceholder)) {
                    dependency.replaceVersion(pomImpl.getProperty(propertyPlaceholder));
                    isPropertyReplaced = true;
                }
                if (properties.containsKey(propertyPlaceholder)) {
                    dependency.replaceVersion(properties.get(propertyPlaceholder));
                    isPropertyReplaced = true;
                }
                if (dependency.getVersion().equals(Application.getVersionPlaceholder())) {
                    dependency.replaceVersion(projectVersion);
                    isPropertyReplaced = true;
                }
            }
            if (dependency.getGroupId().trim().startsWith("${") && dependency.getGroupId().trim().endsWith("}")) {
                if (dependency.getGroupId().equals(Application.getGroupIdPlaceholder())) {
                    dependency.replaceGroupId(projectGroupId);
                    isPropertyReplaced = true;
                }
            }
            if (isPropertyReplaced) {
                if (options.isOption(OPTION.VERBOSE)) {
                    System.out.println("    Dependency before replacement : " + oldDependencyString);
                    System.out.println("    Dependency after replacement  : " + dependency.toString());
                }
            }
        }

        for (PomImpl pomImplModule : pomImpl.getModulesPomImpls()) {
            Map<String,String> propertiesCopy = new HashMap<>();
            propertiesCopy.putAll(properties);
            propertiesCopy.putAll(pomImpl.getProperties());
            replacePropertyVariablesInPom(pomImplModule, projectGroupId, projectVersion, propertiesCopy);
        }
    }

    void printPropertiesInPom(final PomImpl pomImpl) {
        if (options.isOption(OPTION.VERBOSE)) {
            if ( ! pomImpl.getProperties().isEmpty()) {
                StringBuilder properties = new StringBuilder();
                properties.append("    Properties: ");
                for (String key : pomImpl.getProperties().keySet()) {
                    String value = pomImpl.getProperty(key);
                    properties.append(key);
                    properties.append(":");
                    properties.append(value);
                    properties.append(" ");
                }
                System.out.println(properties.toString());
            }
            else {
                System.out.println("    Properties: none");
            }
        }
    }
}
