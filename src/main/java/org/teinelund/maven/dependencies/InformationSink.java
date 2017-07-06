package org.teinelund.maven.dependencies;

public interface InformationSink {
    public void information(String informationMessage);
    public void warning(String warningMessage);
    public void error(String errorMessage);
}
