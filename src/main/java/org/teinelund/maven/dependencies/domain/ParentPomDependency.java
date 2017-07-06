package org.teinelund.maven.dependencies.domain;

public class ParentPomDependency extends Dependency {

    private String relativePath;

    public ParentPomDependency(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
        this.relativePath = "../pom.xml";
    }

    public ParentPomDependency(String groupId, String artifactId, String version, String relativePath) {
        super(groupId, artifactId, version);
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public String toString() {
        return "[" + this.getGroupId() + ", " + this.getArtifactId() + ", " + this.getVersion() + ", " + this.relativePath + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if ( ! (other instanceof ParentPomDependency) ) {
            return false;
        }
        ParentPomDependency otherDependency = (ParentPomDependency) other;
        return this.getGroupId().equals(otherDependency.getGroupId()) &&
                this.getArtifactId().equals(otherDependency.getArtifactId()) &&
                this.getVersion().equals(otherDependency.getVersion()) &&
                this.relativePath.equals(otherDependency.relativePath);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getGroupId().hashCode();
        result = 31 * result + this.getArtifactId().hashCode();
        result = 31 * result + this.getVersion().hashCode();
        result = 31 * result + this.relativePath.hashCode();
        return result;
    }
}
