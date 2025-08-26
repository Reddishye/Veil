# Automation Guide

This repository uses GitHub Actions for automated builds and releases.

## Current Status

✅ **Forge** - Working  
✅ **Fabric** - Working  
❌ **NeoForge** - Disabled due to configuration issues  

## Workflows

### CI Build (`ci.yml`)
- Runs on every push and pull request
- Builds Forge and Fabric platforms
- Uploads artifacts for 7 days

### Release (`release.yml`)
- Triggered by version tags (`v*`) or manual dispatch
- Builds all working platforms and creates GitHub release

## Quick Start

### For Development
```bash
./gradlew :forge:build    # Build Forge
./gradlew :fabric:build   # Build Fabric
```

### For Releases
```bash
git tag v1.0.0
git push origin v1.0.0   # Triggers release workflow
```

## Known Issues

- **NeoForge**: Currently disabled due to Gradle plugin compatibility issues
- **Repository Access**: Some builds may fail in restricted environments due to Maven repository access requirements

## Repository Access

The build system requires access to:
- `maven.fabricmc.net` (Fabric)
- `maven.minecraftforge.net` (Forge)
- `repo.spongepowered.org` (Mixin)

If builds fail with connection errors, these domains may need to be added to allowlists in restricted environments.