package org.teinelund.maven.dependencies.domain;

public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;

    public Dependency(final String groupId, final String artifactId, final String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void replaceGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void replaceVersion(final String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "[" + this.groupId + ", " + this.artifactId + ", " + this.version + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if ( ! (other instanceof Dependency) ) {
            return false;
        }
        Dependency otherDependency = (Dependency) other;
        return this.groupId.equals(otherDependency.getGroupId()) &&
                this.artifactId.equals(otherDependency.getArtifactId()) &&
                this.version.equals(otherDependency.getVersion());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.groupId.hashCode();
        result = 31 * result + this.artifactId.hashCode();
        result = 31 * result + this.version.hashCode();
        return result;
    }
}
