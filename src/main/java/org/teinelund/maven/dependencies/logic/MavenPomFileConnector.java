package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.domain.PomImpl;

import java.util.Collection;

public interface MavenPomFileConnector {
    public void connectMavenPomFiles(Collection<PomImpl> pomImplXmlFiles);
}
