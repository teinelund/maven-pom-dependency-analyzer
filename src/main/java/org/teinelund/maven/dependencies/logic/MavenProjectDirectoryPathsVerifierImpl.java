package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

class MavenProjectDirectoryPathsVerifierImpl implements MavenProjectDirectoryPathsVerifier {

    private InformationSink informationSink;
    private CommandLineOptions options;
    private MavenPomFileFetcher mavenPomFileFetcher;

    public MavenProjectDirectoryPathsVerifierImpl(InformationSink informationSink, final CommandLineOptions options,
                                                  MavenPomFileFetcher mavenPomFileFetcher) {
        this.informationSink = informationSink;
        this.options = options;
        this.mavenPomFileFetcher = mavenPomFileFetcher;
    }

    @Override
    public void analyzePaths() throws ParserConfigurationException, SAXException, IOException {
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Verify Paths...");
        }
        Collection<Path> includeRepoPathList = new HashSet<Path>();
        boolean isErrors = false;
        for (String pathName : options.getIncludeRepoPathNames()) {
            Path path = Paths.get(pathName);
            if ( ! Files.exists(path) ) {
                informationSink.error(path.toString() + "\' does not exist.");
                isErrors = true;
            }
            else if ( ! Files.isDirectory(path) ) {
                informationSink.error(path.toString() + "\' is not a directory.");
                isErrors = true;
            }
            includeRepoPathList.add(path);
        }
        if (!isErrors && options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Verify Paths.");
        }
        if (!isErrors) {
            this.mavenPomFileFetcher.fetchMavenPomFiles(includeRepoPathList);
        }
    }
}
