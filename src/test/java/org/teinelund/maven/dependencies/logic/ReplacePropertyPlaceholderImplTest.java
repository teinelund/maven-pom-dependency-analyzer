package org.teinelund.maven.dependencies.logic;

import org.junit.BeforeClass;
import org.junit.Test;
import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.ParentPomDependency;
import org.teinelund.maven.dependencies.domain.PomImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ReplacePropertyPlaceholderImplTest {

    private static MavenPomEntityFactory factory;
    private final String NO_GROUP_ID = "?";
    private final String GROUP_ID = "GROUP_ID";
    private final String PARENT_ARTIFACT_ID = "PARENT_ARTIFACT_ID";
    private final String CHILD_GROUP_ID = "CHILD_GROUP_ID";
    private final String CHILD_ARTIFACT_ID = "CHILD_ARTIFACT_ID";
    private final String GRAND_CHILD_ARTIFACT_ID = "GRAND_CHILD_ARTIFACT_ID";
    private final String NO_VERSION = "?";
    private final String VERSION = "VERSION";
    private final String CHILD_VERSION = "CHILD_VERSION";

    private final String DEPENDENCY_1_GROUP_ID = "DEPENDENCY_1_GROUP_ID";
    private final String DEPENDENCY_2_GROUP_ID = "DEPENDENCY_2_GROUP_ID";
    private final String DEPENDENCY_3_GROUP_ID = "DEPENDENCY_3_GROUP_ID";

    private final String DEPENDENCY_1_ARTIFACT_ID = "DEPENDENCY_1_ARTIFACT_ID";
    private final String DEPENDENCY_2_ARTIFACT_ID = "DEPENDENCY_2_ARTIFACT_ID";
    private final String DEPENDENCY_3_ARTIFACT_ID = "DEPENDENCY_3_ARTIFACT_ID";

    private final String DEPENDENCY_1_VERSION_ID = "DEPENDENCY_1_VERSION_ID";
    private final String DEPENDENCY_2_VERSION_ID = "DEPENDENCY_2_VERSION_ID";
    private final String DEPENDENCY_3_VERSION_ID = "DEPENDENCY_3_VERSION_ID";

    private final String PROPERTY_KEY_1 = "PROPERTY_KEY_1";
    private final String PROPERTY_KEY_2 = "PROPERTY_KEY_2";
    private final String PROPERTY_KEY_3 = "PROPERTY_KEY_3";


    @BeforeClass
    public static void initializeClass() {
        factory = new MavenPomEntityFactoryImpl();
    }

    @Test
    public void replacePlaceholdersWhereInputContainsPomsWithGroupIdAndVersionPlaceholders() {
        // Initialize
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(null, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileConnector mavenPomFileConnector = new MavenPomFileConnectorMock();
        ReplacePropertyPlaceholder replacePropertyPlaceholder = factory.createReplacePropertyPlaceholder(infomrationSinkMock, commandLineOptionsMock, mavenPomFileConnector);
        Collection<PomImpl> poms = createParentAndChildPomWithGroupIdAndVersionPlaceholders();
        // Test
        replacePropertyPlaceholder.replacePlaceholders(poms);
        // Verify
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependency().getGroupId() ).isEqualTo( GROUP_ID );  //AssertJ
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependency().getArtifactId() ).isEqualTo( CHILD_ARTIFACT_ID );
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependency().getVersion() ).isEqualTo( VERSION );
    }

    private Collection<PomImpl> createParentAndChildPomWithGroupIdAndVersionPlaceholders() {
        Collection<PomImpl> poms = new LinkedList<>();
        // Parent
        Optional<ParentPomDependency> parentPomDependencyOption = Optional.empty();
        Dependency parentDependency = new Dependency(GROUP_ID, PARENT_ARTIFACT_ID, VERSION);
        Path pathToParentPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "pom.xml");
        PomImpl parentPom = new PomImpl(parentPomDependencyOption, parentDependency, pathToParentPomFile);
        // Child, with no group id or version
        Optional<ParentPomDependency> childPomDependencyOption = Optional.of(new ParentPomDependency(GROUP_ID, PARENT_ARTIFACT_ID, VERSION, "../pom.xml"));
        Dependency childDependency = new Dependency(NO_GROUP_ID, CHILD_ARTIFACT_ID, NO_VERSION);
        Path pathToChildPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "Domain", "pom.xml");
        PomImpl childPom = new PomImpl(childPomDependencyOption, childDependency, pathToChildPomFile);

        childPom.setParentPom(parentPom);
        parentPom.addPomThatDependsOnThisPom(childPom);
        poms.add(parentPom);
        return poms;
    }

    @Test
    public void replacePlaceholdersWhereInputContainsPomsWithPropertyVariables() {
        // Initialize
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(null, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileConnector mavenPomFileConnector = new MavenPomFileConnectorMock();
        ReplacePropertyPlaceholder replacePropertyPlaceholder = factory.createReplacePropertyPlaceholder(infomrationSinkMock, commandLineOptionsMock, mavenPomFileConnector);
        Collection<PomImpl> poms = createPomsWithPropertyVariables();
        // Test
        replacePropertyPlaceholder.replacePlaceholders(poms);
        // Verify
        assertThat( ((List<PomImpl>) poms).get(0).getDependencies().get(0).getVersion() ).isEqualTo( DEPENDENCY_1_VERSION_ID );  //AssertJ
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(0).getVersion() ).isEqualTo( DEPENDENCY_1_VERSION_ID );
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(1).getVersion() ).isEqualTo( DEPENDENCY_2_VERSION_ID );
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(0).getVersion() ).isEqualTo( DEPENDENCY_1_VERSION_ID );
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(1).getVersion() ).isEqualTo( DEPENDENCY_2_VERSION_ID );
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(2).getVersion() ).isEqualTo( DEPENDENCY_3_VERSION_ID );
    }

    private Collection<PomImpl> createPomsWithPropertyVariables() {
        Collection<PomImpl> poms = new LinkedList<>();
        // Parent
        Optional<ParentPomDependency> parentPomDependencyOption = Optional.empty();
        Dependency parentDependency = new Dependency(GROUP_ID, PARENT_ARTIFACT_ID, VERSION);
        Path pathToParentPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "pom.xml");
        PomImpl parentPom = new PomImpl(parentPomDependencyOption, parentDependency, pathToParentPomFile);
        Map<String, String> parentProperties = new HashMap<>();
        parentProperties.put(PROPERTY_KEY_1, DEPENDENCY_1_VERSION_ID);
        parentPom.addProperties(parentProperties);
        Dependency parent_dependency_1 = new Dependency(DEPENDENCY_1_GROUP_ID, DEPENDENCY_1_ARTIFACT_ID, "${" + PROPERTY_KEY_1 + "}");
        parentPom.addDependency(parent_dependency_1);
        // Child
        Optional<ParentPomDependency> childPomDependencyOption = Optional.of(new ParentPomDependency(GROUP_ID, PARENT_ARTIFACT_ID, VERSION, "../pom.xml"));
        Dependency childDependency = new Dependency(NO_GROUP_ID, CHILD_ARTIFACT_ID, NO_VERSION);
        Path pathToChildPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "Domain", "pom.xml");
        PomImpl childPom = new PomImpl(childPomDependencyOption, childDependency, pathToChildPomFile);
        Map<String, String> childProperties = new HashMap<>();
        childProperties.put(PROPERTY_KEY_2, DEPENDENCY_2_VERSION_ID);
        childPom.addProperties(childProperties);
        Dependency child_dependency_1 = new Dependency(DEPENDENCY_1_GROUP_ID, DEPENDENCY_1_ARTIFACT_ID, "${" + PROPERTY_KEY_1 + "}");
        childPom.addDependency(child_dependency_1);
        Dependency child_dependency_2 = new Dependency(DEPENDENCY_2_GROUP_ID, DEPENDENCY_2_ARTIFACT_ID, "${" + PROPERTY_KEY_2 + "}");
        childPom.addDependency(child_dependency_2);
        // Grand Child
        Optional<ParentPomDependency> grandChildPomDependencyOption = Optional.of(new ParentPomDependency(GROUP_ID, CHILD_ARTIFACT_ID, VERSION, "../pom.xml"));
        Dependency grandChildDependency = new Dependency(NO_GROUP_ID, GRAND_CHILD_ARTIFACT_ID, NO_VERSION);
        Path pathToGrandChildPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "Domain", "Integration", "pom.xml");
        PomImpl grandChildPom = new PomImpl(grandChildPomDependencyOption, grandChildDependency, pathToGrandChildPomFile);
        Map<String, String> grandChildProperties = new HashMap<>();
        grandChildProperties.put(PROPERTY_KEY_3, DEPENDENCY_3_VERSION_ID);
        grandChildPom.addProperties(grandChildProperties);
        Dependency grandChild_dependency_1 = new Dependency(DEPENDENCY_1_GROUP_ID, DEPENDENCY_1_ARTIFACT_ID, "${" + PROPERTY_KEY_1 + "}");
        grandChildPom.addDependency(grandChild_dependency_1);
        Dependency grandChild_dependency_2 = new Dependency(DEPENDENCY_2_GROUP_ID, DEPENDENCY_2_ARTIFACT_ID, "${" + PROPERTY_KEY_2 + "}");
        grandChildPom.addDependency(grandChild_dependency_2);
        Dependency grandChild_dependency_3 = new Dependency(DEPENDENCY_3_GROUP_ID, DEPENDENCY_3_ARTIFACT_ID, "${" + PROPERTY_KEY_3 + "}");
        grandChildPom.addDependency(grandChild_dependency_3);

        grandChildPom.setParentPom(childPom);
        childPom.addPomThatDependsOnThisPom(grandChildPom);
        childPom.setParentPom(parentPom);
        parentPom.addPomThatDependsOnThisPom(childPom);
        poms.add(parentPom);
        return poms;
    }

    @Test
    public void replacePlaceholdersWhereInputContainsPomsWithProjectGroupIdAndProjectVersionPlaceholders() {
        // Initialize
        CommandLineOptionsMock commandLineOptionsMock = new CommandLineOptionsMock(null, null, OPTION.QUIET);
        InfomrationSinkMock infomrationSinkMock = new InfomrationSinkMock();
        MavenPomFileConnector mavenPomFileConnector = new MavenPomFileConnectorMock();
        ReplacePropertyPlaceholder replacePropertyPlaceholder = factory.createReplacePropertyPlaceholder(infomrationSinkMock, commandLineOptionsMock, mavenPomFileConnector);
        Collection<PomImpl> poms = createPomsWithProjectGroupIdAndProjectVersionPlaceholders();
        // Test
        replacePropertyPlaceholder.replacePlaceholders(poms);
        // Verify
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(0).getGroupId() ).isEqualTo( CHILD_GROUP_ID );
        assertThat( ((List<PomImpl>) poms).get(0).getPomsThatDependsOnThisPomImpl().get(0).getDependencies().get(0).getVersion() ).isEqualTo( CHILD_VERSION );
    }

    private Collection<PomImpl> createPomsWithProjectGroupIdAndProjectVersionPlaceholders() {
        Collection<PomImpl> poms = new LinkedList<>();
        // Parent
        Optional<ParentPomDependency> parentPomDependencyOption = Optional.empty();
        Dependency parentDependency = new Dependency(GROUP_ID, PARENT_ARTIFACT_ID, VERSION);
        Path pathToParentPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "pom.xml");
        PomImpl parentPom = new PomImpl(parentPomDependencyOption, parentDependency, pathToParentPomFile);
        // Child, with no group id or version
        Optional<ParentPomDependency> childPomDependencyOption = Optional.of(new ParentPomDependency(GROUP_ID, PARENT_ARTIFACT_ID, VERSION, "../pom.xml"));
        Dependency childDependency = new Dependency(CHILD_GROUP_ID, CHILD_ARTIFACT_ID, CHILD_VERSION);
        Path pathToChildPomFile = Paths.get("Users", "cody", "repos", "OrderEngine", "Domain", "pom.xml");
        PomImpl childPom = new PomImpl(childPomDependencyOption, childDependency, pathToChildPomFile);
        Dependency child_dependency_1 = new Dependency(Application.getGroupIdPlaceholder(), DEPENDENCY_1_ARTIFACT_ID, Application.getVersionPlaceholder());
        childPom.addDependency(child_dependency_1);

        childPom.setParentPom(parentPom);
        parentPom.addPomThatDependsOnThisPom(childPom);
        poms.add(parentPom);
        return poms;
    }
}

class MavenPomFileConnectorMock implements MavenPomFileConnector {

    @Override
    public void connectMavenPomFiles(Collection<PomImpl> pomImplXmlFiles) {

    }
}