#!/bin/bash

SRC_DIR="src"
TEST_DIR="test"
LIB_DIR="lib"
OUT_DIR="output"
JAVA_FILES="*.java"
JAVA_CLASS="*.class"
TMP_SOURCES_FILE="tmp.txt"
LIB="junit-platform-console-standalone-1.10.0.jar"

function build() {
    echo "Compiling..."

    mkdir -p "$OUT_DIR"

    find "$SRC_DIR" -name $JAVA_FILES > "$TMP_SOURCES_FILE"

    javac -d "$OUT_DIR" @$TMP_SOURCES_FILE

    rm -f "$TMP_SOURCES_FILE"

    echo "Build complete."
}

function clean() {
    echo "Cleaning..."

    rm -rf "$OUT_DIR"
    find . -name $JAVA_CLASS -delete

    echo "Clean complete."
}

function run() {
    if [ -z "$2" ]; then
        echo "Usage: ./project.sh run <inputfile>"
        exit 1
    fi

    INPUT_FILE="$2"

    if [ ! -f "$INPUT_FILE" ]; then
        echo "Error: File '$INPUT_FILE' does not exist."
        return 1
    fi

    echo "Running parser for file: $2"

    java -cp "$OUT_DIR" FileParser "$2"
}

function test() {
    echo "Running tests..."

    JUNIT_JAR=$LIB_DIR/$LIB

    mkdir -p $LIB_DIR

    if [ ! -f "$JUNIT_JAR" ]; then
        echo "Downloading JUnit..."
        curl -L -o "$JUNIT_JAR" https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar
    fi

    mkdir -p $OUT_DIR
    find $SRC_DIR $TEST_DIR -name $JAVA_FILES > $TMP_SOURCES_FILE
    javac -cp "$JUNIT_JAR;$OUT_DIR" -d "$OUT_DIR" @$TMP_SOURCES_FILE

    cd "$OUT_DIR"
    java -jar ../"$JUNIT_JAR" --class-path . --scan-classpath
    cd ..
    
    echo "Cleaning up..."
    rm -f "$JUNIT_JAR"
    rm -rf $OUT_DIR
    rm -f $TMP_SOURCES_FILE
}

case "$1" in
    build)
        build
        ;;
    clean)
        clean
        ;;
    run)
        run "$@"
        ;;
    test)
        test
        ;;
    *)
        echo "Usage:"
        echo "  ./fileParser build       # Compile source files"
        echo "  ./fileParser clean       # Remove compiled files"
        echo "  ./fileParser test        # Run tests"
        echo "  ./fileParser <file.env>  # Execute parser"
        exit 1
        ;;
esac