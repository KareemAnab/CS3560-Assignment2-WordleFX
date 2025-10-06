package wordle.model;

// Import Libraries
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// Dictionary Class - Handles loading, validating, and choosing words for the game
public class Dictionary {
    // Set of all valid words the player can guess
    private final Set<String> valid = new HashSet<>();
    // List of all possible answer words
    private final List<String> answers = new ArrayList<>();
    // Random number generator for picking random answers
    private final Random rand = new Random();
    // Whether to allow any 5-letter words
    private final boolean permissive;

    // Default constructor
    public Dictionary() {
        this(false);
    }

    // Constructor with permissive option
    public Dictionary(boolean permissive) {
        this.permissive = permissive;
        loadAllLists(); // Load all word lists from files
        valid.addAll(answers); // Make sure all answers are also valid words
    }

    // Load all valid and answer word lists
    private void loadAllLists() {
        String dir = "assets/words";
        List<String> classpathFiles = listClasspathFiles(dir);

        // Load from resources if available
        if (!classpathFiles.isEmpty()) {
            for (String path : classpathFiles) loadOne(path);
        }
        // Otherwise, load from local directory
        else {
            try {
                Path root = Paths.get(dir);
                if (Files.isDirectory(root)) {
                    try (var stream = Files.list(root)) {
                        for (Path p : stream.collect(Collectors.toList())) {
                            String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
                            if (name.startsWith("valid") || name.startsWith("answers")) {
                                loadOne(p.toString());
                            }
                        }
                    }
                }
            } catch (IOException ignored) {}
        }
    }

    // List of potential word list file paths to load
    private List<String> listClasspathFiles(String root) {
        return List.of(
                root + "/valid.txt",
                root + "/answers.txt",
                root + "/valid-allowed1.txt",
                root + "/valid-allowed2.txt",
                root + "/answers-extra.txt"
        );
    }

    // Load one file and add its contents to valid or answers lists
    private void loadOne(String path) {
        boolean toValid = path.toLowerCase(Locale.ROOT).contains("valid");
        boolean toAnswers = path.toLowerCase(Locale.ROOT).contains("answers");

        // Try loading from classpath first
        try (InputStream in = Dictionary.class.getClassLoader().getResourceAsStream(path)) {
            if (in != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                    String line;
                    while ((line = br.readLine()) != null) addWord(line, toValid, toAnswers);
                }
                return;
            }
        } catch (IOException ignored) {}

        // Fallback to local filesystem if not found in classpath
        try {
            Path p = Paths.get(path);
            if (Files.exists(p)) {
                for (String line : Files.readAllLines(p)) addWord(line, toValid, toAnswers);
            }
        } catch (IOException ignored) {}
    }

    // Add a single word to the correct lists if it's valid (A–Z only and 5 letters)
    private void addWord(String line, boolean toValid, boolean toAnswers) {
        String s = line.trim().toUpperCase(Locale.ROOT);
        if (s.length() == 5 && s.matches("[A-Z]{5}")) {
            if (toValid) valid.add(s);
            if (toAnswers) answers.add(s);
        }
    }

    // Check if a word is valid
    public boolean isValidWord(String w) {
        if (w == null) return false;
        String s = w.toUpperCase(Locale.ROOT);
        if (valid.contains(s)) return true;
        return permissive && s.length() == 5 && s.matches("[A-Z]{5}");
    }

    // Get a random answer word from the list
    public String randomAnswer() {
        if (answers.isEmpty()) return "CRANE"; // fallback if list is empty
        return answers.get(rand.nextInt(answers.size()));
    }

    // Return an unmodifiable list of all possible answer candidates
    public List<String> allCandidates() {
        return answers.isEmpty()
                ? List.of("CRANE")
                : Collections.unmodifiableList(answers);
    }
}
