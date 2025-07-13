# HomeVisionTHC
HomeVision Take-home Challenge for Silver.dev

# FileParser

**FileParser** is a Java command-line tool designed to parse a proprietary binary `.env` file format.  
It extracts embedded files (such as `.jpg`, `.xml`, `.txt`, etc.) and saves them into the `./output` directory.

The `.env` file is a custom container with byte-level headers that mark each file’s metadata and content block.  
This parser was developed using **reverse engineering** techniques based on structured patterns.

---

## What This Project Does

This project provides a utility for **unpacking binary archives** that contain multiple embedded files, commonly used in:
- Internal data interchange formats
- Backups or configuration bundles
- Exported data dumps with metadata-annotated files

The parser:
- Reads and decodes structured sections marked by binary headers
- Extracts the original filenames and file extensions
- Writes each file to the local `./output/` directory

---

## Example

If a file called `sample.env` contains six embedded files:

- `homer-simpson.jpg`
- `0-INC2.xml`
- `1004UADMISMOUAD2.6GSE.xml`
- `1-REO2.xml`
- `2-1004UAD.xml`
- `content.txt`

Then the parser will generate:

```
output/
│   homer-simpson.jpg
│   0-INC2.xml
│   1004UADMISMOUAD2.6GSE.xml
│   1-REO2.xml
|   2-1004UAD.xml
|   content.txt
```

These files are reconstructed exactly as they were embedded, with correct extensions and filenames.

---

## Project Structure

- `FileParser.java` – Main entry point: handles argument validation and orchestration.
- `Utils.java` – Core logic for parsing byte headers, extracting metadata, and saving files.
- `Constants.java` – Byte markers, file structure constants, and format configuration.
- `exceptions/` – Custom exception classes for structured error handling.
- `utils/ErrorMessageEnum.java` – Centralized, consistent error message definitions.
- `fileParser.sh` – Shell script to build, run, and clean the project.

---

## How to Use

Clone the repository and open a terminal in the root directory.

### Commands

- Build the project:

```bash
./fileParser.sh build
```

- Run the parser:

```bash
./fileParser.sh run sample.env
```

- Clean all outputs:

```bash
./fileParser.sh clean
```

## Live Demo

This is a terminal-based project and does not include a web interface or hosted demo.
However, it can be tested locally in seconds using a `sample.env` file:

```bash
./fileParser.sh run test/resources/sample.env
```

## Requirements

- Java 17+
- Unix-like environment with bash and find (Linux/macOS or WSL)