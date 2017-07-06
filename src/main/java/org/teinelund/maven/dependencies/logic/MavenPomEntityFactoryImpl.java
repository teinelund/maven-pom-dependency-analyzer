package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;

public class MavenPomEntityFactoryImpl implements MavenPomEntityFactory {

    @Override
    public MavenProjectDirectoryPathsVerifier createMavenProjectDirectoryPathsVerifier(InformationSink informationSink,
                                                                                       final CommandLineOptions options,
                                                                                       MavenPomFileFetcher mavenPomFileFetcher) {
        return new MavenProjectDirectoryPathsVerifierImpl(informationSink, options, mavenPomFileFetcher);
    }

    @Override
    public MavenPomFileFetcher createMavenPomFileFetcher(InformationSink informationSink, CommandLineOptions options,
                                                         PathExcludeFilter pathExcludeFilter) {
        return new MavenPomFileFetcherImp(informationSink, options, pathExcludeFilter);
    }

    @Override
    public PathExcludeFilter createPathExcludeFilter(InformationSink informationSink, CommandLineOptions options, MavenPomFileReader mavenPomFileReader) {
        return new PathExcludeFilterImpl(informationSink, options, mavenPomFileReader);
    }

    @Override
    public MavenPomFileReader createMavenPomFileReader(InformationSink informationSink, CommandLineOptions options, MavenPomFileHierarchyOrganizer mavenPomFileHierarchyOrganizer) {
        return new MavenPomFileReaderImpl(informationSink, options, mavenPomFileHierarchyOrganizer);
    }

    @Override
    public MavenPomFileHierarchyOrganizer createMavenPomFileHierarchyOrganizer(InformationSink informationSink, CommandLineOptions options, ReplacePropertyPlaceholder replacePropertyPlaceholder) {
        return new MavenPomFileHierarchyOrganizerImpl(informationSink, options, replacePropertyPlaceholder);
    }

    @Override
    public ReplacePropertyPlaceholder createReplacePropertyPlaceholder(InformationSink informationSink, CommandLineOptions options, MavenPomFileConnector mavenPomFileConnector) {
        return new ReplacePropertyPlaceholderImpl(informationSink, options, mavenPomFileConnector);
    }
}
