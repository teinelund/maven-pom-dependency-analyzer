package org.teinelund.maven.dependencies.logic;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.ParentPomDependency;
import org.teinelund.maven.dependencies.domain.PomImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MavenPomFileHierarchyOrganizerImplTest {

    private static MavenPomEntityFactory factory;

    @BeforeClass
    public static void initializeClass() {
        factory = new MavenPomEntityFactoryImpl();
    }


    @Test
    public void organizeMavenPomFiles() {
        // Initialize
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(null, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        ReplacePropertyPlaceholderMock replacePropertyPlaceholderMock = new ReplacePropertyPlaceholderMock();
        MavenPomFileHierarchyOrganizer mavenPomFileHierarchyOrganizer = factory.createMavenPomFileHierarchyOrganizer(infomrationSinkMock,
                commandLineOptionsMock, replacePropertyPlaceholderMock);
        List<PomImpl> poms = createPoms();

        // Test
        mavenPomFileHierarchyOrganizer.organizeMavenPomFiles(poms);
        // Verify
        assertThat(poms.get(0).getParentPom().get()).isSameAs(poms.get(1));  //AssertJ
        assertThat(poms.get(0).getPomsThatDependsOnThisPomImpl().isEmpty()).isTrue();
        assertThat(poms.get(1).getParentPom().isPresent()).isFalse();
        assertThat(poms.get(1).getPomsThatDependsOnThisPomImpl().size()).isEqualTo(2);
        assertThat(poms.get(2).getParentPom().get()).isSameAs(poms.get(1));
        assertThat(poms.get(2).getPomsThatDependsOnThisPomImpl().isEmpty()).isTrue();

        assertThat(poms.get(3).getParentPom().get()).isSameAs(poms.get(5));
        assertThat(poms.get(3).getPomsThatDependsOnThisPomImpl().size()).isEqualTo(1);
        assertThat(poms.get(4).getParentPom().get()).isSameAs(poms.get(7));
        assertThat(poms.get(4).getPomsThatDependsOnThisPomImpl().isEmpty()).isTrue();
        assertThat(poms.get(5).getParentPom().isPresent()).isFalse();
        assertThat(poms.get(5).getPomsThatDependsOnThisPomImpl().size()).isEqualTo(2);
        assertThat(poms.get(6).getParentPom().get()).isSameAs(poms.get(3));
        assertThat(poms.get(6).getPomsThatDependsOnThisPomImpl().isEmpty()).isTrue();
        assertThat(poms.get(7).getParentPom().get()).isSameAs(poms.get(5));
        assertThat(poms.get(7).getPomsThatDependsOnThisPomImpl().size()).isEqualTo(2);
        assertThat(poms.get(8).getParentPom().get()).isSameAs(poms.get(7));
        assertThat(poms.get(8).getPomsThatDependsOnThisPomImpl().isEmpty()).isTrue();

        assertThat(replacePropertyPlaceholderMock.isMethodInvoked()).isTrue();
        assertThat(replacePropertyPlaceholderMock.getPomImplXmlFiles().size()).isEqualTo(2);
    }

    PomImpl createPom(Path pomPath) {
        Dependency dependency = new Dependency("GROUP_ID", "ARTIFACT_ID", "VERSION");
        PomImpl pom = new PomImpl(Optional.empty(), dependency, pomPath);
        return pom;
    }

    PomImpl createPom(Path pomPath, String relativePath) {
        Dependency dependency = new Dependency("GROUP_ID", "ARTIFACT_ID", "VERSION");
        PomImpl pom = new PomImpl(Optional.of(new ParentPomDependency("GROUP_ID",
                "ARTIFACT_ID", "VERSION", relativePath)), dependency, pomPath);
        return pom;
    }

    //
    // orderengine/pom.xml
    // + orderengine/domain/pom.xml
    // + orderengine/engine/pom.xml
    //
    // core/parent/pom.xml
    // + core/pom.xml
    //   + core/domain/pom.xml
    // + core/framework/pom.xml
    //   + core/framework/adapter/pom.xml
    //   + core/framework/dao/pom.xml
    //
    List<PomImpl> createPoms() {
        List<PomImpl> poms = new LinkedList<>();
        poms.add(createPom(Paths.get("Users", "cody", "repos", "orderengine", "domian", "pom.xml"), "../pom.xml"));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "orderengine", "pom.xml")));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "orderengine", "engine", "pom.xml"), "../pom.xml"));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "core", "pom.xml"), "parent/pom.xml"));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "core", "framework", "dao", "pom.xml"), "../pom.xml"));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "core", "parent", "pom.xml")));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "core", "domain", "pom.xml"), "../pom.xml"));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "core", "framework", "pom.xml"), "../parent/pom.xml"));
        poms.add(createPom(Paths.get("Users", "cody", "repos", "core", "framework", "adapter", "pom.xml"), "../pom.xml"));
        return poms;
    }

    @Test
    public void calculateParentPomWhereRelativePathIsTheDefaultRelativePath() {
        // Initialize
        MavenPomFileHierarchyOrganizerImpl mavenPomFileHierarchyOrganizer = (MavenPomFileHierarchyOrganizerImpl) factory.createMavenPomFileHierarchyOrganizer(null,
                null, null);
        Path inputPath = Paths.get("Users", "cody", "repos", "comet", "framework", "core", "pom.xml");
        String relativePath = "../pom.xml";
        Path expectedPath = Paths.get("Users", "cody", "repos", "comet", "framework", "pom.xml");
        // Test
        Path result = mavenPomFileHierarchyOrganizer.calculateParentPom(inputPath, relativePath);
        // Verify
        assertThat(result).isEqualTo(expectedPath);  //AssertJ
    }

    @Test
    public void calculateParentPomWhereRelativePathIsSingleParentPathWithoutPomXml() {
        // Initialize
        MavenPomFileHierarchyOrganizerImpl mavenPomFileHierarchyOrganizer = (MavenPomFileHierarchyOrganizerImpl) factory.createMavenPomFileHierarchyOrganizer(null,
                null, null);
        Path inputPath = Paths.get("Users", "cody", "repos", "comet", "framework", "core", "pom.xml");
        String relativePath = "..";
        Path expectedPath = Paths.get("Users", "cody", "repos", "comet", "framework", "pom.xml");
        // Test
        Path result = mavenPomFileHierarchyOrganizer.calculateParentPom(inputPath, relativePath);
        // Verify
        assertThat(result).isEqualTo(expectedPath);  //AssertJ
    }

    @Test
    public void calculateParentPomWhereRelativePathIsTwoParentPathsUpWithoutPomXml() {
        // Initialize
        MavenPomFileHierarchyOrganizerImpl mavenPomFileHierarchyOrganizer = (MavenPomFileHierarchyOrganizerImpl) factory.createMavenPomFileHierarchyOrganizer(null,
                null, null);
        Path inputPath = Paths.get("Users", "cody", "repos", "comet", "framework", "core", "pom.xml");
        String relativePath = "../../";
        Path expectedPath = Paths.get("Users", "cody", "repos", "comet", "pom.xml");
        // Test
        Path result = mavenPomFileHierarchyOrganizer.calculateParentPom(inputPath, relativePath);
        // Verify
        assertThat(result).isEqualTo(expectedPath);  //AssertJ
    }

    @Test
    public void calculateParentPomWhereRelativePathIsTwoParentPathsUpAndNewDirectoryPathDownWithoutPomXml() {
        // Initialize
        MavenPomFileHierarchyOrganizerImpl mavenPomFileHierarchyOrganizer = (MavenPomFileHierarchyOrganizerImpl) factory.createMavenPomFileHierarchyOrganizer(null,
                null, null);
        Path inputPath = Paths.get("Users", "cody", "repos", "comet", "framework", "core", "pom.xml");
        String relativePath = "../../parent/core";
        Path expectedPath = Paths.get("Users", "cody", "repos", "comet", "parent", "core", "pom.xml");
        // Test
        Path result = mavenPomFileHierarchyOrganizer.calculateParentPom(inputPath, relativePath);
        // Verify
        assertThat(result).isEqualTo(expectedPath);  //AssertJ
    }

    @Test
    public void calculateParentPomWhereRelativePathIsTwoParentPathsUpAndNewDirectoryPathDown() {
        // Initialize
        MavenPomFileHierarchyOrganizerImpl mavenPomFileHierarchyOrganizer = (MavenPomFileHierarchyOrganizerImpl) factory.createMavenPomFileHierarchyOrganizer(null,
                null, null);
        Path inputPath = Paths.get("Users", "cody", "repos", "comet", "framework", "core", "pom.xml");
        String relativePath = "../../parent/core/pom.xml";
        Path expectedPath = Paths.get("Users", "cody", "repos", "comet", "parent", "core", "pom.xml");
        // Test
        Path result = mavenPomFileHierarchyOrganizer.calculateParentPom(inputPath, relativePath);
        // Verify
        assertThat(result).isEqualTo(expectedPath);  //AssertJ
    }

}

class ReplacePropertyPlaceholderMock implements ReplacePropertyPlaceholder {

    private boolean methodIsInvoked = false;
    private Collection<PomImpl> pomImplXmlFiles = new LinkedList<>();

    @Override
    public void replacePlaceholders(Collection<PomImpl> pomImplXmlFiles) {
        this.pomImplXmlFiles = pomImplXmlFiles;
        methodIsInvoked = true;
    }

    public boolean isMethodInvoked() {
        return this.methodIsInvoked;
    }

    public Collection<PomImpl> getPomImplXmlFiles() {
        return this.pomImplXmlFiles;
    }
}