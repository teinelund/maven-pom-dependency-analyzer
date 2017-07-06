package org.teinelund.maven.dependencies.pomlinker;

import org.teinelund.maven.dependencies.Application;
import org.teinelund.maven.dependencies.ApplicationException;
import org.teinelund.maven.dependencies.domain.PomImpl;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PomModuleLinker implements Linker<List<PomImpl>> {

    private CommandLineOptions options;
    private Linker<List<PomImpl>> linker;
    private StringBuilder warnings;

    public PomModuleLinker(final CommandLineOptions options, final Linker<List<PomImpl>> linker, final StringBuilder warnings) {
        this.options = options;
        this.linker = linker;
        this.warnings = warnings;
    }

    @Override
    public void process(List<PomImpl> input) throws IOException, ParserConfigurationException, SAXException {
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Process PomImpl Modules...");
        }
        Map<String, List<PomImpl>> pomMap = PomFileDependencyLinker.buildPomMap(input, warnings);
        for (PomImpl pomImpl : input) {
            for(String moduleName : pomImpl.getModuleNames()) {
                List<PomImpl> pomImplMapList = pomMap.get(moduleName);
                if (pomImplMapList.size() == 0) {
                    StringBuilder error = new StringBuilder();
                    error.append("Module name: ");
                    error.append(moduleName);
                    error.append(" don't have a PomImpl file registred."); error.append(Application.getNewLine());
                    error.append("Affected pomImpl:"); error.append(Application.getNewLine());
                    error.append(pomImpl.toString());
                    throw new ApplicationException(error.toString());
                }
                else if (pomImplMapList.size() == 1) {
                    PomImpl modulePomImpl = pomImplMapList.get(0);
                    pomImpl.addModulePom(modulePomImpl);
                    modulePomImpl.setParentPom(pomImpl);
                }
                else {
                    boolean foundModulePom = false;
                    for (PomImpl modulePomImpl : pomImplMapList) {
                        if (modulePomImpl.getDependency().getGroupId().equals(pomImpl.getDependency().getGroupId())) {
                            if (modulePomImpl.getDependency().getVersion().equals(pomImpl.getDependency().getVersion())) {
                                foundModulePom = true;
                                pomImpl.addModulePom(modulePomImpl);
                                modulePomImpl.setParentPom(pomImpl);
                            }
                        }
                        else if (modulePomImpl.getDependency().getGroupId().equals(Application.getGroupIdPlaceholder())) {
                            if (modulePomImpl.getPathToPomFile().getParent().toString().startsWith(
                                    pomImpl.getPathToPomFile().getParent().toString())) {
                                foundModulePom = true;
                                pomImpl.addModulePom(modulePomImpl);
                                modulePomImpl.setParentPom(pomImpl);
                            }
                        }
                        else if (modulePomImpl.getDependency().getGroupId().equals("?")) {
                            if (modulePomImpl.getPathToPomFile().getParent().toString().startsWith(
                                    pomImpl.getPathToPomFile().getParent().toString())) {
                                foundModulePom = true;
                                pomImpl.addModulePom(modulePomImpl);
                                modulePomImpl.setParentPom(pomImpl);
                            }
                        }
                    }
                    if (! foundModulePom) {
                        warnings.append("Module name " + moduleName + " don't have a PomImpl file registred."); warnings.append(Application.getNewLine());
                        warnings.append("  PomImpl files in pomImpl file list:"); warnings.append(Application.getNewLine());
                        for (PomImpl pomInPomImplList : pomImplMapList) {
                            warnings.append("  ");
                            warnings.append(pomInPomImplList.getDependency().toString()); warnings.append(Application.getNewLine());
                        }
                    }
                }
            }
        }
        if (options.isOption(OPTION.VERBOSE)) {
            System.out.println("Connected module pom.xml files:");
            for (PomImpl pomImpl : input) {
                if ( ! pomImpl.getModulesPomImpls().isEmpty() ) {
                    System.out.println("  " + pomImpl.getDependency().toString() + " has the modules:");
                    for (PomImpl modulePomImpl : pomImpl.getModulesPomImpls()) {
                        System.out.println("  +-" + modulePomImpl.getDependency().toString());
                    }
                }
            }
        }
        this.linker.process(input);
    }
}
