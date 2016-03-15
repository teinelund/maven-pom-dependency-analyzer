package org.teinelund.maven.dependencies.pomlinker;

import org.apache.commons.lang3.tuple.Pair;
import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.ApplicationException;
import org.teinelund.maven.dependencies.Dependency;
import org.teinelund.maven.dependencies.Pom;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PomFileDependencyLinker {

    private CommandLineOptions options;

    public PomFileDependencyLinker(final CommandLineOptions options) {
        this.options = options;
    }

    public void printHelpOrVersionIfOptionsIsTrue() {
        if (options.isOption(OPTION.HELP)) {
            options.printHelp();
        }
        if (options.isOption(OPTION.VERSION)) {
            String version = Application.class.getPackage().getImplementationVersion();
            System.out.println("Maven Repositories Dependency Analyser, version " + version + ".");
            System.out.println("Copyright (C) 2016 Henrik Teinelund.");
        }
    }

    public void linkPomFiles() throws IOException, SAXException, ParserConfigurationException {
        if (!options.isOption(OPTION.HELP) && !options.isOption(OPTION.VERSION)) {
            StringBuilder warnings = new StringBuilder();
            List<Pair<Pom, List<Dependency>>> missingDependencies = new LinkedList<>();
            Linker<List<Pom>> printPomsLinker = new PrintPomsLinker(options, missingDependencies);
            Linker<List<Pom>> pomDependenciesLinker = new PomDependenciesLinker(options, printPomsLinker, warnings, missingDependencies);
            Linker<List<Pom>> replacePropertyPlaceholderLinker = new ReplacePropertyPlaceholderLinker(options, pomDependenciesLinker);
            Linker<List<Pom>> pomModuleLinker = new PomModuleLinker(options, replacePropertyPlaceholderLinker,warnings);
            Linker<List<Path>> readPomFileFromDiskLinker = new ReadPomFileFromDiskLinker(options, pomModuleLinker);
            Linker<List<Path>> filterExcludedPathsFilter = new FilterExcludedPathsLinker(options, readPomFileFromDiskLinker);
            Linker<List<Path>> fetchMavenPomFilesLinker = new FetchMavenPomFilesLinker(options, filterExcludedPathsFilter);
            Linker<List<String>> verifyPathsLinker = new VerifyPathsLinker(options, fetchMavenPomFilesLinker);

            verifyPathsLinker.process(options.getIncludeRepoPathNames());

            if ( ! options.isOption(OPTION.QUIET) ) {
                if (warnings.length() > 0) {
                    System.out.println("Warning:");
                    System.out.println(warnings.toString());
                }
            }
        }
    }

    public static Map<String, List<Pom>> buildPomMap(final List<Pom> pomList, final StringBuilder warnings) {
        Map<String, List<Pom>> pomMap = new HashMap<>();
        for (Pom pom : pomList) {
            if (pomMap.containsKey(pom.getDependency().getArtifactId())) {
                List<Pom> pomMapList = pomMap.get(pom.getDependency().getArtifactId());
                checkPomListForSamePom(pomMapList, pom, warnings);
                pomMapList.add(pom);
            }
            else {
                List<Pom> pomMapList = new LinkedList<>();
                pomMapList.add(pom);
                pomMap.put(pom.getDependency().getArtifactId(), pomMapList);
            }
        }
        return pomMap;
    }

    public static void checkPomListForSamePom(final List<Pom> pomMapList, final Pom pom, final StringBuilder warnings) {
        for (Pom pomFromPomMapList : pomMapList) {
            if (pom.getDependency().getGroupId().equals(pomFromPomMapList.getDependency().getGroupId())) {
                warnings.append("Found two pom files with the same groupId and artifactId."); warnings.append(Application.getNewLine());
                warnings.append("  " + pom.toString()); warnings.append(Application.getNewLine());
                warnings.append("  " + pomFromPomMapList.toString()); warnings.append(Application.getNewLine());
            }
        }
    }

}
