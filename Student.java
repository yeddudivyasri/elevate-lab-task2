/**
 * Student.java
 * Represents a single student record with ID, name, and marks.
 */
public class Student {

    private int id;
    private String name;
    private double marks;
    private String grade;

    // Constructor
    public Student(int id, String name, double marks) {
        this.id = id;
        this.name = name;
        setMarks(marks); // uses setter to auto-compute grade
    }

    // ── Getters ──────────────────────────────────────────────────
    public int getId()       { return id; }
    public String getName()  { return name; }
    public double getMarks() { return marks; }
    public String getGrade() { return grade; }

    // ── Setters ──────────────────────────────────────────────────
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setMarks(double marks) {
        if (marks < 0 || marks > 100)
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        this.marks = marks;
        this.grade = computeGrade(marks);
    }

    // ── Grade logic ───────────────────────────────────────────────
    private String computeGrade(double m) {
        if (m >= 90) return "A+";
        if (m >= 80) return "A";
        if (m >= 70) return "B";
        if (m >= 60) return "C";
        if (m >= 50) return "D";
        return "F";
    }

    // ── Display helper ────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("| %-6d | %-20s | %6.2f | %-5s |",
                id, name, marks, grade);
    }
}
