# Automated Builds and Artifacts

This document describes the automated build and artifact system for the Veil mod project.

## Overview

The Veil project now includes comprehensive automated build pipelines using GitHub Actions to ensure consistent builds, testing, and artifact generation across all supported platforms.

## Supported Platforms

- **Forge** (Minecraft Forge)
- **NeoForge** (NeoForged)
- **Fabric** (Fabric Loader)
- **Common** (Shared code module)

## GitHub Actions Workflows

### 1. Continuous Integration (`ci.yml`)

**Trigger:** Push and Pull Request to `1.20` and `main` branches

**Purpose:** Validates code changes and ensures builds work correctly

**Features:**
- Builds all platforms
- Runs on Java 17
- Uploads build artifacts for review
- Uploads test results

### 2. Release Build (`release.yml`)

**Trigger:** 
- Git tags starting with `v*`
- Manual workflow dispatch
- Published releases

**Purpose:** Creates official release artifacts

**Features:**
- Builds all platform variants
- Creates GitHub releases automatically
- Uploads release JARs
- Supports manual version specification
- Generates comprehensive release notes

### 3. Test Suite (`test.yml`)

**Trigger:** Push, Pull Request, and daily schedule

**Purpose:** Comprehensive testing across multiple environments

**Features:**
- Tests on Ubuntu, Windows, and macOS
- Tests with Java 17 and 21
- Platform-specific build verification
- Integration testing
- Test result reporting

### 4. Matrix Build (`matrix-build.yml`)

**Trigger:** Manual dispatch and weekly schedule

**Purpose:** Full compatibility testing across all combinations

**Features:**
- Tests all platforms on all OS combinations
- Multi-Java version support
- Artifact compatibility verification
- Generates compatibility reports

### 5. Maven Publishing (`publish-maven.yml`)

**Trigger:** Push to `1.20` branch and tags

**Purpose:** Publishes artifacts to Maven repository

**Features:**
- Publishes to configured Maven repository
- Secure credential handling
- Artifact retention

### 6. Javadoc Deployment (`publish-javadoc.yml`)

**Trigger:** Push to `1.20` branch and manual dispatch

**Purpose:** Generates and publishes API documentation

**Features:**
- Automated Javadoc generation
- GitHub Pages deployment
- Documentation artifact archival

## Artifacts Generated

### For Each Platform

1. **Main JAR**: The primary mod file for distribution
2. **Sources JAR**: Source code for development
3. **Javadoc JAR**: API documentation

### Platform-Specific Artifacts

- `veil-forge-{version}.jar` - Forge mod file
- `veil-fabric-{version}.jar` - Fabric mod file  
- `veil-neoforge-{version}.jar` - NeoForge mod file
- `veil-common-{version}.jar` - Common module (development use)

## Build Environment

### Requirements

- **Java**: 17 (primary), 21 (testing)
- **Gradle**: 8.8 (via wrapper)
- **Operating Systems**: Ubuntu (primary), Windows, macOS

### Gradle Tasks

The following Gradle tasks are used in automation:

```bash
./gradlew clean          # Clean previous builds
./gradlew build          # Build all platforms
./gradlew test           # Run tests
./gradlew publish        # Publish to Maven
./gradlew javadoc        # Generate documentation
```

## Artifact Locations

### GitHub Actions Artifacts

All workflow runs generate artifacts that are available for download:

1. **Build Artifacts**: Available for 30 days
2. **Test Results**: Available for 7 days
3. **Release Artifacts**: Available for 90 days

### GitHub Releases

Official releases include platform-specific JAR files attached to the release.

### Maven Repository

Published artifacts are available via Maven coordinates:

```gradle
dependencies {
    implementation 'foundry.veil:veil-forge:1.0.0'
    implementation 'foundry.veil:veil-fabric:1.0.0'
    implementation 'foundry.veil:veil-neoforge:1.0.0'
}
```

## Configuration

