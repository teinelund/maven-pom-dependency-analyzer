package org.teinelund.maven.dependencies;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.DOMBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public class PomBuilder {

    private InputStream pomfile;
    private Path path;
    private Pom pom;

    public PomBuilder(InputStream pomfile, Path path) {
        this.pomfile = pomfile;
        this.path = path;
    }

    public void build() throws ParserConfigurationException, SAXException, IOException {
        Element rootElement = readPomFile(this.pomfile, this.path);
        Dependency dependency = fetchPomGroupIdArtifactIdAndVersion(rootElement);
        this.pom = new Pom(dependency, this.path);
        fetchPomDependencies(rootElement, this.pom);
        fetchModules(rootElement, this.pom);
        fetchProperties(rootElement, this.pom);
    }

    public Pom getResult() {
        return this.pom;
    }

    Element readPomFile(InputStream pomfile, Path path) throws IOException, SAXException, ParserConfigurationException {
        // create the w3c DOM document from which JDOM is to be created
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // we are interested in making it namespace aware.
        factory.setNamespaceAware(true);
        DocumentBuilder dombuilder = factory.newDocumentBuilder();

        org.w3c.dom.Document w3cDocument = dombuilder.parse(pomfile);

        // w3cDocument is the w3c DOM object. we now build the JDOM2 object

        // the DOMBuilder uses the DefaultJDOMFactory to create the JDOM2
        // objects.
        DOMBuilder jdomBuilder = new DOMBuilder();

        // jdomDocument is the JDOM2 Object
        Document jdomDocument = jdomBuilder.build(w3cDocument);

        // The root element is the root of the document. we print its name
        Element rootElement = jdomDocument.getRootElement();
        return rootElement;
    }

    Dependency fetchPomGroupIdArtifactIdAndVersion(final Element rootElement) {
        Namespace namespace = rootElement.getNamespace();
        Element groupIdElement = rootElement.getChild("groupId", namespace);
        String groupId = "?";
        if (groupIdElement == null) {
            groupId = rootElement.getChild("parent", namespace).getChild("groupId", namespace).getText();
        } else {
            groupId = groupIdElement.getText();
        }
        String artifactId = rootElement.getChild("artifactId", namespace).getText();
        Element versionElement = rootElement.getChild("version", namespace);
        String version = "?";
        if (versionElement == null) {
            Element parentVersionElement = rootElement.getChild("parent", namespace).getChild("version", namespace);
            if (parentVersionElement != null) {
                version = parentVersionElement.getText();
            }
        } else {
            version = rootElement.getChild("version", namespace).getText();
        }
        Dependency dependency = new Dependency(groupId, artifactId, version);
        return dependency;
    }

    void fetchPomDependencies(final Element rootElement, final Pom pom) {
        Namespace namespace = rootElement.getNamespace();
        Element dependenciesElement = rootElement.getChild("dependencies", namespace);
        if (dependenciesElement != null) {
            List<Element> dependenciesElementList = dependenciesElement.getChildren();
            for (Element dependencyElement : dependenciesElementList) {
                pom.addDependency(featchDependencyInfoFromPomDependencyElement(dependencyElement, namespace));
            }
        }
    }

    // groupId.equals("${project.groupId}")
    // version.equals("${project.version}")
    Dependency featchDependencyInfoFromPomDependencyElement(final Element dependencyElement, final Namespace namespace) {
        Element groupIdElement = dependencyElement.getChild("groupId", namespace);
        String groupId = "?";
        if (groupIdElement != null) {
            groupId = groupIdElement.getText();
        }
        String artifactId = dependencyElement.getChild("artifactId", namespace).getText();
        Element versionElement = dependencyElement.getChild("version", namespace);
        String version = "?";
        if (versionElement != null) {
            version = versionElement.getText();
        }
        return new Dependency(groupId, artifactId, version);
    }

    void fetchModules(final Element rootElement, final Pom pom) {
        Namespace namespace = rootElement.getNamespace();
        Element modulesElement = rootElement.getChild("modules", namespace);
        if (modulesElement != null) {
            List<Element> moduleElementList = modulesElement.getChildren("module", namespace);
            for (Element moduleElement : moduleElementList) {
                String moduleName = moduleElement.getText();
                pom.addModule(moduleName);
            }
        }
    }

    void fetchProperties(final Element rootElement, final Pom pom) {
        Namespace namespace = rootElement.getNamespace();
        Element propertiesElement = rootElement.getChild("properties", namespace);
        if (propertiesElement != null) {
            List<Element> propertiesElementList = propertiesElement.getChildren();
            for (Element propertyElement : propertiesElementList) {
                String key = propertyElement.getName();
                String value = propertyElement.getText();
                pom.addProperty(key,value);
            }
        }
    }
}
