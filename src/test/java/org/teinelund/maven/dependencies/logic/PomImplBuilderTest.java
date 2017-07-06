package org.teinelund.maven.dependencies.logic;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.PomImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PomImplBuilderTest {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final String PARENT_GROUP_ID = "org.teinelund";
    private final String PARENT_ARTIFACT_ID = "commet-application";
    private final String PARENT_VERSION = "2.34-SNAPSHOT";
    private final String PARENT_RELATIVE_PATH = "../parent/pom.xml";
    private final String PARENT_DEFAULT_RELATIVE_PATH = "../pom.xml";
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
    public void buildWherePomFileContainsDependencies() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        PomBuilder builder = new PomBuilder(pomFileWithDependenciesInputStream(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 2;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 0;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 0;
        // Test
        builder.build();
        // Verify
        PomImpl result = builder.getResult();
        assertFalse( result.getParentPomDependency().isPresent() );
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

    InputStream pomFileWithDependenciesInputStream() throws IOException {
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
    public void buildWherePomFileContainsModules() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        PomBuilder builder = new PomBuilder(pomFileWithModulesInputStream(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 0;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 2;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 0;
        // Test
        builder.build();
        // Verify
        PomImpl result = builder.getResult();
        assertFalse( result.getParentPomDependency().isPresent() );
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

    InputStream pomFileWithModulesInputStream() throws IOException {
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
        pomfile.append("  <modules>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("    <module>backup-protocol-framework</module>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("    <module>backup-server</module>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  </modules>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("</project>"); pomfile.append(LINE_SEPARATOR);
        InputStream in = IOUtils.toInputStream(pomfile.toString(), "UTF-8");
        return in;
    }

    @Test
    public void buildWherePomFileContainsProperties() throws IOException, ParserConfigurationException, SAXException {
        // Initialize
        PomBuilder builder = new PomBuilder(pomFileWithPropertiesInputStream(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 0;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 0;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 3;
        // Test
        builder.build();
        // Verify
        PomImpl result = builder.getResult();
        assertFalse( result.getParentPomDependency().isPresent() );
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

    InputStream pomFileWithPropertiesInputStream() throws IOException {
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
        pomfile.append("</project>"); pomfile.append(LINE_SEPARATOR);
        InputStream in = IOUtils.toInputStream(pomfile.toString(), "UTF-8");
        return in;
    }

    @Test
    public void buildWherePomFileContainsParentPomDependency() throws IOException, SAXException, ParserConfigurationException {
        // Initialize
        PomBuilder builder = new PomBuilder(pomFileWithParentPomDepdencyInputStream(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 0;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 0;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 0;
        // Test
        builder.build();
        // Verify
        PomImpl result = builder.getResult();
        assertTrue( result.getParentPomDependency().isPresent() );
        assertEquals(PARENT_GROUP_ID, result.getParentPomDependency().get().getGroupId());
        assertEquals(PARENT_ARTIFACT_ID, result.getParentPomDependency().get().getArtifactId());
        assertEquals(PARENT_VERSION, result.getParentPomDependency().get().getVersion());
        assertEquals(PARENT_DEFAULT_RELATIVE_PATH, result.getParentPomDependency().get().getRelativePath());
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

    InputStream pomFileWithParentPomDepdencyInputStream() throws IOException {
        StringBuilder pomfile = new StringBuilder();
        pomfile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <modelVersion>4.0.0</modelVersion>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <parent>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <groupId>" + PARENT_GROUP_ID + "</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <artifactId>" + PARENT_ARTIFACT_ID + "</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <version>" + PARENT_VERSION + "</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  </parent>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <groupId>" + GROUP_ID + "</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <artifactId>" + ARTIFACT_ID_FRAMEWORK + "</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <packaging>jar</packaging>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <version>" + VERSION + "</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <name>backup-protocol-framework</name>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <url>http://maven.apache.org</url>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("</project>"); pomfile.append(LINE_SEPARATOR);
        InputStream in = IOUtils.toInputStream(pomfile.toString(), "UTF-8");
        return in;
    }

    @Test
    public void buildWherePomFileContainsParentPomDependencyWithRelativePath() throws IOException, SAXException, ParserConfigurationException {
        // Initialize
        PomBuilder builder = new PomBuilder(pomFileWithParentPomDepdencyWithRelativePathInputStream(), PATH);
        final int EXPECTED_DEPENDENCIES_LIST_LENGTH = 0;
        final int EXPECTED_MODULES_NAMES_LIST_LENGTH = 0;
        final int EXPECTED_PROPERTY_MAP_LENGTH = 0;
        // Test
        builder.build();
        // Verify
        PomImpl result = builder.getResult();
        assertTrue( result.getParentPomDependency().isPresent() );
        assertEquals(PARENT_GROUP_ID, result.getParentPomDependency().get().getGroupId());
        assertEquals(PARENT_ARTIFACT_ID, result.getParentPomDependency().get().getArtifactId());
        assertEquals(PARENT_VERSION, result.getParentPomDependency().get().getVersion());
        assertEquals(PARENT_RELATIVE_PATH, result.getParentPomDependency().get().getRelativePath());
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

    InputStream pomFileWithParentPomDepdencyWithRelativePathInputStream() throws IOException {
        StringBuilder pomfile = new StringBuilder();
        pomfile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <modelVersion>4.0.0</modelVersion>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <parent>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <groupId>" + PARENT_GROUP_ID + "</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <artifactId>" + PARENT_ARTIFACT_ID + "</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <version>" + PARENT_VERSION + "</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("      <relativePath>" + PARENT_RELATIVE_PATH + "</relativePath>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  </parent>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <groupId>" + GROUP_ID + "</groupId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <artifactId>" + ARTIFACT_ID_FRAMEWORK + "</artifactId>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <packaging>jar</packaging>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <version>" + VERSION + "</version>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <name>backup-protocol-framework</name>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("  <url>http://maven.apache.org</url>"); pomfile.append(LINE_SEPARATOR);
        pomfile.append("</project>"); pomfile.append(LINE_SEPARATOR);
        InputStream in = IOUtils.toInputStream(pomfile.toString(), "UTF-8");
        return in;
    }


}
