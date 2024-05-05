package io.github.chains_project.maven_lockfile.reporting;

import com.google.common.collect.Sets;
import io.github.chains_project.maven_lockfile.data.LockFile;
import io.github.chains_project.maven_lockfile.data.MavenPlugin;
import io.github.chains_project.maven_lockfile.graph.DependencyNode;
import org.apache.maven.artifact.Artifact;

import java.util.HashSet;
import java.util.Set;

public class LockFileDifference {

    private final Set<DependencyNode> missingDependenciesInProject;
    private final Set<DependencyNode> missingDependenciesInFile;
    private final Set<DependencyNode> dependenciesWithInvalidChecksums;

    private final Set<MavenPlugin> missingPluginsInProject;
    private final Set<MavenPlugin> missingPluginsInFile;
    private final Set<MavenPlugin> pluginsWithInvalidChecksums;

    private LockFileDifference(
            Set<DependencyNode> missingDependenciesInProject,
            Set<DependencyNode> missingDependenciesInFile,
            Set<DependencyNode> dependenciesWithInvalidChecksums,
            Set<MavenPlugin> missingPluginsInProject,
            Set<MavenPlugin> missingPluginsInFile,
            Set<MavenPlugin> pluginsWithInvalidChecksums) {
        this.missingDependenciesInProject = missingDependenciesInProject;
        this.missingDependenciesInFile = missingDependenciesInFile;
        this.dependenciesWithInvalidChecksums = dependenciesWithInvalidChecksums;
        this.missingPluginsInProject = missingPluginsInProject;
        this.missingPluginsInFile = missingPluginsInFile;
        this.pluginsWithInvalidChecksums = pluginsWithInvalidChecksums;
    }

    // Change this to check for checksum and handle it differently
    public static LockFileDifference diff(LockFile lockFileFromFile, LockFile lockFileFromProject) {
        Set<DependencyNode> dependenciesFromFile = new HashSet<>(lockFileFromFile.getDependencies());
        Set<DependencyNode> dependenciesFromProject = new HashSet<>(lockFileFromProject.getDependencies());
        Set<DependencyNode> missingDependenciesInProject =
                Sets.difference(dependenciesFromFile, dependenciesFromProject);
        Set<DependencyNode> missingDependenciesInFile = Sets.difference(dependenciesFromProject, dependenciesFromFile);
        Set<DependencyNode> dependenciesWithInvalidChecksums = getTamperedDependencies(dependenciesFromFile,
                dependenciesFromProject);
        Set<MavenPlugin> pluginsFromFile = new HashSet<>(lockFileFromFile.getMavenPlugins());
        Set<MavenPlugin> pluginsFromProject = new HashSet<>(lockFileFromProject.getMavenPlugins());
        Set<MavenPlugin> missingPluginsInProject = Sets.difference(pluginsFromFile, pluginsFromProject);
        Set<MavenPlugin> missingPluginsInFile = Sets.difference(pluginsFromProject, pluginsFromFile);
        Set<MavenPlugin> pluginsWithInvalidChecksums = getTamperedPlugins(pluginsFromFile, pluginsFromProject);

        return new LockFileDifference(
                missingDependenciesInProject, missingDependenciesInFile, dependenciesWithInvalidChecksums,
                missingPluginsInProject, missingPluginsInFile, pluginsWithInvalidChecksums);
    }

    private static HashSet<DependencyNode> getTamperedDependencies(Set<DependencyNode> dependenciesFromFile,
                                                               Set<DependencyNode> dependenciesFromProject) {
        HashSet<DependencyNode> tamperedDependencies = new HashSet<>();
        for (DependencyNode dependencyFromFile : dependenciesFromFile) {
            for (DependencyNode dependencyFromProject : dependenciesFromProject) {
                if (isDependencyTamperedWith(dependencyFromFile, dependencyFromProject)) {
                    tamperedDependencies.add(dependencyFromFile);
                }
            }
        }
        return tamperedDependencies;
    }

    private static boolean isDependencyTamperedWith(DependencyNode dependencyFromFile, DependencyNode dependencyFromProject) {
        return dependencyFromFile.getArtifactId().equals(dependencyFromProject.getArtifactId()) &&
                dependencyFromFile.getGroupId().equals(dependencyFromProject.getGroupId()) &&
                dependencyFromFile.getVersion().equals(dependencyFromProject.getVersion()) &&
                dependencyFromFile.getChecksumAlgorithm().equals(dependencyFromProject.getChecksum()) &&
                !dependencyFromFile.getChecksum().equals(dependencyFromProject.getChecksum());
    }

    private static HashSet<MavenPlugin> getTamperedPlugins(Set<MavenPlugin> pluginsFromFile,
                                                           Set<MavenPlugin> pluginsFromProject) {
        HashSet<MavenPlugin> tamperedPlugins = new HashSet<>();
        for (MavenPlugin pluginFromFile : pluginsFromFile) {
            for (MavenPlugin pluginFromProject : pluginsFromProject) {
                if (isPluginTamperedWith(pluginFromFile, pluginFromProject)) {
                   tamperedPlugins.add(pluginFromFile);
                }
            }
        }
        return tamperedPlugins;
    }

    private static boolean isPluginTamperedWith(MavenPlugin pluginFromFile, MavenPlugin pluginFromProject) {
        return pluginFromFile.getArtifactId().equals(pluginFromProject.getArtifactId()) &&
                pluginFromFile.getGroupId().equals(pluginFromProject.getGroupId()) &&
                pluginFromFile.getVersion().equals(pluginFromProject.getVersion()) &&
                pluginFromFile.getChecksumAlgorithm().equals(pluginFromProject.getChecksum()) &&
                !pluginFromFile.getChecksum().equals(pluginFromProject.getChecksum());
    }

    /**
     * @return the missingDependenciesInFile
     */
    public Set<DependencyNode> getMissingDependenciesInFile() {
        return new HashSet<>(missingDependenciesInFile);
    }
    /**
     * @return the missingDependenciesInProject
     */
    public Set<DependencyNode> getMissingDependenciesInProject() {
        return new HashSet<>(missingDependenciesInProject);
    }
    /**
     * @return the dependenciesWithInvalidChecksums
     */
    public Set<DependencyNode> getDependenciesWithInvalidChecksums() {
        return new HashSet<>(dependenciesWithInvalidChecksums);
    }
    /**
     * @return the missingPluginsInFile
     */
    public Set<MavenPlugin> getMissingPluginsInFile() {
        return new HashSet<>(missingPluginsInFile);
    }
    /**
     * @return the missingPluginsInProject
     */
    public Set<MavenPlugin> getMissingPluginsInProject() {
        return new HashSet<>(missingPluginsInProject);
    }
    /**
     * @return the pluginsWithInvalidChecksums
     */
    public Set<MavenPlugin> getPluginsWithInvalidChecksums() {
        return new HashSet<>(pluginsWithInvalidChecksums);
    }
}
