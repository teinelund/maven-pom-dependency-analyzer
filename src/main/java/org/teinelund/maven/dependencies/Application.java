package org.teinelund.maven.dependencies;

import org.teinelund.maven.dependencies.commandline.*;
import org.teinelund.maven.dependencies.logic.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Application implements InformationSink {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        Application application = new Application(args);
        application.start();
    }

    public static String getDefaultHelpText() {
        return " Type '-h' or '--help' to display the help page.";
    }
    public static String getNewLine() {
        return System.getProperty("line.separator");
    }
    public static String getGroupIdPlaceholder() {
        return "${project.groupId}";
    }

    public static String getVersionPlaceholder() {
        return "${project.version}";
    }

    public static String getDependencyPlaceholder() {
        return "?";
    }

    private String[] args;


    public Application(String[] args) {
        this.args = args;
    }

    public void start() throws IOException, SAXException, ParserConfigurationException {
        CommandLineOptionsFactory factory = new CommandLineOptionsFactoryImp();
        CommandLineOptions options = factory.createCommandLineOption(this.args);
        CommandLineValidator validator = new CommandLineValidator();
        validator.validate(options);
        MavenProjectDirectoryPathsVerifier verifier = wireApplication(options);
        //PomFileDependencyLinker linker = new PomFileDependencyLinker(options);
        if (options.isOption(OPTION.HELP) || options.isOption(OPTION.VERSION)) {
            if (options.isOption(OPTION.HELP)) {
                options.printHelp();
            }
            if (options.isOption(OPTION.VERSION)) {
                String version = Application.class.getPackage().getImplementationVersion();
                System.out.println("Maven Repositories Dependency Analyser, version " + version + ".");
                System.out.println("Copyright (C) 2016 Henrik Teinelund.");
            }
        }
        else {
            verifier.analyzePaths();
        }
    }

    private MavenProjectDirectoryPathsVerifier wireApplication(CommandLineOptions options) {
        MavenPomEntityFactory factory = new MavenPomEntityFactoryImpl();
        ReplacePropertyPlaceholder replacePropertyPlaceholder = factory.createReplacePropertyPlaceholder(this, options, null);
        MavenPomFileHierarchyOrganizer mavenPomFileHierarchyOrganizer = factory.createMavenPomFileHierarchyOrganizer(this, options, replacePropertyPlaceholder);
        MavenPomFileReader mavenPomFileReader = factory.createMavenPomFileReader(this, options, mavenPomFileHierarchyOrganizer);
        PathExcludeFilter excludeFilter = factory.createPathExcludeFilter(this, options, mavenPomFileReader);
        MavenPomFileFetcher pomFileFetcher = factory.createMavenPomFileFetcher(this, options, excludeFilter);
        MavenProjectDirectoryPathsVerifier verifier = factory.createMavenProjectDirectoryPathsVerifier(this, options, pomFileFetcher);

        return verifier;
    }

    @Override
    public void information(String informationMessage) {
        System.out.println("Info: " + informationMessage);
    }

    @Override
    public void warning(String warningMessage) {
        System.out.println("Warning: " + warningMessage);
    }

    @Override
    public void error(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }
}
