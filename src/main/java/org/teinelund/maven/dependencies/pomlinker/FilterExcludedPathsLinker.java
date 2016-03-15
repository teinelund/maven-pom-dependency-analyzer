package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FilterExcludedPathsLinker implements Linker<List<Path>> {

    private CommandLineOptions options;
    private Linker<List<Path>> linker;

    public FilterExcludedPathsLinker(final CommandLineOptions options, final Linker<List<Path>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<Path> input) throws ParserConfigurationException, SAXException, IOException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Filter Excluded Parths...");
        }
        if ( this.options.getExcludeRepoPathNames().isEmpty()) {
            if (options.isOption(OPTION.VERBOSE)) {
                System.out.println("No paths excluded.");
            }
            this.linker.process(input);
            return;
        }
        List<Path> filteredPomFilePathList = new LinkedList<>();
        StringBuilder verboseOutput = new StringBuilder();
        for (Path path : input) {
            boolean foundExcludePath = false;
            for (String excludePathName : this.options.getExcludeRepoPathNames()) {
                if (path.toString().contains(excludePathName)) {
                    foundExcludePath = true;
                    break;
                }
            }
            if (foundExcludePath) {
                verboseOutput.append("  " + path.toString());
                verboseOutput.append(Application.getNewLine());
            } else {
                filteredPomFilePathList.add(path);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            if (verboseOutput.length() > 0) {
                System.out.println("Found the following pom.xml files that will be excluded:");
                System.out.println(verboseOutput.toString());
            }
        }
        this.linker.process(filteredPomFilePathList);
    }
}
