package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

class MavenPomFileFetcherImp implements MavenPomFileFetcher {

    private InformationSink informationSink;
    private CommandLineOptions options;
    private PathExcludeFilter pathExcludeFilter;

    public MavenPomFileFetcherImp(InformationSink informationSink, final CommandLineOptions options, PathExcludeFilter pathExcludeFilter) {
        this.informationSink = informationSink;
        this.options = options;
        this.pathExcludeFilter = pathExcludeFilter;
    }

    @Override
    public void fetchMavenPomFiles(Collection<Path> mavenProjectDirectories) throws IOException, SAXException, ParserConfigurationException {
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Fetch Maven PomImpl Files...");
        }
        HashSet<Path> pomfilePaths = new HashSet<>();
        for (Path mavenProjectDirectory : mavenProjectDirectories) {
            filterOutMavenPomFileFromDirectory(mavenProjectDirectory, pomfilePaths);
        }
        if (this.options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Paths to pom files:");
            for (Path path : pomfilePaths) {
                informationSink.information("  " + path.toString());
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("End Fetch Maven PomImpl Files.");
        }
        pathExcludeFilter.excludePaths(pomfilePaths);
    }

    void filterOutMavenPomFileFromDirectory(Path path, HashSet<Path> pomfilePathList) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry: stream) {
                if ( Files.isDirectory(entry) ) {
                    if ( ! entry.getFileName().toString().startsWith(".") && ! entry.getFileName().toString().equals("target")) {
                        filterOutMavenPomFileFromDirectory(entry, pomfilePathList);
                    }
                }
                else {
                    if ( entry.getFileName().toString().equals("pom.xml") ) {
                        pomfilePathList.add(entry);
                    }
                }
            }
        } catch (IOException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can // only be thrown by newDirectoryStream.
            informationSink.error(x.toString());
        }
    }
}
