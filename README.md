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
- `project.sh`: Bash script to **build**, **run**, and **clean** the project.

---

## How to Use

Open a terminal in the root project directory

### Available commands

- Build:

```bash
./project.sh build
```

- Run the parser:

```bash
./project.sh run sample.env
```

- Clean:

```bash
./project.sh clean
```

## Output

After running the parser, all extracted files are saved in the ./output/ folder.

### Example

Given `sample.env` contains the following files:

- homer-simpson.jpg
- 0-INC2.xml
- 1004UADMISMOUAD2.6GSE.xml
- 1-REO2.xml
- 2-1004UAD.xml
- content.txt

The output structure would be:

```
output/
│   homer-simpson.jpg
│   0-INC2.xml
│   1004UADMISMOUAD2.6GSE.xml
│   1-REO2.xml
|   2-1004UAD.xml
|   content.txt
```

