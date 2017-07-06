package org.teinelund.maven.dependencies.pomlinker;

import org.apache.commons.lang3.tuple.Pair;
import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.PomImpl;
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

    public void printHelpOrVersion() {
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
            List<Pair<PomImpl, List<Dependency>>> missingDependencies = new LinkedList<>();
            Linker<List<PomImpl>> printPomsLinker = new PrintPomsLinker(options, missingDependencies);
            Linker<List<PomImpl>> pomDependenciesLinker = new PomDependenciesLinker(options, printPomsLinker, warnings, missingDependencies);
            Linker<List<PomImpl>> replacePropertyPlaceholderLinker = new ReplacePropertyPlaceholderLinker(options, pomDependenciesLinker);
            Linker<List<PomImpl>> pomModuleLinker = new PomModuleLinker(options, replacePropertyPlaceholderLinker,warnings);
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

    public static Map<String, List<PomImpl>> buildPomMap(final List<PomImpl> pomImplList, final StringBuilder warnings) {
        Map<String, List<PomImpl>> pomMap = new HashMap<>();
        for (PomImpl pomImpl : pomImplList) {
            if (pomMap.containsKey(pomImpl.getDependency().getArtifactId())) {
                List<PomImpl> pomImplMapList = pomMap.get(pomImpl.getDependency().getArtifactId());
                checkPomListForSamePom(pomImplMapList, pomImpl, warnings);
                pomImplMapList.add(pomImpl);
            }
            else {
                List<PomImpl> pomImplMapList = new LinkedList<>();
                pomImplMapList.add(pomImpl);
                pomMap.put(pomImpl.getDependency().getArtifactId(), pomImplMapList);
            }
        }
        return pomMap;
    }

    public static void checkPomListForSamePom(final List<PomImpl> pomImplMapList, final PomImpl pomImpl, final StringBuilder warnings) {
        for (PomImpl pomFromPomImplMapList : pomImplMapList) {
            if (pomImpl.getDependency().getGroupId().equals(pomFromPomImplMapList.getDependency().getGroupId())) {
                warnings.append("Found two pomImpl files with the same groupId and artifactId."); warnings.append(Application.getNewLine());
                warnings.append("  " + pomImpl.toString()); warnings.append(Application.getNewLine());
                warnings.append("  " + pomFromPomImplMapList.toString()); warnings.append(Application.getNewLine());
            }
        }
    }

}
