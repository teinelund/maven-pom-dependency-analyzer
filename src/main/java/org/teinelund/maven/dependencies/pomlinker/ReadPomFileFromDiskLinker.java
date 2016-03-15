package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Pom;
import org.teinelund.maven.dependencies.PomBuilder;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public class ReadPomFileFromDiskLinker implements Linker<List<Path>> {

    private CommandLineOptions options;
    private Linker<List<Pom>> linker;

    public ReadPomFileFromDiskLinker(final CommandLineOptions options, final Linker<List<Pom>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<Path> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Read Pom Files From Disk...");
        }
        List<Pom> pomList = new LinkedList<>();
        for (Path path : input) {
            PomBuilder builder = new PomBuilder(Files.newInputStream(path, StandardOpenOption.READ), path);
            builder.build();
            Pom pom = builder.getResult();
            if (options.isOption(OPTION.GROUP_ID_PARTS)) {
                List<String> dependencyFilterList = options.getGroupIdParts();
                pom.filterDependencyList(dependencyFilterList);
            }
            pomList.add(pom);
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Parsed pom.xml files:");
            for (Pom pom : pomList) {
                System.out.print(pom.toString());
            }
        }
        this.linker.process(pomList);
    }
}
