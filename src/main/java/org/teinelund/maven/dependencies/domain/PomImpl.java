package org.teinelund.maven.dependencies.domain;

import org.teinelund.maven.dependencies.Application;

import java.nio.file.Path;
import java.util.*;

public class PomImpl implements BasicPomFileMutable, BasicPomFileUnmutable {

    // Set by the constructor
    private Optional<ParentPomDependency> parentPomDependencyOption;
    private Dependency dependency;
    private Path pathToPomFile;
    // Used by PomBuilder
    private List<String> modules;
    private Map<String, String> properties;
    private List<Dependency> dependencies;
    // wireings
    private List<PomImpl> pomImplDependencies;
    private List<PomImpl> pomsThatDependsOnThisPomImpl;
    private List<PomImpl> modulesPomImpls;
    private Optional<PomImpl> parentPom;

    public PomImpl(Optional<ParentPomDependency> parentPomDependencyOption, final Dependency dependency, final Path pathToPomFile) {
        this.parentPomDependencyOption = parentPomDependencyOption;
        this.dependency = dependency;
        this.pathToPomFile = pathToPomFile;
        this.dependencies = new LinkedList<>();
        this.modules = new LinkedList<>();
        this.pomImplDependencies = new LinkedList<>();
        this.pomsThatDependsOnThisPomImpl = new LinkedList<>();
        this.modulesPomImpls = new LinkedList<>();
        this.properties = new HashMap<>();
        this.parentPom = Optional.empty();
    }

    //
    // These are set at the constructor
    //

    /**
     * Parent pom file, if present: groupId, artifactId, version and relative path. Optional.
     * @return Optional<ParentPomDependency>
     */
    @Override
    public Optional<ParentPomDependency> getParentPomDependency() {
        return this.parentPomDependencyOption;
    }

    /**
     * Pom file groupId, artifactId and version
     * @return Dependency
     */
    @Override
    public Dependency getDependency() {
        return this.dependency;
    }

    /**
     * Path to pom file.
     * @return Path
     */
    @Override
    public Path getPathToPomFile() {
        return this.pathToPomFile;
    }

    //
    // These are used by PomBuilder
    //
    @Override
    public void addDependency(final Dependency dependency) {
        this.dependencies.add(dependency);
    }

    @Override
    public void addModule(final String moduleName) {
        this.modules.add(moduleName);
    }

    @Override
    public void addProperty(final String key, final String value) {
        this.properties.put(key,value);
    }

    @Override
    public void addProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    //
    // Getters for the Setters that PomBuilder use
    //
    @Override
    public List<String> getModuleNames() {
        return Collections.unmodifiableList(this.modules);
    }

    @Override
    public List<Dependency> getDependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }

    @Override
    public boolean existProperty(String key) {
        return this.properties.containsKey(key);
    }

    @Override
    public String getProperty(final String key) {
        return this.properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }


    //
    // Wireings
    //

    public void addPomDependency(final PomImpl pomImpl) {
        this.pomImplDependencies.add(pomImpl);
    }

    public List<PomImpl> getPomImplDependencies() {
        return Collections.unmodifiableList(this.pomImplDependencies);
    }


    public void addPomThatDependsOnThisPom(final PomImpl pomImpl) {
        this.pomsThatDependsOnThisPomImpl.add(pomImpl);
    }

    public List<PomImpl> getPomsThatDependsOnThisPomImpl() {
        return Collections.unmodifiableList(this.pomsThatDependsOnThisPomImpl);
    }


    public void addModulePom(final PomImpl pomImpl) {
        this.modulesPomImpls.add(pomImpl);
    }

    public List<PomImpl> getModulesPomImpls() {
        return Collections.unmodifiableList(this.modulesPomImpls);
    }


    public void setParentPom(final PomImpl pom) {
        this.parentPom = Optional.of(pom);
    }

    public Optional<PomImpl> getParentPom() {
        return this.parentPom;
    }


    //
    // Helpers
    //

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

    /*public boolean isMyApiDepedendeByOnlyThisImpl() {
        if (this.parentPom != null) {
            String parentArtifactIdName = this.parentPom.getDependency().getArtifactId();
            if (this.dependency.getArtifactId().startsWith(parentArtifactIdName) && this.dependency.getArtifactId().endsWith("impl")) {
                for (PomImpl pomImpl : this.pomImplDependencies) {
                    if (pomImpl.dependency.getGroupId().equals(this.dependency.getGroupId()) &&
                            pomImpl.dependency.getArtifactId().startsWith(parentArtifactIdName) &&
                            pomImpl.dependency.getArtifactId().endsWith("api")) {
                        if (pomImpl.getPomsThatDependsOnThisPomImpl().size() == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }*/

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
