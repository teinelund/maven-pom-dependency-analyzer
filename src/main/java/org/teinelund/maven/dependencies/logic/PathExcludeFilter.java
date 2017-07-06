package org.teinelund.maven.dependencies.logic;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface PathExcludeFilter {
    public void excludePaths(Collection<Path> pomXmlFiles) throws ParserConfigurationException, SAXException, IOException;
}
