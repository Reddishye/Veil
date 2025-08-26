@echo off
REM Veil Development Helper Script for Windows
REM This script provides common development tasks for the Veil project

setlocal

if "%1"=="" goto :help
if "%1"=="help" goto :help
if "%1"=="--help" goto :help
if "%1"=="-h" goto :help

if "%1"=="build" goto :build
if "%1"=="build-forge" goto :build-forge
if "%1"=="build-fabric" goto :build-fabric
if "%1"=="build-neoforge" goto :build-neoforge
if "%1"=="build-common" goto :build-common
if "%1"=="test" goto :test
if "%1"=="clean" goto :clean
if "%1"=="publish-local" goto :publish-local
if "%1"=="check-deps" goto :check-deps

echo ❌ Unknown command: %1
echo.
goto :help

:help
echo Veil Development Helper
echo Usage: %0 [command]
echo.
echo Commands:
echo   build          - Build all platforms
echo   build-forge    - Build only Forge
echo   build-fabric   - Build only Fabric
echo   build-neoforge - Build only NeoForge
echo   build-common   - Build only Common
echo   test           - Run all tests
echo   clean          - Clean build artifacts
echo   publish-local  - Publish to local Maven repository
echo   check-deps     - Check for dependency updates
echo   help           - Show this help message
echo.
echo Examples:
echo   %0 build                 # Build all platforms
echo   %0 build-forge          # Build only Forge version
echo   %0 test                 # Run test suite
goto :end

:build
echo 🔨 Building all platforms...
call gradlew.bat clean build --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Build complete! Check */build/libs/ for artifacts.
) else (
    echo ❌ Build failed!
    exit /b %ERRORLEVEL%
)
goto :end

:build-forge
echo 🔨 Building Forge...
call gradlew.bat :forge:clean :forge:build --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Forge build complete!
) else (
    echo ❌ Forge build failed!
    exit /b %ERRORLEVEL%
)
goto :end

:build-fabric
echo 🔨 Building Fabric...
call gradlew.bat :fabric:clean :fabric:build --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Fabric build complete!
) else (
    echo ❌ Fabric build failed!
    exit /b %ERRORLEVEL%
)
goto :end

:build-neoforge
echo 🔨 Building NeoForge...
call gradlew.bat :neoforge:clean :neoforge:build --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ NeoForge build complete!
) else (
    echo ❌ NeoForge build failed!
    exit /b %ERRORLEVEL%
)
goto :end

:build-common
echo 🔨 Building Common...
call gradlew.bat :common:clean :common:build --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Common build complete!
) else (
    echo ❌ Common build failed!
    exit /b %ERRORLEVEL%
)
goto :end

:test
echo 🧪 Running tests...
call gradlew.bat test --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Tests complete!
) else (
    echo ❌ Tests failed!
    exit /b %ERRORLEVEL%
)
goto :end

:clean
echo 🧹 Cleaning build artifacts...
call gradlew.bat clean --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Clean complete!
) else (
    echo ❌ Clean failed!
    exit /b %ERRORLEVEL%
)
goto :end

:publish-local
echo 📦 Publishing to local Maven repository...
call gradlew.bat publishToMavenLocal --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo ✅ Published to local Maven repository!
) else (
    echo ❌ Publish failed!
    exit /b %ERRORLEVEL%
)
goto :end

:check-deps
echo 🔍 Checking for dependency updates...
call gradlew.bat dependencyUpdates --no-daemon
goto :end

:end
endlocal