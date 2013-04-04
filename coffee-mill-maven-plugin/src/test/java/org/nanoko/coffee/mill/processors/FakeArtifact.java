package org.nanoko.coffee.mill.processors;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * A fake implementation of artifact.
 * For testing purpose only.
 */
public class FakeArtifact implements Artifact {
    private File artifactFile;
    private String version;
    private String groupId;
    private String artifactId;

    private String classifier;

    public FakeArtifact(String groupId, String artifactId, String version, File artifactFile) {
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
        setFile(artifactFile);
    }

    public FakeArtifact(String groupId, String artifactId, String version, String classifier, File artifactFile) {
        this(groupId, artifactId, version, artifactFile);
        this.classifier = classifier;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return "compile";
    }

    public void setScope(String scope) {
        // Nothing (compile set)
    }

    public String getType() {
        return FilenameUtils.getExtension(artifactFile.getName());
    }

    public String getClassifier() {
        return classifier;
    }

    public boolean hasClassifier() {
        return classifier != null;
    }

    public File getFile() {
        return artifactFile;
    }

    public void setFile(File destination) {
        artifactFile = destination;
    }

    public String getBaseVersion() {
        return version;
    }

    public void setBaseVersion(String baseVersion) {
        // Do nothing.
    }

    public String getId() {
        return groupId + ":" + artifactId + ":" + getType() + ":" + version;
    }

    public String getDependencyConflictId() {
        return null;
    }

    public void addMetadata(ArtifactMetadata metadata) {
        // Nothing.
    }

    public ArtifactMetadata getMetadata(Class<?> metadataClass) {
        return null;
    }

    public Collection<ArtifactMetadata> getMetadataList() {
        return null;
    }

    public ArtifactRepository getRepository() {
        return null;
    }

    public void setRepository(ArtifactRepository remoteRepository) {
        // Nothing.
    }

    public void updateVersion(String version, ArtifactRepository localRepository) {
        // Nothing.
    }

    public String getDownloadUrl() {
        return null;
    }

    public void setDownloadUrl(String downloadUrl) {
        // Nothing.
    }

    public ArtifactFilter getDependencyFilter() {
        return null;
    }

    public void setDependencyFilter(ArtifactFilter artifactFilter) {
        // Nothing.
    }

    public ArtifactHandler getArtifactHandler() {
        return null;
    }

    public void setArtifactHandler(ArtifactHandler handler) {
        // Nothing.
    }

    public List<String> getDependencyTrail() {
        return null;
    }

    public void setDependencyTrail(List<String> dependencyTrail) {
        // Nothing.
    }

    public VersionRange getVersionRange() {
        return null;
    }

    public void setVersionRange(VersionRange newRange) {
        // Nothing.
    }

    public void selectVersion(String version) {
        // Nothing.
    }

    public boolean isSnapshot() {
        return version.endsWith("-SNAPSHOT");
    }

    public boolean isResolved() {
        return true; // We inject the file.
    }

    public void setResolved(boolean resolved) {
        // Nothing.
    }

    public void setResolvedVersion(String version) {
        // Nothing.
    }

    public boolean isRelease() {
        return !isSnapshot();
    }

    public void setRelease(boolean release) {
        // Nothing.
    }

    public List<ArtifactVersion> getAvailableVersions() {
        return null;
    }

    public void setAvailableVersions(List<ArtifactVersion> versions) {
        // Nothing.
    }

    public boolean isOptional() {
        return false;
    }

    public void setOptional(boolean optional) {
        // Nothing.
    }

    public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
        return null;
    }

    public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
        return false;
    }

    public int compareTo(Artifact o) {
        return getId().compareTo(o.getId());
    }
}
