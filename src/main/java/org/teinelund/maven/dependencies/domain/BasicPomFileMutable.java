package org.teinelund.maven.dependencies.domain;

import java.util.Map;

public interface BasicPomFileMutable {
    public void addDependency(final Dependency dependency);
    public void addModule(final String moduleName);
    public void addProperty(final String key, final String value);
    public void addProperties(final Map<String, String> properties);
}
