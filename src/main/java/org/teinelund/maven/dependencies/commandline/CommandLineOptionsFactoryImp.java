package org.teinelund.maven.dependencies.commandline;

public class CommandLineOptionsFactoryImp implements CommandLineOptionsFactory {
    @Override
    public CommandLineOptions createCommandLineOption(String[] args) {
        CommandLineOptionsImpl commandLineOptions = new CommandLineOptionsImpl(args);
        commandLineOptions.parse();
        return commandLineOptions;
    }
}
