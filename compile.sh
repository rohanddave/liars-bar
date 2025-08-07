#!/bin/bash

echo "🔨 Compiling Liar's Bar Multiplayer Game..."

# Create output directory
mkdir -p build/classes

# Download dependencies (basic approach - in production use Maven)
if [ ! -d "lib" ]; then
    mkdir -p lib
    echo "📦 Note: You need to manually download the following dependencies to lib/:"
    echo "  - Java-WebSocket-1.5.6.jar"
    echo "  - jackson-databind-2.17.0.jar"
    echo "  - jackson-core-2.17.0.jar"
    echo "  - jackson-annotations-2.17.0.jar"
    echo "  - slf4j-api-2.0.12.jar"
    echo "  - logback-classic-1.5.3.jar"
    echo "  - logback-core-1.5.3.jar"
    echo ""
    echo "For now, compiling without external dependencies (may have errors)..."
fi

# Set classpath
CLASSPATH="src:build/classes"
if [ -d "lib" ]; then
    for jar in lib/*.jar; do
        if [ -f "$jar" ]; then
            CLASSPATH="$CLASSPATH:$jar"
        fi
    done
fi

# Find all Java files
echo "🔍 Finding Java source files..."
find src -name "*.java" > sources.txt

# Compile
echo "⚙️  Compiling Java sources..."
javac -cp "$CLASSPATH" -d build/classes @sources.txt

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "🚀 To run the WebSocket server:"
    echo "   java -cp \"$CLASSPATH:build/classes\" server.GameServer"
    echo ""
    echo "🎮 To run console mode:"
    echo "   java -cp \"$CLASSPATH:build/classes\" Main"
    echo ""
    echo "🌐 To test with web client:"
    echo "   1. Start the WebSocket server"
    echo "   2. Open client/index.html in your web browser"
else
    echo "❌ Compilation failed!"
    echo "Note: You may need to install Maven and run 'mvn compile' instead"
    echo "Or manually download the required JAR dependencies to the lib/ directory"
fi

# Cleanup
rm -f sources.txt