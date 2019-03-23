package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class PathExcludeFilterImpl implements PathExcludeFilter {

    private InformationSink informationSink;
    private CommandLineOptions options;
    private MavenPomFileReader mavenPomFileReader;

    public PathExcludeFilterImpl(InformationSink informationSink, final CommandLineOptions options, MavenPomFileReader mavenPomFileReader) {
        this.informationSink = informationSink;
        this.options = options;
        this.mavenPomFileReader = mavenPomFileReader;
    }

    @Override
    public void excludePaths(Collection<Path> pomXmlFiles) throws ParserConfigurationException, SAXException, IOException {
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Filter Excluded Parths...");
        }
        List<Path> filteredPomFilePathList;
        if ( this.options.getExcludeRepoPathNames().isEmpty()) {
            if (options.isOption(OPTION.VERBOSE)) {
                informationSink.information("No paths excluded.");
            }
            filteredPomFilePathList = new LinkedList<Path>(pomXmlFiles);
        }
        else {
            int nrOfExcludedFiles = 0;
            filteredPomFilePathList = new LinkedList<Path>();
            for (Path path : pomXmlFiles) {
                boolean foundExcludePath = false;
                for (String excludePathName : this.options.getExcludeRepoPathNames()) {
                    String pathName = path.toString();
                    pathName = pathName.replaceAll("\\\\", "/");
                    if (pathName.contains(excludePathName)) {
                        foundExcludePath = true;
                        if (options.isOption(OPTION.VERBOSE)) {
                            if (nrOfExcludedFiles == 0)
                                informationSink.information("Found the following pom.xml files that will be excluded:");
                            informationSink.information("  " + path.toString());
                        }
                        break;
                    }
                }
                if (! foundExcludePath)
                    filteredPomFilePathList.add(path);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("End Excluded Parths.");
        }
        this.mavenPomFileReader.readPomFiles(filteredPomFilePathList);
    }
}
