package org.teinelund.maven.dependencies.logic;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class MavenProjectDirectoryPathsVerifierImplTest {

    private File folderA;
    private File folderC;
    private File pomXml;
    private static MavenPomEntityFactory factory;

    @BeforeClass
    public static void initializeClass() {
        factory = new MavenPomEntityFactoryImpl();
    }

    @Rule
    final public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initialize() throws IOException {
        folderA = tempFolder.newFolder("folderA");
        folderC = tempFolder.newFolder("folderB", "folderC");
        pomXml = tempFolder.newFile("pom.xml");
    }


    @Test
    public void analyzePathsWhereIncludeRepoPathNamesContainsFolderA() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        System.out.println("analyzePathsWhereIncludeRepoPathNamesContainsFolderA.");
        File[] fileAndFolders = {folderA};
        List<String> includeRepoPathNamesList = createIncludeRepoPathNamesList(fileAndFolders);
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(includeRepoPathNamesList, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileFetcherStub mavenPomFileFetcherStub = new MavenPomFileFetcherStub();
        MavenProjectDirectoryPathsVerifier verifier =
                factory.createMavenProjectDirectoryPathsVerifier(infomrationSinkMock, commandLineOptionsMock,
                        mavenPomFileFetcherStub);
        // Test
        verifier.analyzePaths();
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(mavenPomFileFetcherStub.isMethodInvoked()).isTrue();
        assertThat(mavenPomFileFetcherStub.getIncludeRepoPaths().size()).isEqualTo(1);
    }

    private List<String> createIncludeRepoPathNamesList(File[] filesAndDirectories) {
        List<String> repoNames = new LinkedList<>();
        for (File fileOrDirectory : filesAndDirectories) {
            String pathName = fileOrDirectory.getPath();
            System.out.println("Using path name: \'" + pathName + "\'.");
            repoNames.add(pathName);
        }
        return repoNames;
    }

    @Test
    public void analyzePathsWhereIncludeRepoPathNamesContainsNonExistingDirectory() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        System.out.println("analyzePathsWhereIncludeRepoPathNamesContainsNonExistingDirectory.");
        List<String> includeRepoPathNamesList = createIncludeRepoPathNamesListContainingNonExistingDirectory();
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(includeRepoPathNamesList, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileFetcherStub mavenPomFileFetcherStub = new MavenPomFileFetcherStub();
        MavenProjectDirectoryPathsVerifier verifier =
                factory.createMavenProjectDirectoryPathsVerifier(infomrationSinkMock, commandLineOptionsMock,
                        mavenPomFileFetcherStub);
        // Test
        verifier.analyzePaths();
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(1);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(mavenPomFileFetcherStub.isMethodInvoked()).isFalse();
    }

    private List<String> createIncludeRepoPathNamesListContainingNonExistingDirectory() {
        List<String> repoNames = new LinkedList<>();
        String pathName = "/var/folders/THIS_DIRECTORY_DOES_NOT_EXIST";
        System.out.println("Using path name: \'" + pathName + "\'.");
        repoNames.add(pathName);
        return repoNames;
    }

    @Test
    public void analyzePathsWhereIncludeRepoPathNamesContainsPomXml() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        System.out.println("analyzePathsWhereIncludeRepoPathNamesContainsPomXml.");
        File[] fileAndFolders = {pomXml};
        List<String> includeRepoPathNamesList = createIncludeRepoPathNamesList(fileAndFolders);
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(includeRepoPathNamesList, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileFetcherStub mavenPomFileFetcherStub = new MavenPomFileFetcherStub();
        MavenProjectDirectoryPathsVerifier verifier =
                factory.createMavenProjectDirectoryPathsVerifier(infomrationSinkMock, commandLineOptionsMock,
                        mavenPomFileFetcherStub);
        // Test
        verifier.analyzePaths();
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(1);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(mavenPomFileFetcherStub.isMethodInvoked()).isFalse();
    }

    @Test
    public void analyzePathsWhereIncludeRepoPathNamesContainsFolderAAndFolderC() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        System.out.println("analyzePathsWhereIncludeRepoPathNamesContainsFolderAAndFolderC.");
        File[] fileAndFolders = {folderA, folderC};
        List<String> includeRepoPathNamesList = createIncludeRepoPathNamesList(fileAndFolders);
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(includeRepoPathNamesList, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileFetcherStub mavenPomFileFetcherStub = new MavenPomFileFetcherStub();
        MavenProjectDirectoryPathsVerifier verifier =
                factory.createMavenProjectDirectoryPathsVerifier(infomrationSinkMock, commandLineOptionsMock,
                        mavenPomFileFetcherStub);
        // Test
        verifier.analyzePaths();
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(mavenPomFileFetcherStub.isMethodInvoked()).isTrue();
        assertThat(mavenPomFileFetcherStub.getIncludeRepoPaths().size()).isEqualTo(2);
    }

    @Test
    public void analyzePathsWhereIncludeRepoPathNamesContainsFolderATwise() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        System.out.println("analyzePathsWhereIncludeRepoPathNamesContainsFolderATwise.");
        File[] fileAndFolders = {folderA, folderA};
        List<String> includeRepoPathNamesList = createIncludeRepoPathNamesList(fileAndFolders);
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(includeRepoPathNamesList, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileFetcherStub mavenPomFileFetcherStub = new MavenPomFileFetcherStub();
        MavenProjectDirectoryPathsVerifier verifier =
                factory.createMavenProjectDirectoryPathsVerifier(infomrationSinkMock, commandLineOptionsMock,
                        mavenPomFileFetcherStub);
        // Test
        verifier.analyzePaths();
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(mavenPomFileFetcherStub.isMethodInvoked()).isTrue();
        assertThat(mavenPomFileFetcherStub.getIncludeRepoPaths().size()).isEqualTo(1);
    }


}

class MavenPomFileFetcherStub implements MavenPomFileFetcher {

    private boolean methodIsInvoked = false;
    private Collection<Path> mavenProjectDirectories = new LinkedList<>();

    @Override
    public void fetchMavenPomFiles(Collection<Path> mavenProjectDirectories) {
        this.mavenProjectDirectories = mavenProjectDirectories;
        methodIsInvoked = true;
    }

    public boolean isMethodInvoked() {
        return this.methodIsInvoked;
    }

    public Collection<Path> getIncludeRepoPaths() {
        return this.mavenProjectDirectories;
    }
}