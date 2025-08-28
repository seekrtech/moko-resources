# GitHub Packages Publishing Setup (Private Repository - MOKO Resources)

This guide explains how to publish your **MOKO Resources** library to GitHub Packages for your **private repository**.

## Overview

Your project is configured to publish **all MOKO Resources modules** exclusively to GitHub Packages for private distribution:
- ✅ **resources** → GitHub Packages (private repository)
- ✅ **resources-compose** → GitHub Packages (private repository) 
- ✅ **resources-test** → GitHub Packages (private repository)
- ✅ **resources-generator** → GitHub Packages (private repository)
- 🔄 **Fallback support** for OSSRH (original publishing method still available)

## Quick Start

### 1. Automatic Publishing (Recommended)

The easiest way is to use GitHub Actions:

1. **Create a release tag:**
   ```bash
   git tag v0.24.1-private
   git push origin v0.24.1-private
   ```

2. **Or trigger manual workflow:**
   - Go to your repository on GitHub
   - Navigate to Actions → "Publish MOKO Resources to GitHub Packages"
   - Click "Run workflow"
   - Enter your desired version (e.g., `0.24.1-private`)

### 2. Manual Publishing (Local)

If you prefer to publish from your local machine:

1. **Install GitHub CLI (if not already installed):**
   ```bash
   brew install gh  # On macOS
   # or visit https://cli.github.com/ for other platforms
   ```

2. **Authenticate with GitHub:**
   ```bash
   gh auth login
   ```

3. **Run the publish script:**
   ```bash
   ./publish-to-github-packages.sh 0.24.1-private
   ```

## Configuration Details

### What Gets Published

**All MOKO Resources modules** are configured for publishing with these targets:
- **resources**: Core MOKO Resources library
  - Android, iOS (arm64, x64, simulatorArm64), macOS (arm64, x64), JVM, JS
- **resources-compose**: Compose integration for MOKO Resources
  - Android, iOS (arm64, x64, simulatorArm64), macOS (arm64, x64), JVM, JS
- **resources-test**: Testing utilities for MOKO Resources
  - Android, iOS (arm64, x64, simulatorArm64), macOS (arm64, x64), JVM, JS
- **resources-generator**: Gradle plugin for resource generation
  - JVM

### Environment Variables

The publishing system uses these environment variables:

| Variable | Description | Source |
|----------|-------------|--------|
| `GITHUB_TOKEN` | GitHub authentication token | Auto-provided by GitHub Actions or `gh auth token` |
| `GITHUB_ACTOR` | GitHub username | Auto-detected |
| `GITHUB_REPOSITORY` | Repository in format `username/repo` | Auto-detected |
| `VERSION` | Version to publish | Tag name, manual input, or gradle/moko.versions.toml |

### Repository Structure

Your **private MOKO Resources packages** will be available at:
```
https://github.com/YourUsername/YourRepositoryName/packages
```

The packages will be named:
- `dev.icerock.moko:resources`
- `dev.icerock.moko:resources-compose`
- `dev.icerock.moko:resources-test`
- `dev.icerock.moko:resources-generator`

## Using Published Package

To use your **private MOKO Resources packages** in other projects:

### 1. Add Repository to build.gradle.kts

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/YourUsername/YourRepositoryName")
        credentials {
            username = "YourGitHubUsername"
            password = "your_github_token_here"
        }
    }
}
```

### 2. Add Dependencies

```kotlin
dependencies {
    implementation("dev.icerock.moko:resources:0.24.1-private")
    implementation("dev.icerock.moko:resources-compose:0.24.1-private") // if using Compose
    // testImplementation("dev.icerock.moko:resources-test:0.24.1-private") // if needed for testing
}

// For projects using the resources generator plugin
plugins {
    id("dev.icerock.mobile.multiplatform-resources") version "0.24.1-private"
}
```

### 3. GitHub Token Setup (Required for Private Packages)

For consuming private packages, you'll need a GitHub Personal Access Token with `read:packages` permission:

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with `read:packages` scope
3. Use this token in your `credentials.password`

⚠️ **Important**: Private packages require authentication even for reading!

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Ensure you're authenticated: `gh auth status`
   - Check token permissions include `write:packages` (for publishing) or `read:packages` (for consuming)

2. **Repository Not Found**
   - Verify `GITHUB_REPOSITORY` environment variable
   - Ensure repository exists and you have access

3. **Version Already Exists**
   - GitHub Packages doesn't allow overwriting published versions
   - Use a new version number

4. **Build Failures**
   - Check that all modules build successfully: `./gradlew build`
   - Ensure all required dependencies are available
   - For iOS builds, ensure you're on macOS with Xcode installed

5. **Cannot Access Private Package**
   - Verify you have the correct GitHub token with `read:packages` scope
   - Ensure you have access to the private repository

### Manual Environment Setup

If you need to set environment variables manually:

```bash
export GITHUB_TOKEN="your_github_token"
export GITHUB_ACTOR="your_username"
export GITHUB_REPOSITORY="your_username/your_repo_name"
export VERSION="0.24.1-private"

./gradlew publishAllPublicationsToGitHubPackagesRepository
```

## Version Management

The project version is determined by:
1. `VERSION` environment variable (if set)
2. Git tag name (in GitHub Actions)
3. Default: value from `gradle/moko.versions.toml` with `-private` suffix

To change the default version, update the `resourcesVersion` property in:
```toml
# gradle/moko.versions.toml
resourcesVersion = "0.24.1"  # <- Change this
```

## Advanced Configuration

### Publishing Specific Modules

To publish only specific modules:

```bash
# Publish only resources module
./gradlew :resources:publishAllPublicationsToGitHubPackagesRepository

# Publish only resources-compose module  
./gradlew :resources-compose:publishAllPublicationsToGitHubPackagesRepository
```

### Dry Run

To test the build without actually publishing:

```bash
./gradlew publishToMavenLocal
```

This publishes to your local Maven repository (`~/.m2/repository`) for testing.

## Migration from Public to Private

If you're migrating from using the public MOKO Resources to your private version:

1. **Update your repository configuration** to point to your private GitHub packages
2. **Update dependency versions** to use your private version
3. **Add GitHub token authentication** to your build configuration
4. **Update CI/CD pipelines** to authenticate with GitHub packages

## Security & Privacy Notes

- 🔒 **Authentication Required**: Package access requires GitHub authentication
- 🔑 **Token Security**: Never commit GitHub tokens to your repository
- 🏢 **Team Access**: Only users with repository access can consume packages
- 📦 **Package Visibility**: Package inherits repository visibility (private)
- 🔄 **Dual Publishing**: OSSRH publishing is still available as fallback

## Support

If you encounter issues:
1. Check the GitHub Actions logs for detailed error messages
2. Verify your authentication with `gh auth status`
3. Ensure your repository has Packages enabled in Settings
4. Check that you have the necessary permissions for the repository
5. Verify that your GitHub token has the correct scopes (`read:packages` or `write:packages`)
6. For iOS-related publishing issues, ensure you're using a macOS runner with Xcode

## Why Private GitHub Packages?

This configuration ensures:
- 🔐 **Complete Privacy**: Your fork and customizations remain private
- 🏢 **Team Control**: Only authorized team members can access the package
- 🎯 **Custom Versions**: Use your own versioning scheme independent of upstream
- 🔄 **Fork Independence**: Publish from your fork without affecting upstream
- ⚡ **Fast Distribution**: Distribute to your team without going through public channels