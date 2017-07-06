package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.domain.PomImpl;

import java.util.Collection;

public interface MavenPomFileHierarchyOrganizer {
    public void organizeMavenPomFiles(Collection<PomImpl> poms);
}
