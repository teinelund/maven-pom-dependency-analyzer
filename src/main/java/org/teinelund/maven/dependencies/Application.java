package org.teinelund.maven.dependencies;

import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.CommandLineValidator;
import org.teinelund.maven.dependencies.pomlinker.PomFileDependencyLinker;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Application {

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
        CommandLineOptions options = new CommandLineOptions(this.args);
        options.parse();
        CommandLineValidator validator = new CommandLineValidator();
        validator.validate(options);
        PomFileDependencyLinker linker = new PomFileDependencyLinker(options);
        linker.printHelpOrVersionIfOptionsIsTrue();
        linker.linkPomFiles();
    }

}
