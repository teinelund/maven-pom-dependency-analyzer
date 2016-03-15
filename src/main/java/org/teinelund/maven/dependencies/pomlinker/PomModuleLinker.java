package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.ApplicationException;
import org.teinelund.maven.dependencies.Pom;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PomModuleLinker implements Linker<List<Pom>> {

    private CommandLineOptions options;
    private Linker<List<Pom>> linker;
    private StringBuilder warnings;

    public PomModuleLinker(final CommandLineOptions options, final Linker<List<Pom>> linker, final StringBuilder warnings) {
        this.options = options;
        this.linker = linker;
        this.warnings = warnings;
    }

    @Override
    public void process(List<Pom> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Process Pom Modules...");
        }
        Map<String, List<Pom>> pomMap = PomFileDependencyLinker.buildPomMap(input, warnings);
        for (Pom pom : input) {
            for(String moduleName : pom.getModuleNames()) {
                List<Pom> pomMapList = pomMap.get(moduleName);
                if (pomMapList.size() == 0) {
                    StringBuilder error = new StringBuilder();
                    error.append("Module name: ");
                    error.append(moduleName);
                    error.append(" don't have a Pom file registred."); error.append(Application.getNewLine());
                    error.append("Affected pom:"); error.append(Application.getNewLine());
                    error.append(pom.toString());
                    throw new ApplicationException(error.toString());
                }
                else if (pomMapList.size() == 1) {
                    Pom modulePom = pomMapList.get(0);
                    pom.addModulePom(modulePom);
                    modulePom.setParentModulePom(pom);
                }
                else {
                    boolean foundModulePom = false;
                    for (Pom modulePom : pomMapList) {
                        if (modulePom.getDependency().getGroupId().equals(pom.getDependency().getGroupId())) {
                            if (modulePom.getDependency().getVersion().equals(pom.getDependency().getVersion())) {
                                foundModulePom = true;
                                pom.addModulePom(modulePom);
                                modulePom.setParentModulePom(pom);
                            }
                        }
                        else if (modulePom.getDependency().getGroupId().equals(Application.getGroupIdPlaceholder())) {
                            if (modulePom.getPathToPomFile().getParent().toString().startsWith(
                                    pom.getPathToPomFile().getParent().toString())) {
                                foundModulePom = true;
                                pom.addModulePom(modulePom);
                                modulePom.setParentModulePom(pom);
                            }
                        }
                        else if (modulePom.getDependency().getGroupId().equals("?")) {
                            if (modulePom.getPathToPomFile().getParent().toString().startsWith(
                                    pom.getPathToPomFile().getParent().toString())) {
                                foundModulePom = true;
                                pom.addModulePom(modulePom);
                                modulePom.setParentModulePom(pom);
                            }
                        }
                    }
                    if (! foundModulePom) {
                        warnings.append("Module name " + moduleName + " don't have a Pom file registred."); warnings.append(Application.getNewLine());
                        warnings.append("  Pom files in pom file list:"); warnings.append(Application.getNewLine());
                        for (Pom pomInPomList : pomMapList ) {
                            warnings.append("  ");
                            warnings.append(pomInPomList.getDependency().toString()); warnings.append(Application.getNewLine());
                        }
                    }
                }
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Connected module pom.xml files:");
            for (Pom pom : input) {
                if ( ! pom.getModulesPoms().isEmpty() ) {
                    System.out.println("  " + pom.getDependency().toString() + " has the modules:");
                    for (Pom modulePom : pom.getModulesPoms()) {
                        System.out.println("  +-" + modulePom.getDependency().toString());
                    }
                }
            }
        }
        this.linker.process(input);
    }
}
