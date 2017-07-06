package org.teinelund.maven.dependencies.logic;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface MavenProjectDirectoryPathsVerifier {
    public void analyzePaths() throws ParserConfigurationException, SAXException, IOException;
}
