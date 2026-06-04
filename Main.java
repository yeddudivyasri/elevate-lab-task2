import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main.java
 * Entry point – provides an interactive CLI menu for the
 * Student Record Management System.
 *
 * Menu structure:
 *   1. Add Student
 *   2. View All Students
 *   3. Search Student by ID
 *   4. Update Student
 *   5. Delete Student
 *   6. View Rankings / Stats
 *   7. Delete All Records
 *   0. Exit
 */
public class Main {

    // ── ANSI colour codes (visible in most terminals) ─────────────
    static final String RESET  = "\u001B[0m";
    static final String BOLD   = "\u001B[1m";
    static final String CYAN   = "\u001B[36m";
    static final String GREEN  = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String RED    = "\u001B[31m";
    static final String BLUE   = "\u001B[34m";

    private static final StudentManager manager = new StudentManager();
    private static final Scanner sc = new Scanner(System.in);

    // ═════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ═════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        printBanner();
        seedDemoData(); // load a few sample records so the app feels alive

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> addStudentFlow();
                case 2 -> viewAllStudents();
                case 3 -> searchStudentFlow();
                case 4 -> updateStudentFlow();
                case 5 -> deleteStudentFlow();
                case 6 -> viewStatsFlow();
                case 7 -> deleteAllFlow();
                case 0 -> {
                    printLine(GREEN, "\n  Goodbye! Keep learning. 👋\n");
                    running = false;
                }
                default -> printLine(RED, "  ✗ Invalid option. Please choose 0–7.");
            }
        }
        sc.close();
    }

    // ═════════════════════════════════════════════════════════════
    //  MENU FLOWS
    // ═════════════════════════════════════════════════════════════

    /** 1 – Add a new student */
    static void addStudentFlow() {
        header("ADD STUDENT");
        String name  = readString("  Enter student name  : ");
        double marks = readMarks("  Enter marks (0-100) : ");

        try {
            Student s = manager.addStudent(name, marks);
            printLine(GREEN, "\n  ✓ Student added successfully!");
            printTableHeader();
            System.out.println("  " + s);
            printTableFooter();
        } catch (IllegalArgumentException e) {
            printLine(RED, "  ✗ Error: " + e.getMessage());
        }
    }

    /** 2 – Display all students in a formatted table */
    static void viewAllStudents() {
        header("ALL STUDENTS");
        if (manager.isEmpty()) {
            printLine(YELLOW, "  No records found. Add a student first.");
            return;
        }
        printTableHeader();
        for (Student s : manager.getAllStudents())
            System.out.println("  " + s);
        printTableFooter();
        printLine(CYAN, String.format("  Total: %d student(s)", manager.getTotalStudents()));
    }

    /** 3 – Search by ID */
    static void searchStudentFlow() {
        header("SEARCH STUDENT");
        int id = readInt("  Enter student ID: ");
        Optional<Student> opt = manager.findById(id);

        if (opt.isPresent()) {
            printTableHeader();
            System.out.println("  " + opt.get());
            printTableFooter();
        } else {
            printLine(RED, "  ✗ No student found with ID " + id + ".");
        }
    }

    /** 4 – Update name and/or marks */
    static void updateStudentFlow() {
        header("UPDATE STUDENT");
        if (manager.isEmpty()) { printLine(YELLOW, "  No records to update."); return; }

        viewAllStudents();
        int id = readInt("\n  Enter ID to update: ");

        Optional<Student> opt = manager.findById(id);
        if (opt.isEmpty()) { printLine(RED, "  ✗ ID not found."); return; }

        Student existing = opt.get();
        System.out.printf("  Current name  : %s%n", existing.getName());
        System.out.printf("  Current marks : %.2f%n", existing.getMarks());

        System.out.print("  New name  (Enter to keep) : ");
        String newName = sc.nextLine().trim();

        System.out.print("  New marks (Enter to keep) : ");
        String marksInput = sc.nextLine().trim();
        double newMarks = marksInput.isEmpty() ? -1 : parseDouble(marksInput);

        String nameToSet = newName.isEmpty() ? null : newName;

        try {
            boolean ok = manager.updateStudent(id, nameToSet, newMarks);
            if (ok) printLine(GREEN, "  ✓ Record updated successfully!");
            else    printLine(RED,   "  ✗ Update failed.");
        } catch (IllegalArgumentException e) {
            printLine(RED, "  ✗ Error: " + e.getMessage());
        }
    }

    /** 5 – Delete a single student */
    static void deleteStudentFlow() {
        header("DELETE STUDENT");
        if (manager.isEmpty()) { printLine(YELLOW, "  No records to delete."); return; }

        viewAllStudents();
        int id = readInt("\n  Enter ID to delete: ");

        System.out.print("  " + YELLOW + "Confirm delete ID " + id + "? (y/n): " + RESET);
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            boolean removed = manager.deleteStudent(id);
            if (removed) printLine(GREEN, "  ✓ Student ID " + id + " deleted.");
            else         printLine(RED,   "  ✗ ID not found.");
        } else {
            printLine(CYAN, "  Delete cancelled.");
        }
    }

    /** 6 – Rankings and class statistics */
    static void viewStatsFlow() {
        header("RANKINGS & STATISTICS");
        if (manager.isEmpty()) { printLine(YELLOW, "  No data available."); return; }

        System.out.printf("  %-22s %s%.2f%s%n",
                "Class Average :", BOLD, manager.getAverage(), RESET);

        manager.getTopStudent().ifPresent(t ->
            System.out.printf("  %-22s %s%s (%.2f)%s%n",
                    "Top Student    :", GREEN + BOLD, t.getName(), t.getMarks(), RESET));

        manager.getLowestStudent().ifPresent(l ->
            System.out.printf("  %-22s %s%s (%.2f)%s%n",
                    "Needs Support  :", YELLOW + BOLD, l.getName(), l.getMarks(), RESET));

        System.out.println();
        System.out.println(BOLD + "  RANK  " + RESET + "─".repeat(52));
        List<Student> ranked = manager.getRankedStudents();
        for (int i = 0; i < ranked.size(); i++) {
            String medal = switch (i) {
                case 0 -> "🥇 ";
                case 1 -> "🥈 ";
                case 2 -> "🥉 ";
                default -> "   ";
            };
            System.out.printf("  #%-3d %s%s%n", i + 1, medal, ranked.get(i));
        }
        printTableFooter();
    }

    /** 7 – Delete all records */
    static void deleteAllFlow() {
        header("DELETE ALL RECORDS");
        if (manager.isEmpty()) { printLine(YELLOW, "  No records to delete."); return; }

        printLine(RED, "  ⚠ This will erase ALL " + manager.getTotalStudents() + " record(s)!");
        System.out.print("  Type DELETE to confirm: ");
        String input = sc.nextLine().trim();

        if (input.equals("DELETE")) {
            manager.deleteAll();
            printLine(GREEN, "  ✓ All records deleted. ID counter reset.");
        } else {
            printLine(CYAN, "  Operation cancelled.");
        }
    }

    // ═════════════════════════════════════════════════════════════
    //  UI HELPERS
    // ═════════════════════════════════════════════════════════════

    static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║      STUDENT RECORD MANAGEMENT SYSTEM           ║");
        System.out.println("  ║              Java CLI Edition                   ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    static void printMenu() {
        System.out.println();
        System.out.println(BOLD + BLUE + "  ┌─────────────────────────────┐" + RESET);
        System.out.println(BOLD + BLUE + "  │         MAIN MENU           │" + RESET);
        System.out.println(BOLD + BLUE + "  ├─────────────────────────────┤" + RESET);
        System.out.println(BLUE + "  │  1. Add Student              │" + RESET);
        System.out.println(BLUE + "  │  2. View All Students        │" + RESET);
        System.out.println(BLUE + "  │  3. Search Student by ID     │" + RESET);
        System.out.println(BLUE + "  │  4. Update Student           │" + RESET);
        System.out.println(BLUE + "  │  5. Delete Student           │" + RESET);
        System.out.println(BLUE + "  │  6. Rankings & Statistics    │" + RESET);
        System.out.println(BLUE + "  │  7. Delete All Records       │" + RESET);
        System.out.println(BLUE + "  │  0. Exit                     │" + RESET);
        System.out.println(BOLD + BLUE + "  └─────────────────────────────┘" + RESET);
    }

    static void header(String title) {
        System.out.println();
        System.out.println(BOLD + CYAN + "  ── " + title + " " + "─".repeat(Math.max(0, 40 - title.length())) + RESET);
    }

    static void printTableHeader() {
        System.out.println("  ┌────────┬──────────────────────┬────────┬───────┐");
        System.out.println("  │ ID     │ Name                 │  Marks │ Grade │");
        System.out.println("  ├────────┼──────────────────────┼────────┼───────┤");
    }

    static void printTableFooter() {
        System.out.println("  └────────┴──────────────────────┴────────┴───────┘");
    }

    static void printLine(String color, String msg) {
        System.out.println(color + msg + RESET);
    }

    // ═════════════════════════════════════════════════════════════
    //  INPUT HELPERS
    // ═════════════════════════════════════════════════════════════

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = sc.nextInt();
                sc.nextLine(); // consume newline
                return v;
            } catch (InputMismatchException e) {
                sc.nextLine(); // flush bad input
                printLine(RED, "  ✗ Please enter a whole number.");
            }
        }
    }

    static double readMarks(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = sc.nextDouble();
                sc.nextLine();
                if (v < 0 || v > 100) {
                    printLine(RED, "  ✗ Marks must be between 0 and 100.");
                    continue;
                }
                return v;
            } catch (InputMismatchException e) {
                sc.nextLine();
                printLine(RED, "  ✗ Please enter a valid number.");
            }
        }
    }

    static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            printLine(RED, "  ✗ Input cannot be empty.");
        }
    }

    static double parseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return -1; }
    }

    // ═════════════════════════════════════════════════════════════
    //  DEMO DATA (so the app is not empty on first launch)
    // ═════════════════════════════════════════════════════════════

    static void seedDemoData() {
        manager.addStudent("Arjun Sharma",   87.5);
        manager.addStudent("Priya Reddy",    93.0);
        manager.addStudent("Ravi Kumar",     72.0);
        manager.addStudent("Sneha Patel",    65.5);
        manager.addStudent("Kiran Rao",      45.0);
        printLine(GREEN, "  ✓ 5 demo records loaded.");
    }
}
