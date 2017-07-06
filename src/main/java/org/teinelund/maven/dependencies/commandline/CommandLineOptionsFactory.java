package org.teinelund.maven.dependencies.commandline;


public interface CommandLineOptionsFactory {
    public CommandLineOptions createCommandLineOption(String[] args);
}
