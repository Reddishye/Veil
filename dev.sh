#!/bin/bash

# Veil Development Helper Script
# This script provides common development tasks for the Veil project

set -e

show_help() {
    echo "Veil Development Helper"
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  build          - Build all platforms"
    echo "  build-forge    - Build only Forge"
    echo "  build-fabric   - Build only Fabric"
    echo "  build-neoforge - Build only NeoForge"
    echo "  build-common   - Build only Common"
    echo "  test           - Run all tests"
    echo "  clean          - Clean build artifacts"
    echo "  publish-local  - Publish to local Maven repository"
    echo "  check-deps     - Check for dependency updates"
    echo "  help           - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build                 # Build all platforms"
    echo "  $0 build-forge          # Build only Forge version"
    echo "  $0 test                 # Run test suite"
}

ensure_gradlew() {
    if [[ ! -x "./gradlew" ]]; then
        echo "Making gradlew executable..."
        chmod +x ./gradlew
    fi
}

case "${1:-help}" in
    "build")
        echo "🔨 Building all platforms..."
        ensure_gradlew
        ./gradlew clean build --no-daemon
        echo "✅ Build complete! Check */build/libs/ for artifacts."
        ;;
    "build-forge")
        echo "🔨 Building Forge..."
        ensure_gradlew
        ./gradlew :forge:clean :forge:build --no-daemon
        echo "✅ Forge build complete!"
        ;;
    "build-fabric")
        echo "🔨 Building Fabric..."
        ensure_gradlew
        ./gradlew :fabric:clean :fabric:build --no-daemon
        echo "✅ Fabric build complete!"
        ;;
    "build-neoforge")
        echo "🔨 Building NeoForge..."
        ensure_gradlew
        ./gradlew :neoforge:clean :neoforge:build --no-daemon
        echo "✅ NeoForge build complete!"
        ;;
    "build-common")
        echo "🔨 Building Common..."
        ensure_gradlew
        ./gradlew :common:clean :common:build --no-daemon
        echo "✅ Common build complete!"
        ;;
    "test")
        echo "🧪 Running tests..."
        ensure_gradlew
        ./gradlew test --no-daemon
        echo "✅ Tests complete!"
        ;;
    "clean")
        echo "🧹 Cleaning build artifacts..."
        ensure_gradlew
        ./gradlew clean --no-daemon
        echo "✅ Clean complete!"
        ;;
    "publish-local")
        echo "📦 Publishing to local Maven repository..."
        ensure_gradlew
        ./gradlew publishToMavenLocal --no-daemon
        echo "✅ Published to local Maven repository!"
        ;;
    "check-deps")
        echo "🔍 Checking for dependency updates..."
        ensure_gradlew
        ./gradlew dependencyUpdates --no-daemon
        ;;
    "help"|"--help"|"-h")
        show_help
        ;;
    *)
        echo "❌ Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac