package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;

import java.util.List;

public class CommandLineOptionsMock implements CommandLineOptions {

    private List<String> includeRepoPathNames;
    private List<String> excludeRepoPathNames;
    private OPTION option;

    public CommandLineOptionsMock(List<String> includeRepoPathNames, List<String> excludeRepoPathNames, OPTION option) {
        this.option = option;
        this.includeRepoPathNames = includeRepoPathNames;
        this.excludeRepoPathNames = excludeRepoPathNames;
    }

    @Override
    public boolean isOption(OPTION option) {
        return this.option == option ? true : false;
    }

    @Override
    public List<String> getIncludeRepoPathNames() {
        return includeRepoPathNames;
    }

    @Override
    public List<String> getExcludeRepoPathNames() {
        return this.excludeRepoPathNames;
    }

    @Override
    public List<String> getGroupIdParts() {
        return null;
    }

    @Override
    public void printHelp() {

    }
}
