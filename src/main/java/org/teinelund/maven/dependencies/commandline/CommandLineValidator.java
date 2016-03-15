package org.teinelund.maven.dependencies.commandline;

import org.teinelund.maven.dependencies.Application;

public class CommandLineValidator {
    public void validate(final CommandLineOptions options) {
        if (options.isOption(OPTION.HELP) || options.isOption(OPTION.VERSION)) {
            return;
        }
        if ( ! options.isOption(OPTION.INCLUDE_REPO_PATH)) {
            System.out.println("Option \'-p PATHS\' or \'--repopath PATHS\' is mandatory." + Application.getDefaultHelpText());
            System.exit(-1);
        }
    }
}
