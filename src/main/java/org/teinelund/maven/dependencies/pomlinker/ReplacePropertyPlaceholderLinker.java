package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.Dependency;
import org.teinelund.maven.dependencies.Pom;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplacePropertyPlaceholderLinker implements Linker<List<Pom>> {

    private CommandLineOptions options;
    private Linker<List<Pom>> linker;

    public ReplacePropertyPlaceholderLinker(final CommandLineOptions options, final Linker<List<Pom>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<Pom> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Replace Property Placeholder...");
        }
        for (Pom pom : input) {
            if (pom.getParentModulePom() == null) {
                if (pom.getDependency().getGroupId().equals(Application.getGroupIdPlaceholder())) {
                    printErrorMessage(pom, "Can't find ${project.groupId} for pom: ");
                }
                if (pom.getDependency().getGroupId().equals(Application.getDependencyPlaceholder())) {
                    printErrorMessage(pom, "Can't calculate groupId for pom: ");
                }
                if (pom.getDependency().getVersion().equals(Application.getVersionPlaceholder())) {
                    printErrorMessage(pom, "Can't find ${project.version} for pom: ");
                }
                if (pom.getDependency().getVersion().equals(Application.getDependencyPlaceholder())) {
                    printErrorMessage(pom, "Can't calculate version for pom: ");
                }
                String projectGroupId = pom.getDependency().getGroupId();
                String projectVersion = pom.getDependency().getVersion();
                Map<String, String> properties = new HashMap<>();
                replacePropertyVariablesInPom(pom, projectGroupId, projectVersion, properties);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Replace Property Placeholder.");
        }
        this.linker.process(input);
    }

    void printErrorMessage(final Pom pom, final String errorMessage) {
        StringBuilder error = new StringBuilder();
        error.append(errorMessage);
        error.append(Application.getNewLine());
        error.append(pom.toString());
        System.out.println(error.toString());
        System.exit(-1);
    }

    void replacePropertyVariablesInPom(final Pom pom, final String projectGroupId, final String projectVersion,
                                              Map<String,String> properties) {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("  Process pom : " + pom.getDependency().toString());
        }
        printPropertiesInPom(pom);
        if (pom.getDependency().getGroupId().equals(Application.getGroupIdPlaceholder())) {
            pom.getDependency().replaceGroupId(projectGroupId);
        }
        if (pom.getDependency().getVersion().equals(Application.getVersionPlaceholder())) {
            pom.getDependency().replaceVersion(projectVersion);
        }
        for (Dependency dependency : pom.getDependencies()) {
            String oldDependencyString = dependency.toString();
            boolean isPropertyReplaced = false;
            if (dependency.getVersion().trim().startsWith("${") && dependency.getVersion().trim().endsWith("}")) {
                String propertyPlaceholder = dependency.getVersion().trim().substring(2, dependency.getVersion().trim().length() - 1);
                if (pom.existProperty(propertyPlaceholder)) {
                    dependency.replaceVersion(pom.getProperty(propertyPlaceholder));
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

        for (Pom pomModule : pom.getModulesPoms()) {
            Map<String,String> propertiesCopy = new HashMap<>();
            propertiesCopy.putAll(properties);
            propertiesCopy.putAll(pom.getProperties());
            replacePropertyVariablesInPom(pomModule, projectGroupId, projectVersion, propertiesCopy);
        }
    }

    void printPropertiesInPom(final Pom pom) {
        if (options.isOption(OPTION.VERBOSE)) {
            if ( ! pom.getProperties().isEmpty()) {
                StringBuilder properties = new StringBuilder();
                properties.append("    Properties: ");
                for (String key : pom.getProperties().keySet()) {
                    String value = pom.getProperty(key);
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
