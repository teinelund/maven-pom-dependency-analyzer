package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;

public interface MavenPomEntityFactory {
    public MavenProjectDirectoryPathsVerifier createMavenProjectDirectoryPathsVerifier(InformationSink informationSink,
                                                                                       final CommandLineOptions options,
                                                                                       MavenPomFileFetcher mavenPomFileFetcher);

    public MavenPomFileFetcher createMavenPomFileFetcher(InformationSink informationSink,
                                                         final CommandLineOptions options,
                                                         PathExcludeFilter pathExcludeFilter);

    public PathExcludeFilter createPathExcludeFilter(InformationSink informationSink,
                                                     final CommandLineOptions options,
                                                     MavenPomFileReader mavenPomFileReader);

    public MavenPomFileReader createMavenPomFileReader(InformationSink informationSink,
                                                       final CommandLineOptions options,
                                                       MavenPomFileHierarchyOrganizer mavenPomFileHierarchyOrganizer);

    public MavenPomFileHierarchyOrganizer createMavenPomFileHierarchyOrganizer(InformationSink informationSink,
                                                                               CommandLineOptions options,
                                                                               ReplacePropertyPlaceholder replacePropertyPlaceholder);

    public ReplacePropertyPlaceholder createReplacePropertyPlaceholder(InformationSink informationSink,
                                                                       final CommandLineOptions options,
                                                                       MavenPomFileConnector mavenPomFileConnector);
}
