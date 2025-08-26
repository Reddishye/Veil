# NeoForge Implementation Summary

This document summarizes the NeoForge 1.20.1 support implementation for Veil.

## Files Created

### Core Module Files
- `VeilNeoForge.java` - Main mod entry point using NeoForge ModContainer
- `VeilNeoForgeClient.java` - Client-side initialization with NeoForge event bus
- `VeilNeoForgeClientEvents.java` - Client event handlers for NeoForge events

### Platform Implementation
- `NeoForgeVeilPlatform.java` - Platform detection and mod loading queries
- `NeoForgeVeilEventPlatform.java` - Event system integration
- `NeoForgeVeilClientPlatform.java` - Client-specific platform features
- `NeoForgeRegistrationFactory.java` - Registry system integration

### Event System
- `NeoForgeVeilRendererEvent.java` - Renderer availability events
- `NeoForgeFreeNativeResourcesEvent.java` - Resource cleanup events
- `NeoForgeVeilPostProcessingEvent.java` - Post-processing hooks
- `NeoForgeVeilRegisterBlockLayerEvent.java` - Block layer registration
- `NeoForgeVeilRegisterFixedBuffersEvent.java` - Fixed buffer registration

### Rendering Support
- `NeoForgeRenderTypeStageHandler.java` - Render stage management

### Service Integration
- Service loader files in `META-INF/services/` for platform discovery

### Build Configuration
- `build.gradle` with NeoForge 47.1.106 dependency
- `mods.toml` configured for NeoForge loader
- Mixin configuration adapted for NeoForge

## Key Differences from Forge

1. **Package Names**: `net.neoforged` instead of `net.minecraftforge`
2. **Event Bus**: NeoForge uses different event bus structure
3. **Mod Loading**: Uses ModContainer parameter in main class
4. **Platform Type**: Added NEOFORGE to the platform enum
5. **Dependencies**: Uses `net.neoforged:neoforge:47.1.106`

## Integration Points

The NeoForge implementation integrates with the existing Veil common module through:
- Platform service discovery
- Event system abstractions  
- Registry provider factories
- Client platform hooks

This allows Veil to support both Forge and NeoForge simultaneously while sharing the common rendering and API code.