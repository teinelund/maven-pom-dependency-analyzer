package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FetchMavenPomFilesLinker implements Linker<List<Path>> {

    private CommandLineOptions options;
    private Linker<List<Path>> linker;

    public FetchMavenPomFilesLinker(final CommandLineOptions options, final Linker<List<Path>> linker) {
        this.options = options;
        this.linker = linker;
    }

    @Override
    public void process(List<Path> input) throws ParserConfigurationException, SAXException, IOException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Fetch Maven Pom Files...");
        }
        List<Path> pomfilePathList = new LinkedList<>();
        for (Path path : input) {
            filterOutMavenPomFileFromDirectory(path, pomfilePathList);
        }
        if (this.options.isOption(OPTION.VERBOSE)) {
            System.out.println("Paths to pom files:");
            for (Path path : pomfilePathList) {
                System.out.println("  " + path.toString());
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("End Fetch Maven Pom Files.");
        }
        this.linker.process(pomfilePathList);
    }

    void filterOutMavenPomFileFromDirectory(Path path, List<Path> pomfilePathList) {
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
            System.err.println(x);
        }
    }
}
