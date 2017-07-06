package org.teinelund.maven.dependencies.logic;

import org.junit.BeforeClass;
import org.junit.Test;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PathExcludeFilterImplTest {

    private static MavenPomEntityFactory factory;

    @BeforeClass
    public static void initializeClass() {
        factory = new MavenPomEntityFactoryImpl();
    }

    @Test
    public void excludePaths() throws IOException, SAXException, ParserConfigurationException {
        // Initialize
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(null,
                createExcludeRepoPathNames(), OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileReaderMock mavenPomFileReaderMock = new MavenPomFileReaderMock();
        PathExcludeFilter pathExcludeFilter = factory.createPathExcludeFilter(infomrationSinkMock, commandLineOptionsMock,
                mavenPomFileReaderMock);
        // Test
        pathExcludeFilter.excludePaths(createPomXmlFileNames());
        // Verify
        assertThat(infomrationSinkMock.getErrorMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getWarningMessages().size()).isEqualTo(0);
        assertThat(infomrationSinkMock.getInformationMessages().size()).isEqualTo(0);
        assertThat(mavenPomFileReaderMock.isMethodInvoked()).isTrue();
        assertThat(mavenPomFileReaderMock.getPomXmlFiles().size()).isEqualTo(3);
    }

    private Collection<Path> createPomXmlFileNames() {
        Collection<Path> pomXmlFiles = new LinkedList<>();
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "OrderEngine", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "OrderEngine", "OrderUI", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "OrderEngine", "OrderService", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "OrderEngine", "OrderDao", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "Core", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "Core", "Dao", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "Core", "UI", "pom.xml" ));
        pomXmlFiles.add(Paths.get("Users", "Cody", "repos", "Core", "Utility", "pom.xml" ));
        return pomXmlFiles;
    }

    private List<String> createExcludeRepoPathNames() {
        List<String> excludeRepoPathNames = new LinkedList<>();
        excludeRepoPathNames.add("repos/OrderEngine");
        excludeRepoPathNames.add("Users/Cody/repos/Core/Dao/pom.xml");
        excludeRepoPathNames.add("PATH/DOES/NOT/EXIST");    // THis should be ignored!
        return excludeRepoPathNames;
    }

}

class MavenPomFileReaderMock implements MavenPomFileReader {

    public Collection<Path> getPomXmlFiles() {
        return pomXmlFiles;
    }

    public boolean isMethodInvoked() {
        return methodIsInvoked;
    }

    private Collection<Path> pomXmlFiles = new LinkedList<>();
    private boolean methodIsInvoked = false;

    @Override
    public void readPomFiles(Collection<Path> pomXmlFiles) {
        methodIsInvoked = true;
        this.pomXmlFiles = pomXmlFiles;
    }
}