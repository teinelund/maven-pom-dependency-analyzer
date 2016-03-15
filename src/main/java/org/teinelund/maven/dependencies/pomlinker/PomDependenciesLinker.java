package org.teinelund.maven.dependencies.pomlinker;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.Dependency;
import org.teinelund.maven.dependencies.Pom;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PomDependenciesLinker implements Linker<List<Pom>> {

    private CommandLineOptions options;
    private Linker<List<Pom>> linker;
    private StringBuilder warnings;
    private List<Pair<Pom, List<Dependency>>> missingDependencies;

    public PomDependenciesLinker(final CommandLineOptions options, final Linker<List<Pom>> linker,
                                 final StringBuilder warnings, List<Pair<Pom, List<Dependency>>> missingDependencies) {
        this.options = options;
        this.linker = linker;
        this.warnings = warnings;
        this.missingDependencies = missingDependencies;
    }

    @Override
    public void process(List<Pom> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Connects Pom with Pom Dependencies...");
        }
        Map<String, List<Pom>> pomMap = PomFileDependencyLinker.buildPomMap(input, this.warnings);
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("  Poms in pom map:");
            for (String artifactId : pomMap.keySet()) {
                System.out.println("    " + artifactId);
                for (Pom pom : pomMap.get(artifactId)) {
                    System.out.println("    + " + pom.getDependency().toString());
                }
            }
        }
        for (Pom pom : input) {
            List<Dependency> listOfMissingDependencies = new LinkedList<>();
            for (Dependency dependency : pom.getDependencies()) {
                if (pomMap.containsKey(dependency.getArtifactId())) {
                    List<Pom> pomMapList = pomMap.get(dependency.getArtifactId());
                    if (pomMapList.size() == 0) {
                        System.out.println(buildErrorString("Found empty list of pom files in pom map for:",
                                pom, "Dependency with no entry in list of pom:", dependency));
                        System.exit(-1);
                    }
                    if (pomMapList.size() == 1) {
                        Pom pomFromPomMapList = pomMapList.get(0);
                        if (!pomFromPomMapList.getDependency().getGroupId().equals(dependency.getGroupId())) {
                            System.out.println(buildErrorString("Error: Did not find pom dependency:",
                                    pom, "Dependency with no entry in list of pom:", dependency));
                            System.exit(-1);
                        }
                        pom.addPomDependency(pomFromPomMapList);
                        pomFromPomMapList.addPomThatDependsOnThisPom(pom);
                        if (!pomFromPomMapList.getDependency().getVersion().equals(dependency.getVersion())) {
                            AddWarning("Did not find the correct version of dependency:", dependency, pom,
                                    pomFromPomMapList, warnings);
                        }
                    } else {
                        boolean foundCorrectPom = false;
                        Pom pomWithDifferentVersion = null;
                        for (Pom pomFromPomMapList : pomMapList) {
                            if (pomFromPomMapList.getDependency().getGroupId().equals(dependency.getGroupId())) {
                                pomWithDifferentVersion = pomFromPomMapList;
                                if (pomFromPomMapList.getDependency().getVersion().equals(dependency.getVersion())) {
                                    foundCorrectPom = true;
                                    pomWithDifferentVersion = null;
                                    pom.addPomDependency(pomFromPomMapList);
                                    pomFromPomMapList.addPomThatDependsOnThisPom(pom);
                                }
                            }
                        }
                        if (!foundCorrectPom) {
                            if (pomWithDifferentVersion != null) {
                                AddWarning("Did not find the correct version of dependency:", dependency, pom,
                                        pomWithDifferentVersion, warnings);
                                pom.addPomDependency(pomWithDifferentVersion);
                                pomWithDifferentVersion.addPomThatDependsOnThisPom(pom);
                            } else {
                                System.out.println(buildErrorString("Error: Did not find pom dependency:",
                                        pom, "Dependency with no entry in list of pom:", dependency));
                                System.exit(-1);
                            }
                        }
                    }
                }
                else {
                    if (options.isOption(OPTION.GROUP_ID_PARTS)) {
                        for (String groupIdPart : options.getGroupIdParts())
                            if (dependency.getGroupId().startsWith(groupIdPart)) {
                                listOfMissingDependencies.add(dependency);
                        }
                    }
                }
            }
            if ( ! listOfMissingDependencies.isEmpty() ) {
                Pair<Pom, List<Dependency>> pair = new ImmutablePair<>(pom, listOfMissingDependencies);
                this.missingDependencies.add(pair);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Connects Pom with Pom Dependencies.");
        }
        this.linker.process(input);
    }

    String buildErrorString(final String errorMessage, final Pom pom, final String errorDependencyMessage,
                            final Dependency dependency) {
        StringBuilder error = new StringBuilder();
        error.append(errorMessage); error.append(Application.getNewLine());
        error.append(pom.toString()); error.append(Application.getNewLine());
        error.append(errorDependencyMessage); error.append(Application.getNewLine());
        error.append(dependency.toString()); error.append(Application.getNewLine());
        return error.toString();
    }

    void AddWarning(final String warningMessage, final Dependency dependency, final Pom pom, final Pom pomFromPomMap,
                    StringBuilder warnings) {
        warnings.append(warningMessage); warnings.append(Application.getNewLine());
        warnings.append("  Dependency:"); warnings.append(Application.getNewLine());
        warnings.append("    " + dependency.toString()); warnings.append(Application.getNewLine());
        warnings.append("    Pom with dependency:"); warnings.append(Application.getNewLine());
        warnings.append("      " + pom.getDependency().toString()); warnings.append(Application.getNewLine());
        warnings.append("      " + pom.getPathToPomFile().toString()); warnings.append(Application.getNewLine());
        warnings.append("    Pom from pom map:"); warnings.append(Application.getNewLine());
        warnings.append("      " + pomFromPomMap.getDependency().toString()); warnings.append(Application.getNewLine());
        warnings.append("      " + pomFromPomMap.getPathToPomFile().toString()); warnings.append(Application.getNewLine());
    }
}
