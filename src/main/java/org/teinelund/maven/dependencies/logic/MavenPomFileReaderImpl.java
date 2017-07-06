package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.teinelund.maven.dependencies.domain.PomImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class MavenPomFileReaderImpl implements MavenPomFileReader {

    private InformationSink informationSink;
    private CommandLineOptions options;
    private MavenPomFileHierarchyOrganizer mavenPomFileHierarchyOrganizer;

    public MavenPomFileReaderImpl(InformationSink informationSink, CommandLineOptions options,
                                  MavenPomFileHierarchyOrganizer mavenPomFileHierarchyOrganizer) {
        this.informationSink = informationSink;
        this.options = options;
        this.mavenPomFileHierarchyOrganizer = mavenPomFileHierarchyOrganizer;
    }

    @Override
    public void readPomFiles(Collection<Path> pomXmlFiles) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Read PomImpl Files From Disk...");
        }
        List<PomImpl> pomImplList = new LinkedList<>();
        for (Path path : pomXmlFiles) {
            PomBuilder builder = new PomBuilder(Files.newInputStream(path, StandardOpenOption.READ), path);
            builder.build();
            PomImpl pomImpl = builder.getResult();
            if (options.isOption(OPTION.GROUP_ID_PARTS)) {
                List<String> dependencyFilterList = options.getGroupIdParts();
                pomImpl.filterDependencyList(dependencyFilterList);
            }
            pomImplList.add(pomImpl);
        }
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Parsed pom.xml files:");
            for (PomImpl pomImpl : pomImplList) {
                System.out.print(pomImpl.toString());
            }
        }
        this.mavenPomFileHierarchyOrganizer.organizeMavenPomFiles(pomImplList);
    }
}