### Secrets Required

For full functionality, the following repository secrets should be configured:

- `MAVEN_USERNAME`: Maven repository username
- `MAVEN_PASSWORD`: Maven repository password  
- `MAVEN_URL`: Maven repository URL

### Environment Variables

The workflows use these environment variables:

- `BUILD_NUMBER`: GitHub run number
- `VERSION`: Build version (from tags or manual input)

## Usage for Developers

### Creating a Release

1. Create and push a tag: `git tag v1.0.0 && git push origin v1.0.0`
2. The release workflow will automatically:
   - Build all platforms
   - Create a GitHub release
   - Upload artifacts
   - Generate release notes

### Testing Changes

1. Push to a branch or create a pull request
2. The CI workflow will:
   - Build and test your changes
   - Provide downloadable artifacts
   - Report any test failures

### Manual Builds

Use workflow dispatch to trigger builds manually:

1. Go to Actions tab in GitHub
2. Select the desired workflow
3. Click "Run workflow"
4. Specify any required parameters

## Monitoring

### Build Status

- Check the Actions tab for workflow status
- Green checkmarks indicate successful builds
- Red X marks indicate failures requiring attention

### Artifacts

- Download artifacts from workflow run pages
- Release artifacts are permanently available on the Releases page
- Maven artifacts are available through the configured repository

## Migration from Jenkins

The existing Jenkins pipeline (`.jenkins/Jenkinsfile`) remains functional and can run in parallel with GitHub Actions. Key differences:

- **Jenkins**: Focuses on Maven publishing
- **GitHub Actions**: Comprehensive CI/CD with multiple triggers and environments

Both systems can coexist during the transition period.

## Troubleshooting

### Repository Access Issues

The Veil project requires access to several Maven repositories for Gradle plugins and dependencies:

- `maven.fabricmc.net` - Fabric Loom plugin and dependencies
- `maven.minecraftforge.net` - Minecraft Forge Gradle plugin
- `maven.neoforged.net` - NeoForge Gradle plugin  
- `repo.spongepowered.org` - Sponge/Mixin dependencies

If builds fail with plugin resolution errors, ensure these repositories are accessible. In environments with firewall restrictions:

#### For GitHub Actions

1. **Repository Settings**: Add required domains to the repository's allowlist:
   - Go to Settings → Copilot → Coding Agent Settings
   - Add the Maven repository URLs to the allowlist
   
2. **Actions Setup**: Configure setup steps that run before firewall restrictions:
   ```yaml
   - name: Setup Maven repositories
     run: |
       # Pre-download required plugins and dependencies
       ./gradlew help --no-daemon
   ```

3. **Alternative Approach**: Use third-party actions that handle repository access:
   ```yaml
   - name: Build with Gradle
     uses: gradle/gradle-build-action@v2
     with:
       arguments: clean build --no-daemon
   ```

#### For Local Development

Ensure your network allows access to the required repositories, or configure Gradle to use alternative mirrors:

```gradle
// In settings.gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        // Add mirror repositories if needed
        maven { url 'https://your-mirror-repo.com/maven-public/' }
    }
}
```

### Common Build Issues

#### Plugin Resolution Failures
```
Plugin [id: 'fabric-loom', version: '1.4.2'] was not found
```
**Solution**: Verify repository access and check if alternative plugin repositories are available.

#### Dependency Download Failures
```
Could not resolve all dependencies
```
**Solution**: Check network connectivity to Maven repositories and verify versions in `gradle.properties`.

#### Memory Issues
```
Java heap space OutOfMemoryError
```
**Solution**: Increase Gradle JVM memory in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4G
```

### Getting Help

If builds continue to fail after following these steps:

1. Check the [GitHub Actions logs](../../actions) for detailed error messages
2. Verify all required repositories are accessible from your environment
3. Consider using the existing Jenkins pipeline as an alternative build method
4. Open an issue with complete error logs and environment details