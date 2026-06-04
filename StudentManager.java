import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * StudentManager.java
 * Manages the in-memory ArrayList of Student records.
 * Provides CRUD operations: Add, View, Update, Delete.
 */
public class StudentManager {

    private final ArrayList<Student> students = new ArrayList<>();
    private int nextId = 1; // auto-increment ID counter

    // ══════════════════════════════════════════════════════════════
    //  CREATE
    // ══════════════════════════════════════════════════════════════

    /**
     * Adds a new student with an auto-generated ID.
     * @return the newly created Student
     */
    public Student addStudent(String name, double marks) {
        Student s = new Student(nextId++, name, marks);
        students.add(s);
        return s;
    }

    // ══════════════════════════════════════════════════════════════
    //  READ
    // ══════════════════════════════════════════════════════════════

    /** Returns an unmodifiable view of all students. */
    public List<Student> getAllStudents() {
        return students;
    }

    /** Returns an Optional containing the student with the given ID, or empty. */
    public Optional<Student> findById(int id) {
        return students.stream()
                       .filter(s -> s.getId() == id)
                       .findFirst();
    }

    /** Returns true if there are no records. */
    public boolean isEmpty() {
        return students.isEmpty();
    }

    /** Returns a list sorted by marks descending (top performers first). */
    public List<Student> getRankedStudents() {
        List<Student> ranked = new ArrayList<>(students);
        ranked.sort(Comparator.comparingDouble(Student::getMarks).reversed());
        return ranked;
    }

    // ══════════════════════════════════════════════════════════════
    //  UPDATE
    // ══════════════════════════════════════════════════════════════

    /**
     * Updates the name and/or marks of the student with the given ID.
     * Pass null for name or -1 for marks to skip updating that field.
     * @return true if updated, false if ID not found
     */
    public boolean updateStudent(int id, String newName, double newMarks) {
        Optional<Student> opt = findById(id);
        if (opt.isEmpty()) return false;

        Student s = opt.get();
        if (newName != null && !newName.isBlank()) s.setName(newName);
        if (newMarks >= 0)                         s.setMarks(newMarks);
        return true;
    }

    // ══════════════════════════════════════════════════════════════
    //  DELETE
    // ══════════════════════════════════════════════════════════════

    /**
     * Removes the student with the given ID.
     * @return true if removed, false if not found
     */
    public boolean deleteStudent(int id) {
        return students.removeIf(s -> s.getId() == id);
    }

    /** Removes all students and resets the ID counter. */
    public void deleteAll() {
        students.clear();
        nextId = 1;
    }

    // ══════════════════════════════════════════════════════════════
    //  STATISTICS
    // ══════════════════════════════════════════════════════════════

    public double getAverage() {
        return students.stream()
                       .mapToDouble(Student::getMarks)
                       .average()
                       .orElse(0);
    }

    public Optional<Student> getTopStudent() {
        return students.stream()
                       .max(Comparator.comparingDouble(Student::getMarks));
    }

    public Optional<Student> getLowestStudent() {
        return students.stream()
                       .min(Comparator.comparingDouble(Student::getMarks));
    }

    public int getTotalStudents() {
        return students.size();
    }
}
