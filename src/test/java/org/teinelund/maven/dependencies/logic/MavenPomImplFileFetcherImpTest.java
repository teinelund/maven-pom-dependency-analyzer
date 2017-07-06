package org.teinelund.maven.dependencies.logic;

import org.apache.commons.io.FileUtils;
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
import java.util.HashSet;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class MavenPomImplFileFetcherImpTest {

    private static MavenPomEntityFactory factory;
    private File folderA;
    private File folderB;
    private File folderC;
    private File folderD;
    private File folderA_pomXml;
    private File folderC_pomXml;
    private File folderD_pomXml;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void initializeClass() {
        factory = new MavenPomEntityFactoryImpl();
    }

    @Before
    public void initialize() throws IOException {
        folderA = tempFolder.newFolder("folderA");
        folderB = tempFolder.newFolder("folderA", "folderB");
        folderC = tempFolder.newFolder("folderA", "folderB", "folderC");
        folderD = tempFolder.newFolder("folderD");
        folderA_pomXml = FileUtils.getFile(folderA, "pom.xml");
        if (! folderA_pomXml.createNewFile() )
            System.err.println("File \'" + folderA_pomXml.toString() + "\' wasn't created!");
        else
            System.out.println("File \'" + folderA_pomXml.toString() + "\' was crerated.");

        folderC_pomXml = FileUtils.getFile(folderC, "pom.xml");
        if (! folderC_pomXml.createNewFile() )
            System.err.println("File \'" + folderC_pomXml.toString() + "\' wasn't created!");
        else
            System.out.println("File \'" + folderC_pomXml.toString() + "\' was crerated.");

        folderD_pomXml = FileUtils.getFile(folderD, "pom.xml");
        if (! folderD_pomXml.createNewFile() )
            System.err.println("File \'" + folderD_pomXml.toString() + "\' wasn't created!");
        else
            System.out.println("File \'" + folderD_pomXml.toString() + "\' was crerated.");

        File somwFile = FileUtils.getFile(folderA, "README.txt");
        if (! somwFile.createNewFile() )
            System.err.println("File \'" + somwFile.toString() + "\' wasn't created!");
        else
            System.out.println("File \'" + somwFile.toString() + "\' was crerated.");

        somwFile = FileUtils.getFile(folderC, "system.properties");
        if (! somwFile.createNewFile() )
            System.err.println("File \'" + somwFile.toString() + "\' wasn't created!");
        else
            System.out.println("File \'" + somwFile.toString() + "\' was crerated.");
    }

    @Test
    public void fetchMavenPomFilesWhereTwoFoldersAreGivenExpectedThreePomXmlFile() throws ParserConfigurationException, SAXException, IOException {
        // Initialize
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(null, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        PathExcludeFilterMock pathExcludeFilter = new PathExcludeFilterMock();
        MavenPomFileFetcher mavenPomFileFetcher = factory.createMavenPomFileFetcher(infomrationSinkMock,
                commandLineOptionsMock, pathExcludeFilter);
        Collection<Path> mavenProjectDirectories = new HashSet<>();
        mavenProjectDirectories.add(this.folderA.toPath());
        mavenProjectDirectories.add(this.folderD.toPath());
        // Test
        mavenPomFileFetcher.fetchMavenPomFiles(mavenProjectDirectories);
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(pathExcludeFilter.isMethodInvoked()).isTrue();
        assertThat(pathExcludeFilter.getPomXmlFiles().size()).isEqualTo(3);
    }
}

class PathExcludeFilterMock implements PathExcludeFilter {

    public Collection<Path> getPomXmlFiles() {
        return pomXmlFiles;
    }

    public boolean isMethodInvoked() {
        return methodIsInvoked;
    }

    private Collection<Path> pomXmlFiles = new LinkedList<>();
    private boolean methodIsInvoked = false;

    @Override
    public void excludePaths(Collection<Path> pomXmlFiles) {
        methodIsInvoked = true;
        this.pomXmlFiles = pomXmlFiles;
    }
}