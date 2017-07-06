package org.teinelund.maven.dependencies.pomlinker;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.PomImpl;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PomDependenciesLinker implements Linker<List<PomImpl>> {

    private CommandLineOptions options;
    private Linker<List<PomImpl>> linker;
    private StringBuilder warnings;
    private List<Pair<PomImpl, List<Dependency>>> missingDependencies;

    public PomDependenciesLinker(final CommandLineOptions options, final Linker<List<PomImpl>> linker,
                                 final StringBuilder warnings, List<Pair<PomImpl, List<Dependency>>> missingDependencies) {
        this.options = options;
        this.linker = linker;
        this.warnings = warnings;
        this.missingDependencies = missingDependencies;
    }

    @Override
    public void process(List<PomImpl> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Connects PomImpl with PomImpl Dependencies...");
        }
        Map<String, List<PomImpl>> pomMap = PomFileDependencyLinker.buildPomMap(input, this.warnings);
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("  Poms in pom map:");
            for (String artifactId : pomMap.keySet()) {
                System.out.println("    " + artifactId);
                for (PomImpl pomImpl : pomMap.get(artifactId)) {
                    System.out.println("    + " + pomImpl.getDependency().toString());
                }
            }
        }
        for (PomImpl pomImpl : input) {
            List<Dependency> listOfMissingDependencies = new LinkedList<>();
            for (Dependency dependency : pomImpl.getDependencies()) {
                if (pomMap.containsKey(dependency.getArtifactId())) {
                    List<PomImpl> pomImplMapList = pomMap.get(dependency.getArtifactId());
                    if (pomImplMapList.size() == 0) {
                        System.out.println(buildErrorString("Found empty list of pomImpl files in pomImpl map for:",
                                pomImpl, "Dependency with no entry in list of pomImpl:", dependency));
                        System.exit(-1);
                    }
                    if (pomImplMapList.size() == 1) {
                        PomImpl pomFromPomImplMapList = pomImplMapList.get(0);
                        if (!pomFromPomImplMapList.getDependency().getGroupId().equals(dependency.getGroupId())) {
                            System.out.println(buildErrorString("Error: Did not find pomImpl dependency:",
                                    pomImpl, "Dependency with no entry in list of pomImpl:", dependency));
                            System.exit(-1);
                        }
                        pomImpl.addPomDependency(pomFromPomImplMapList);
                        pomFromPomImplMapList.addPomThatDependsOnThisPom(pomImpl);
                        if (!pomFromPomImplMapList.getDependency().getVersion().equals(dependency.getVersion())) {
                            AddWarning("Did not find the correct version of dependency:", dependency, pomImpl,
                                    pomFromPomImplMapList, warnings);
                        }
                    } else {
                        boolean foundCorrectPom = false;
                        PomImpl pomImplWithDifferentVersion = null;
                        for (PomImpl pomFromPomImplMapList : pomImplMapList) {
                            if (pomFromPomImplMapList.getDependency().getGroupId().equals(dependency.getGroupId())) {
                                pomImplWithDifferentVersion = pomFromPomImplMapList;
                                if (pomFromPomImplMapList.getDependency().getVersion().equals(dependency.getVersion())) {
                                    foundCorrectPom = true;
                                    pomImplWithDifferentVersion = null;
                                    pomImpl.addPomDependency(pomFromPomImplMapList);
                                    pomFromPomImplMapList.addPomThatDependsOnThisPom(pomImpl);
                                }
                            }
                        }
                        if (!foundCorrectPom) {
                            if (pomImplWithDifferentVersion != null) {
                                AddWarning("Did not find the correct version of dependency:", dependency, pomImpl,
                                        pomImplWithDifferentVersion, warnings);
                                pomImpl.addPomDependency(pomImplWithDifferentVersion);
                                pomImplWithDifferentVersion.addPomThatDependsOnThisPom(pomImpl);
                            } else {
                                System.out.println(buildErrorString("Error: Did not find pomImpl dependency:",
                                        pomImpl, "Dependency with no entry in list of pomImpl:", dependency));
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
                Pair<PomImpl, List<Dependency>> pair = new ImmutablePair<>(pomImpl, listOfMissingDependencies);
                this.missingDependencies.add(pair);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Connects PomImpl with PomImpl Dependencies.");
        }
        this.linker.process(input);
    }

    String buildErrorString(final String errorMessage, final PomImpl pomImpl, final String errorDependencyMessage,
                            final Dependency dependency) {
        StringBuilder error = new StringBuilder();
        error.append(errorMessage); error.append(Application.getNewLine());
        error.append(pomImpl.toString()); error.append(Application.getNewLine());
        error.append(errorDependencyMessage); error.append(Application.getNewLine());
        error.append(dependency.toString()); error.append(Application.getNewLine());
        return error.toString();
    }

    void AddWarning(final String warningMessage, final Dependency dependency, final PomImpl pomImpl, final PomImpl pomFromPomImplMap,
                    StringBuilder warnings) {
        warnings.append(warningMessage); warnings.append(Application.getNewLine());
        warnings.append("  Dependency:"); warnings.append(Application.getNewLine());
        warnings.append("    " + dependency.toString()); warnings.append(Application.getNewLine());
        warnings.append("    PomImpl with dependency:"); warnings.append(Application.getNewLine());
        warnings.append("      " + pomImpl.getDependency().toString()); warnings.append(Application.getNewLine());
        warnings.append("      " + pomImpl.getPathToPomFile().toString()); warnings.append(Application.getNewLine());
        warnings.append("    PomImpl from pomImpl map:"); warnings.append(Application.getNewLine());
        warnings.append("      " + pomFromPomImplMap.getDependency().toString()); warnings.append(Application.getNewLine());
        warnings.append("      " + pomFromPomImplMap.getPathToPomFile().toString()); warnings.append(Application.getNewLine());
    }
}
