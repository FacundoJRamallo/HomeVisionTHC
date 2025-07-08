# HomeVisionTHC
HomeVision Take-home Challenge for Silver.dev

# FileParser

**FileParser** is a Java utility that analyzes a proprietary `.env` archival file format, extracts embedded files (such as `.jpg`, `.xml`, `.txt`, etc.), and saves them into the `./output` directory.

The `.env` file is a binary container with custom delimiters marking each metadata and content section. This parser was built using reverse engineering techniques.

---

## Project Structure

- `FileParser.java`: The entry point. Validates input, handles errors, and coordinates the file parsing process.
- `Utils.java`: Contains core logic for parsing, interpreting headers, and saving extracted files.
- `Constants.java`: Stores byte-level markers and format-specific constants for parsing.

---

## How to Compile

Open a terminal in the root project directory and run:

```bash
javac FileParser.java Utils.java Constants.java
```

---

## How to Run

Open a terminal in the root project directory and run:

```bash
java FileParser sample.env
```