package org.teinelund.maven.dependencies;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PomBuilderTest {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final String GROUP_ID = "org.teinelund.comet";
    private final String ARTIFACT_ID_FRAMEWORK = "framework";
    private final String VERSION = "1.0-SNAPSHOT";
    private final Path PATH = Paths.get(System.getProperty("user.dir"));
    private final String ARTIFACT_ID_CLIENT = "client";
    private final String ARTIFACT_ID_SERVER = "server";
    private final String GROUP_ID_JUNIT = "junit";
    private final String ARTIFACT_ID_JUNIT = "junit";
    private final String VERSION_JUNIT = "4.5";
    private final String GROUP_ID_SPRING = "org.spring";
    private final String ARTIFACT_ID_SPRING = "spring-core";
    private final String VERSION_SPRING = "4.3.1";
    private final String PROPERTY_NAME_1 = "hgRepositoryName";
    private final String PROPERTY_VALUE_1 = "selfservice-invoice";
    private final String PROPERTY_NAME_2 = "selfservice.core.version";
    private final String PROPERTY_VALUE_2 = "4.0.0";
    private final String PROPERTY_NAME_3 = "selfservice.subscription.version";
    private final String PROPERTY_VALUE_3 = "4.0.0";


    @Test
    public void testMethodReadPomFileWherePomFileContainsDependenciesButNoModules() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        PomBuilder builder = new PomBuilder(readPomFileWithDependenciesButNoModules(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 2;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 0;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 0;
        // Test
        builder.build();
        Pom result = builder.getResult();
        // Verify
        assertNotNull( result.getDependency() );
        assertEquals(GROUP_ID, result.getDependency().getGroupId());
        assertEquals(ARTIFACT_ID_FRAMEWORK, result.getDependency().getArtifactId());
        assertEquals(VERSION, result.getDependency().getVersion());
        assertNotNull( result.getDependencies() );
        assertEquals(EXPECTED_DEPENDENCIES_LIST_LENGTH, result.getDependencies().size());
        assertNotNull( result.getModuleNames() );
        assertEquals(EXPECTED_MODULES_NAMES_LIST_LENGTH, result.getModuleNames().size());
        assertNotNull( result.getPathToPomFile() );
        assertTrue(result.getPathToPomFile().equals(PATH));
        assertNotNull( result.getProperties() );
        assertEquals(EXPECTED_PROPERTY_MAP_LENGTH, result.getProperties().size());
    }

    InputStream readPomFileWithDependenciesButNoModules() throws IOException {
        StringBuilder pomfile = new StringBuilder();
        pomfile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <modelVersion>4.0.0</modelVersion>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <groupId>" + GROUP_ID + "</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <artifactId>" + ARTIFACT_ID_FRAMEWORK + "</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <packaging>jar</packaging>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <version>" + VERSION + "</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <name>backup-protocol-framework</name>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <url>http://maven.apache.org</url>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <dependencies>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     <dependency>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <groupId>junit</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <artifactId>junit</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <version>4.11</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <scope>test</scope>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     </dependency>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     <dependency>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <groupId>org.mockito</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <artifactId>mockito-all</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <version>1.9.5</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("        <scope>test</scope>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     </dependency>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  </dependencies>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("</project>"); pomfile.append(LINE_SEPARATOR);
        InputStream in = IOUtils.toInputStream(pomfile.toString(), "UTF-8");
        return in;
    }

    @Test
    public void testMethodReadPomFileWherePomFileContainsModulesButNoDependencies() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        PomBuilder builder = new PomBuilder(readPomFileWithModulesButNoDependencies(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 0;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 2;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 3;
        // Test
        builder.build();
        Pom result = builder.getResult();
        // Verify
        assertNotNull( result.getDependency() );
        assertEquals(GROUP_ID, result.getDependency().getGroupId());
        assertEquals(ARTIFACT_ID_FRAMEWORK, result.getDependency().getArtifactId());
        assertEquals(VERSION, result.getDependency().getVersion());
        assertNotNull( result.getDependencies() );
        assertEquals(EXPECTED_DEPENDENCIES_LIST_LENGTH, result.getDependencies().size());
        assertNotNull( result.getModuleNames() );
        assertEquals(EXPECTED_MODULES_NAMES_LIST_LENGTH, result.getModuleNames().size());
        assertNotNull( result.getPathToPomFile() );
        assertTrue(result.getPathToPomFile().equals(PATH));
        assertNotNull( result.getProperties() );
        assertEquals(EXPECTED_PROPERTY_MAP_LENGTH, result.getProperties().size());
        assertTrue(result.getProperties().keySet().contains(PROPERTY_NAME_1));
        assertTrue(result.getProperties().keySet().contains(PROPERTY_NAME_2));
        assertTrue(result.getProperties().keySet().contains(PROPERTY_NAME_3));
        assertTrue(result.getProperties().get(PROPERTY_NAME_1).equals(PROPERTY_VALUE_1));
        assertTrue(result.getProperties().get(PROPERTY_NAME_2).equals(PROPERTY_VALUE_2));
        assertTrue(result.getProperties().get(PROPERTY_NAME_3).equals(PROPERTY_VALUE_3));
    }

    InputStream readPomFileWithModulesButNoDependencies() throws IOException {
        StringBuilder pomfile = new StringBuilder();
        pomfile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <modelVersion>4.0.0</modelVersion>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <groupId>" + GROUP_ID + "</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <artifactId>" + ARTIFACT_ID_FRAMEWORK + "</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <packaging>jar</packaging>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <version>" + VERSION + "</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <name>backup-protocol-framework</name>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <url>http://maven.apache.org</url>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <properties>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     <" + PROPERTY_NAME_1 + ">" + PROPERTY_VALUE_1 + "</" + PROPERTY_NAME_1 + ">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     <" + PROPERTY_NAME_2 + ">" + PROPERTY_VALUE_2 + "</" + PROPERTY_NAME_2 + ">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("     <" + PROPERTY_NAME_3 + ">" + PROPERTY_VALUE_3 + "</" + PROPERTY_NAME_3 + ">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  </properties>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <modules>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("    <module>backup-protocol-framework</module>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("    <module>backup-server</module>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  </modules>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("</project>"); pomfile.append(LINE_SEPARATOR);
        InputStream in = IOUtils.toInputStream(pomfile.toString(), "UTF-8");
        return in;
    }

    //@Test
    public void testMethodConnectPomsWithThreePomsWhereTwoAreDependentAndSameVersion() {
        // Initialize
        Application application = new Application(null);
        StringBuilder warnings = new StringBuilder();
        List<Pom> pomList = createPomListWithThreePomsWhereTwoAreDependentAndSameVersion();
        // Test
    //    application.connectPomsViaDependencies(pomList, warnings);
        // Post Test Initializations
        Pom pomFramework = pomList.get(0);
        Pom pomDependsOnFramework = pomList.get(1);
        Pom pomNotDependentOnAnyModule = pomList.get(2);
        // Verify
        assertEquals( 0, warnings.toString().length() );

        assertEquals( 0, pomFramework.getPomDependencies().size() );
        assertEquals( 1, pomFramework.getPomsThatDependsOnThisPom().size() );
        assertEquals( pomDependsOnFramework, pomFramework.getPomsThatDependsOnThisPom().get(0) );

        assertEquals( 1, pomDependsOnFramework.getPomDependencies().size() );
        assertEquals( pomFramework, pomDependsOnFramework.getPomDependencies().get(0) );
        assertEquals( 0, pomDependsOnFramework.getPomsThatDependsOnThisPom().size() );

        assertEquals( 0, pomNotDependentOnAnyModule.getPomDependencies().size() );
        assertEquals( 0, pomNotDependentOnAnyModule.getPomsThatDependsOnThisPom().size() );

    }

    List<Pom> createPomListWithThreePomsWhereTwoAreDependentAndSameVersion() {
        List<Pom> pomList = new LinkedList<>();
        Pom pomFramework = new Pom(new Dependency(GROUP_ID, ARTIFACT_ID_FRAMEWORK, VERSION), Paths.get(System.getProperty("user.dir"), ARTIFACT_ID_FRAMEWORK));
        pomFramework.addDependency(new Dependency(GROUP_ID_JUNIT, ARTIFACT_ID_JUNIT, VERSION_JUNIT));
        pomList.add(pomFramework);
        Pom pomDependsOnFramework = new Pom(new Dependency(GROUP_ID, ARTIFACT_ID_CLIENT, VERSION),
                Paths.get(System.getProperty("user.dir"), ARTIFACT_ID_FRAMEWORK));
        pomDependsOnFramework.addDependency(new Dependency(GROUP_ID_JUNIT, ARTIFACT_ID_JUNIT, VERSION_JUNIT));
        pomDependsOnFramework.addDependency(new Dependency(GROUP_ID, ARTIFACT_ID_FRAMEWORK, VERSION));
        pomList.add(pomDependsOnFramework);
        Pom pomNotDependentOnAnyModule = new Pom(new Dependency(GROUP_ID, ARTIFACT_ID_SERVER, VERSION),
            Paths.get(System.getProperty("user.dir"), ARTIFACT_ID_FRAMEWORK));
        pomNotDependentOnAnyModule.addDependency(new Dependency(GROUP_ID_SPRING, ARTIFACT_ID_SPRING, VERSION_SPRING));
        pomNotDependentOnAnyModule.addDependency(new Dependency(GROUP_ID_JUNIT, ARTIFACT_ID_JUNIT, VERSION_JUNIT));
        pomList.add(pomNotDependentOnAnyModule);
        return pomList;
    }


}
