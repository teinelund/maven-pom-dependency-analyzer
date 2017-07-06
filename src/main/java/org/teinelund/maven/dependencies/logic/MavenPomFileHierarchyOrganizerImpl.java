package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.teinelund.maven.dependencies.domain.PomImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static java.util.Arrays.copyOfRange;

public class MavenPomFileHierarchyOrganizerImpl implements MavenPomFileHierarchyOrganizer {

    private InformationSink informationSink;
    private CommandLineOptions options;
    private ReplacePropertyPlaceholder replacePropertyPlaceholder;

    public MavenPomFileHierarchyOrganizerImpl(InformationSink informationSink, CommandLineOptions options, ReplacePropertyPlaceholder replacePropertyPlaceholder) {
        this.informationSink = informationSink;
        this.options = options;
        this.replacePropertyPlaceholder = replacePropertyPlaceholder;
    }

    //
    // A/B/C/pom.xml
    // A/B/C/D/pom.xml
    // A/B/C/E/pom.xml
    // A/B/C/E/F/pom.xml
    // A/B/C/E/G/pom.xml
    // A/B/C/H/pom.xml
    // A/B/J/pom.xml
    // A/B/J/K/pom.xml
    // A/B/J/L/pom.xml
    //
    // <parent>
    //     <groupId>com.teliasonera.agora.parent</groupId>
    //     <artifactId>agora-parent</artifactId>
    //     <version>5.6.4</version>
    //     <relativePath />
    // </parent>
    // <packaging>pom</packaging>
    // <groupId>com.teliasonera.agora.selfservice.subscription</groupId>
    // <artifactId>selfservice-subscription</artifactId>
    // <version>6.1.2-dcmt-61-SNAPSHOT</version>
    //
    // <parent>
    //     <groupId>com.teliasonera.agora.selfservice.subscription</groupId>
    //     <artifactId>selfservice-subscription</artifactId>
    //     <version>6.1.2-dcmt-61-SNAPSHOT</version>
    //     <relativePath>..</relativePath>
    // </parent>
    // <groupId>com.teliasonera.agora.selfservice.administration</groupId>
    // <artifactId>selfservice-administration</artifactId>
    //
    // <parent>
    //     <groupId>com.teliasonera.agora.selfservice.administration</groupId>
    //     <artifactId>selfservice-administration</artifactId>
    //     <version>6.1.2-dcmt-61-SNAPSHOT</version>
    //     <relativePath>..</relativePath>
    // </parent>
    // <name>${project.artifactId}</name>
    // <artifactId>selfservice-administration-api</artifactId>
    //
    @Override
    public void organizeMavenPomFiles(Collection<PomImpl> poms) {
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("Organize Pom files in file tree hierarchy ...");
        }
        for (PomImpl pom : poms) {
            if (pom.getParentPomDependency().isPresent()) {
                Path parentPomFile = calculateParentPom(pom.getPathToPomFile(), pom.getParentPomDependency().get().getRelativePath());
                for (PomImpl otherPom : poms) {
                    if (parentPomFile.equals(otherPom.getPathToPomFile())) {
                        pom.setParentPom(otherPom);
                        otherPom.addPomThatDependsOnThisPom(pom);
                        break;
                    }
                }
            }
        }
        Collection<PomImpl> rootPoms = new LinkedList<>();
        for (PomImpl pom : poms) {
            if ( ! pom.getParentPom().isPresent()) {
                rootPoms.add(pom);
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            informationSink.information("End organizing Pom files in file tree hierarchy.");
        }
        replacePropertyPlaceholder.replacePlaceholders(rootPoms);
    }

    Path calculateParentPom(Path pathToPomFile, String parentPomRelativePath) {
        Path parentDirectoryToParentPom = pathToPomFile.getParent();
        String normalizedRelativePomPath = parentPomRelativePath;
        if (parentPomRelativePath.endsWith("/") || parentPomRelativePath.endsWith("\\")) {
            normalizedRelativePomPath = parentPomRelativePath + "pom.xml";
        }
        else if ( ! parentPomRelativePath.endsWith("pom.xml") ) {
            normalizedRelativePomPath = parentPomRelativePath + "/pom.xml";
        }
        String split = "/";
        if (normalizedRelativePomPath.contains("\\"))
            split = "\\";
        String[] pathNameArray = normalizedRelativePomPath.split(split);
        int index;
        for (index = 0; index < pathNameArray.length; index++) {
            if ( pathNameArray[index].equals("..") )
                parentDirectoryToParentPom = parentDirectoryToParentPom.getParent();
            else
                break;
        }
        String[] pathNamePathEndingArray = Arrays.copyOfRange(pathNameArray, index, pathNameArray.length);
        parentDirectoryToParentPom = Paths.get(parentDirectoryToParentPom.toString(), pathNamePathEndingArray);
        return parentDirectoryToParentPom;
    }

}
