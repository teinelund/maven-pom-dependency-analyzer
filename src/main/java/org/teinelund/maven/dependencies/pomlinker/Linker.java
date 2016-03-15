package org.teinelund.maven.dependencies.pomlinker;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface Linker<U> {
    public void process(U input) throws IOException, ParserConfigurationException, SAXException;
}
