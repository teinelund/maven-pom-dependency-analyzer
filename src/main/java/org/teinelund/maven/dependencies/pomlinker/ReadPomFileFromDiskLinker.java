package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.domain.PomImpl;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class ReadPomFileFromDiskLinker implements Linker<List<Path>> {

    private CommandLineOptions options;
    private Linker<List<PomImpl>> linker;

    public ReadPomFileFromDiskLinker(final CommandLineOptions options, final Linker<List<PomImpl>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<Path> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Read PomImpl Files From Disk...");
        }
        List<PomImpl> pomImplList = new LinkedList<>();
        for (Path path : input) {
            /*PomBuilder builder = new PomBuilder(Files.newInputStream(path, StandardOpenOption.READ), path);
            builder.build();
            PomImpl pomImpl = builder.getResult();
            if (options.isOption(OPTION.GROUP_ID_PARTS)) {
                List<String> dependencyFilterList = options.getGroupIdParts();
                pomImpl.filterDependencyList(dependencyFilterList);
            }
            pomImplList.add(pomImpl);*/
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Parsed pom.xml files:");
            for (PomImpl pomImpl : pomImplList) {
                System.out.print(pomImpl.toString());
            }
        }
        this.linker.process(pomImplList);
    }
}
