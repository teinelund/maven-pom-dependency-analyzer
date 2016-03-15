package org.teinelund.maven.dependencies;

import java.nio.file.Path;
import java.util.*;

public class Pom {
    private Dependency dependency;
    private List<Dependency> dependencies;
    private List<String> modules;
    private List<Pom> pomDependencies;
    private List<Pom> pomsThatDependsOnThisPom;
    private List<Pom> modulesPoms;
    private Map<String, String> properties;
    private Pom parentModulePom;
    private Path pathToPomFile;

    public Pom(final Dependency dependency, final Path pathToPomFile) {
        this.dependency = dependency;
        this.pathToPomFile = pathToPomFile;
        this.dependencies = new LinkedList<>();
        this.modules = new LinkedList<>();
        this.pomDependencies = new LinkedList<>();
        this.pomsThatDependsOnThisPom = new LinkedList<>();
        this.modulesPoms = new LinkedList<>();
        this.properties = new HashMap<>();
    }

    public Dependency getDependency() {
        return this.dependency;
    }

    public Path getPathToPomFile() {
        return this.pathToPomFile;
    }

    public void addDependency(final Dependency dependency) {
        this.dependencies.add(dependency);
    }

    public List<Dependency> getDependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }

    public void addPomDependency(final Pom pom) {
        this.pomDependencies.add(pom);
    }

    public List<Pom> getPomDependencies() {
        return Collections.unmodifiableList(this.pomDependencies);
    }

    public void addPomThatDependsOnThisPom(final Pom pom) {
        this.pomsThatDependsOnThisPom.add(pom);
    }

    public List<Pom> getPomsThatDependsOnThisPom() {
        return Collections.unmodifiableList(this.pomsThatDependsOnThisPom);
    }

    public void addModule(final String moduleName) {
        this.modules.add(moduleName);
    }

    public List<String> getModuleNames() {
        return Collections.unmodifiableList(this.modules);
    }

    public void addModulePom(final Pom pom) {
        this.modulesPoms.add(pom);
    }

    public List<Pom> getModulesPoms() {
        return Collections.unmodifiableList(this.modulesPoms);
    }

    public void addProperty(final String key, final String value) {
        this.properties.put(key,value);
    }

    public boolean existProperty(String key) {
        return this.properties.containsKey(key);
    }

    public String getProperty(final String key) {
        return this.properties.get(key);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public void setParentModulePom(final Pom pom) {
        this.parentModulePom = pom;
    }

    public Pom getParentModulePom() {
        return this.parentModulePom;
    }

    public void filterDependencyList(final List<String> groupIdFilter) {
        List<Dependency> newDependencies = new LinkedList<>();
        for (Dependency dependency : this.dependencies) {
            for (String groupIdPart : groupIdFilter) {
                if (dependency.getGroupId().startsWith(groupIdPart)) {
                    newDependencies.add(dependency);
                    break;
                }
            }
        }
        if ( ! newDependencies.isEmpty() ) {
            this.dependencies = newDependencies;
        }
    }

    public boolean isMyApiDepedendeByOnlyThisImpl() {
        if (this.parentModulePom != null) {
            String parentArtifactIdName = this.parentModulePom.getDependency().getArtifactId();
            if (this.dependency.getArtifactId().startsWith(parentArtifactIdName) && this.dependency.getArtifactId().endsWith("impl")) {
                for (Pom pom : this.pomDependencies) {
                    if (pom.dependency.getGroupId().equals(this.dependency.getGroupId()) &&
                            pom.dependency.getArtifactId().startsWith(parentArtifactIdName) &&
                            pom.dependency.getArtifactId().endsWith("api")) {
                        if (pom.getPomsThatDependsOnThisPom().size() == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  " + this.dependency.toString()); sb.append(Application.getNewLine());
        sb.append("    " + this.pathToPomFile.toString()); sb.append(Application.getNewLine());
        if ( ! this.dependencies.isEmpty()) {
            sb.append("    Dependencies:"); sb.append(Application.getNewLine());
            for (Dependency dependency : this.dependencies) {
                sb.append("      " + dependency.toString()); sb.append(Application.getNewLine());
            }
        }
        if ( ! this.modules.isEmpty() ) {
            sb.append("    Modules:"); sb.append(Application.getNewLine());
            for (String moduleName : this.modules) {
                sb.append("      " + moduleName); sb.append(Application.getNewLine());
            }
        }
        if ( ! this.properties.isEmpty() ) {
            sb.append("    Properties:"); sb.append(Application.getNewLine());
            for (String key : this.properties.keySet()) {
                sb.append("      " + key + " : " + this.properties.get(key)); sb.append(Application.getNewLine());
            }
        }
        return sb.toString();
    }

}
