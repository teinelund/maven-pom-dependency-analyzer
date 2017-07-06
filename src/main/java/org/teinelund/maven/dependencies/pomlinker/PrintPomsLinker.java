package org.teinelund.maven.dependencies.pomlinker;

import org.apache.commons.lang3.tuple.Pair;
import org.teinelund.maven.dependencies.domain.Dependency;
import org.teinelund.maven.dependencies.domain.PomImpl;
import org.teinelund.maven.dependencies.commandline.CommandLineOptions;
import org.teinelund.maven.dependencies.commandline.OPTION;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrintPomsLinker implements Linker<List<PomImpl>> {

    private CommandLineOptions options;
    private List<Pair<PomImpl, List<Dependency>>> missingDependencies;

    public PrintPomsLinker(final CommandLineOptions options, final List<Pair<PomImpl, List<Dependency>>> missingDependencies) {
        this.options = options;
        this.missingDependencies = missingDependencies;
    }

    @Override
    public void process(List<PomImpl> input) throws IOException, ParserConfigurationException, SAXException {
        for (PomImpl pomImpl : input) {
            if (pomImpl.getPomsThatDependsOnThisPomImpl().size() == 0 &&
                    pomImpl.getPomImplDependencies().size() > 0 /* &&
                    pomImpl.isMyApiDepedendeByOnlyThisImpl() */) {
                printPom(pomImpl, 0);
                System.out.println("");
            }
        }
        if (options.isOption(OPTION.GROUP_ID_PARTS)) {
            System.out.println("Missing dependencies:");
            if (options.isOption(OPTION.VERBOSE)) {
                for (Pair<PomImpl, List<Dependency>> pair : this.missingDependencies) {
                    System.out.println("  PomImpl : " + pair.getKey().getDependency().toString());
                    System.out.println("  + " + pair.getKey().getPathToPomFile().toString());
                    for (Dependency dependency : pair.getValue()) {
                        System.out.println("    + " + dependency.toString());
                    }
                }
            }
            else {
                Set<Dependency> dependencySet = new HashSet<>();
                for (Pair<PomImpl, List<Dependency>> pair : this.missingDependencies) {
                    for (Dependency dependency : pair.getValue()) {
                        dependencySet.add(dependency);
                    }
                }
                for (Dependency dependency : dependencySet) {
                    System.out.println("    + " + dependency.toString());
                }
            }
        }
    }

    void printPom(final PomImpl pomImpl, final int level) {
        StringBuilder plusAndSpace = new StringBuilder();
        for (int i=0; i<level-1; i++) {
            plusAndSpace.append("  ");
        }
        if (level > 0) {
            plusAndSpace.append("+ ");
        }
        StringBuilder space = new StringBuilder();
        for (int i=0; i<level; i++) {
            space.append("  ");
        }
        System.out.print(plusAndSpace.toString());
        System.out.println( pomImpl.getDependency().toString() );
        System.out.print(space.toString());
        System.out.println( pomImpl.getPathToPomFile().toString() );
        for ( PomImpl pomImplDependency : pomImpl.getPomImplDependencies() ) {
            printPom(pomImplDependency, level + 1);
        }
    }
}
