# Student Record Management System
A CLI-based CRUD application in Java for managing student records.

## Project Structure
```
StudentRecordSystem/
├── src/
│   ├── Student.java         # Data model: id, name, marks, grade
│   ├── StudentManager.java  # Business logic: ArrayList CRUD + stats
│   └── Main.java            # CLI menu & user interaction
├── out/                     # Compiled .class files (after build)
└── README.md
```

## How to Compile & Run
### Using Terminal
```bash
# 1. Compile all source files
javac -d out src/*.java

# 2. Run the program
java -cp out Main
```
### Using VS Code
1. Open the `StudentRecordSystem` folder
2. Install the **Extension Pack for Java** (Microsoft)
3. Open `Main.java` → click **Run** above `main()`

### Using IntelliJ IDEA CE
1. File → Open → select `StudentRecordSystem`
2. Right-click `Main.java` → Run 'Main.main()'

## Features
| # | Menu Option          | Description                                 |
|---|----------------------|---------------------------------------------|
| 1 | Add Student          | Enter name + marks, auto-assigns ID & grade |
| 2 | View All Students    | Table with ID, Name, Marks, Grade           |
| 3 | Search by ID         | Find a specific student                     |
| 4 | Update Student       | Change name and/or marks (blank = keep)     |
| 5 | Delete Student       | Remove one record by ID                     |
| 6 | Rankings & Stats     | Class average, top/lowest, ranked table     |
| 7 | Delete All Records   | Wipe everything (with confirmation)         |
| 0 | Exit                 | Quit the program                            |

## Grade Scale
| Marks   | Grade |
|---------|-------|
| 90–100  | A+    |
| 80–89   | A     |
| 70–79   | B     |
| 60–69   | C     |
| 50–59   | D     |
| 0–49    | F     |

## Concepts Practiced
- OOP: Classes, constructors, encapsulation, getters/setters
- `ArrayList<Student>` for dynamic storage
- Java Streams (filter, min, max, average)
- Input validation and exception handling
- CLI UI with ANSI colors
