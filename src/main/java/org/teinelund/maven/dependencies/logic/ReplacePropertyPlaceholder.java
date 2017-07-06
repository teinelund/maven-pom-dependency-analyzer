package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.domain.PomImpl;

import java.util.Collection;

public interface ReplacePropertyPlaceholder {
    public void replacePlaceholders(Collection<PomImpl> pomImplXmlFiles);
}
