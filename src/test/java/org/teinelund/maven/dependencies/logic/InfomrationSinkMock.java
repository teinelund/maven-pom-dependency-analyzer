package org.teinelund.maven.dependencies.logic;

import org.teinelund.maven.dependencies.InformationSink;

import java.util.LinkedList;
import java.util.List;

public class InfomrationSinkMock implements InformationSink {

    private List<String> informationMessages = new LinkedList<>();
    private List<String> warningMessages = new LinkedList<>();
    private List<String> errorMessages = new LinkedList<>();

    @Override
    public void information(String informationMessage) {
        this.informationMessages.add(informationMessage);
    }

    @Override
    public void warning(String warningMessage) {
        this.warningMessages.add(warningMessage);
    }

    @Override
    public void error(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }

    public List<String> getInformationMessages() {
        return informationMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
