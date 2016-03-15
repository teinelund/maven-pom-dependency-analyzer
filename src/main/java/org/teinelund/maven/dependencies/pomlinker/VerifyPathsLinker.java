package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class VerifyPathsLinker implements Linker<List<String>> {

    private CommandLineOptions options;
    private Linker<List<Path>> linker;

    public VerifyPathsLinker(final CommandLineOptions options, Linker<List<Path>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<String> input) throws ParserConfigurationException, SAXException, IOException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Verify Paths...");
        }
        List<Path> includeRepoPathList = new LinkedList<Path>();
        StringBuilder errors = new StringBuilder();
        boolean isErrors = false;
        for (String pathName : input) {
            Path path = Paths.get(pathName);
            if ( ! Files.exists(path) ) {
                appendErrors(path, "\' does not exist.", errors);
                isErrors = true;
            }
            else if ( ! Files.isDirectory(path) ) {
                appendErrors(path, "\' is not a directory.", errors);
                isErrors = true;
            }
            includeRepoPathList.add(path);
        }
        if (isErrors) {
            System.out.println("Errors:");
            System.out.println(errors.toString());
            System.exit(-1);
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Verify Paths.");
        }
        this.linker.process(includeRepoPathList);
    }

    void appendErrors(final Path path, final String errorMessage, final StringBuilder errors) {
        errors.append("  \'" + path.toString() + errorMessage + Application.getDefaultHelpText());
        errors.append(Application.getNewLine());
    }
}
