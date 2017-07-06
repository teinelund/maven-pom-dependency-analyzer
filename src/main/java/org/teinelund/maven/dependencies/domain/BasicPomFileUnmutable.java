package org.teinelund.maven.dependencies.domain;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BasicPomFileUnmutable {
    public Optional<ParentPomDependency> getParentPomDependency();
    public Dependency getDependency();
    public Path getPathToPomFile();
    public List<String> getModuleNames();
    public List<Dependency> getDependencies();
    public boolean existProperty(String key);
    public String getProperty(final String key);
    public Map<String, String> getProperties();
}
