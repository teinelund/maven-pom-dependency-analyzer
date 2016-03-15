package org.teinelund.maven.dependencies.commandline;

import org.apache.commons.cli.*;
import org.teinelund.maven.dependencies.Application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CommandLineOptions {

    private String[] args;
    private Options options;
    private boolean isHelpOption;
    private boolean isVersionOption;
    private boolean isRepoPathNamesSet;
    private boolean isVerboseOption;
    private boolean isQuietOption;
    private List<String> repoPathNamesList;
    private boolean isExcludeRepoPathNamesSet;
    private List<String> excludeRepoPathNamesList;
    private boolean isGroupIdParts;
    private List<String> groupIdPartList;
    private String currentPathName;

    public CommandLineOptions(final String[] args) {
        this.args = args;
        this.repoPathNamesList = new LinkedList<>();
        this.excludeRepoPathNamesList = new LinkedList<>();
        this.currentPathName = System.getProperty("user.dir");
        this.groupIdPartList = new LinkedList<>();
    }

    public void parse() {
        buildCommandLineOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        try {
            CommandLine commandLine = commandLineParser.parse(this.options, this.args);
            processCommandLineOptions(commandLine);
        } catch (ParseException e) {
            System.out.println( e.getMessage() + "." + Application.getDefaultHelpText());
            System.exit(-1);
        }
    }

    public boolean isOption(final OPTION option) {
        switch(option) {
            case HELP:
                return this.isHelpOption;
            case VERSION:
                return this.isVersionOption;
            case INCLUDE_REPO_PATH:
                return this.isRepoPathNamesSet;
            case EXCLUDE_REPO_PATH:
                return this.isExcludeRepoPathNamesSet;
            case VERBOSE:
                return this.isVerboseOption;
            case QUIET:
                return this.isQuietOption;
            case GROUP_ID_PARTS:
        }
        return this.isGroupIdParts;
    }

    public List<String> getIncludeRepoPathNames() {
        return Collections.unmodifiableList(this.repoPathNamesList);
    }

    public List<String> getExcludeRepoPathNames() {
        return Collections.unmodifiableList(this.excludeRepoPathNamesList);
    }

    public List<String> getGroupIdParts() {
        return Collections.unmodifiableList(this.groupIdPartList);
    }

    public void printHelp() {
        String header = "Maven Repositories Dependency Analyser, analyzes the dependencies between several Maven repositories.\n\n";
        String footer = "";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(200, "dependency-analyzer", header, options, footer, true);
    }

    void buildCommandLineOptions() {
        Options options = new Options();
        options.addOption(Option.builder("p").longOpt("repopath").hasArg().argName("PATHS")
                .desc("Maven repository paths given by PATHS to analyze. Several paths may be given separated" +
                        " by comma \',\'. If absolut paths are not given (path started with \'/\'), " +
                        "paths are starting from current path. Paths must be directories. " +
                        "Example: \'-p repos/main,repos/feature/tse-45532\'. Mandatory.").build());
        options.addOption(Option.builder("e").longOpt("excludepath").hasArg().argName("PATHS")
                .desc("Maven repository paths given by PATHS to exclude. Several paths may be given separated" +
                        " by comma \',\'. Must not be full paths. Example: \'-e repos/main/engine,repos/main/factories\'. Optional.").build());
        options.addOption(Option.builder("g").longOpt("groupidparts").hasArg().argName("GROUP_ID_PARTS")
                .desc("GROUP_ID_PARTS are group id parts, separated with comma \',\'. All dependencies with groupIdPartList" +
                        " that starts with GROUP_ID_PART without a repo will be displayed to the console." +
                        " Pom dependencies will also be filtered with GROUP_ID_PARTS. Example 1: -g com.company1." +
                        " Example 2: -g com.company1,com.company2.engine. Optional.").build());
        options.addOption(Option.builder("V").longOpt("verbose").desc("Print verbose information. Optional.").build());
        options.addOption(Option.builder("q").longOpt("quiet").desc("Print only errors. This option wins over verbose. Optional.").build());
        options.addOption(Option.builder("h").longOpt("help").desc("Prints this page.").build());
        options.addOption(Option.builder("v").longOpt("version").desc("Show version.").build());
        this.options = options;
    }

    void processCommandLineOptions(final CommandLine commandLine) {
        if (commandLine.hasOption('h') || commandLine.hasOption("help")) {
            this.isHelpOption = true;
        }
        else if (commandLine.hasOption('v') || commandLine.hasOption("version")) {
            this.isVersionOption = true;
        }
        else {
            if (commandLine.hasOption('v') || commandLine.hasOption("verbose")) {
                this.isVerboseOption = true;
            }
            if (commandLine.hasOption('q') || commandLine.hasOption("quiet")) {
                this.isQuietOption = true;
            }
            if (commandLine.hasOption('p') || commandLine.hasOption("repopath")) {
                String repoPathNames = commandLine.getOptionValue('p');
                if (repoPathNames != null && ! repoPathNames.trim().isEmpty()) {
                    createRepoPathNameList(repoPathNames, this.repoPathNamesList, PATH_OPTION.ABSOLUT_PATH);
                    this.isRepoPathNamesSet = true;
                }
            }
            if (commandLine.hasOption('e') || commandLine.hasOption("excludepath")) {
                String repoPathNames = commandLine.getOptionValue('e');
                if (repoPathNames != null && ! repoPathNames.trim().isEmpty()) {
                    createRepoPathNameList(repoPathNames, this.excludeRepoPathNamesList, PATH_OPTION.RELATIVE_PATH);
                    this.isExcludeRepoPathNamesSet = true;
                }
            }
            if (commandLine.hasOption('g') || commandLine.hasOption("groupidparts")) {
                String groupIdParts = commandLine.getOptionValue('g');
                if (groupIdParts != null && ! groupIdParts.trim().isEmpty()) {
                    String[] groupIdPartArray = groupIdParts.trim().split(",");
                    for (String groupIdPart : groupIdPartArray) {
                        this.groupIdPartList.add(groupIdPart);
                    }
                    this.isGroupIdParts = true;
                }
            }
        }
    }

    void createRepoPathNameList(final String repoPathNames, final List<String> repoPathNamesList,
                                final PATH_OPTION pathOption) {
        String[] repoPathNamesArray = repoPathNames.trim().split(",");
        for (String repoPathName : repoPathNamesArray) {
            if (repoPathName != null && ! repoPathName.trim().isEmpty()) {
                if (pathOption == PATH_OPTION.ABSOLUT_PATH) {
                    if (repoPathName.startsWith("/")) {
                        repoPathNamesList.add(repoPathName);
                    } else {
                        repoPathNamesList.add(this.currentPathName + "/" + repoPathName);
                    }
                }
                else {
                    repoPathNamesList.add(repoPathName);
                }
            }
        }
    }


}
