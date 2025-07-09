#!/bin/bash

function build() {
    javac FileParser.java Utils.java Constants.java
    echo "Build completed."
}

function clean() {
    rm -rf *.class output/
    echo "Clean completed."
}

function run() {
    if [ -z "$2" ]; then
        echo "Usage: ./project.sh run <inputfile>"
        exit 1
    fi
    java FileParser "$2"
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
    *)
        echo "Usage: ./project.sh {build|clean|run <inputfile>}"
        exit 1
        ;;
esac