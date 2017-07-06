package org.teinelund.maven.dependencies.commandline;

import java.util.List;

public interface CommandLineOptions {
    public boolean isOption(final OPTION option);
    List<String> getIncludeRepoPathNames();
    List<String> getExcludeRepoPathNames();
    List<String> getGroupIdParts();
    void printHelp();
}
