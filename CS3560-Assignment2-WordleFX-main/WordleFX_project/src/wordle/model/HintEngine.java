package wordle.model;

import java.util.*;
import java.util.stream.Collectors;

// HintEngine Class - Generates hints and filters possible words based on player feedback
public class HintEngine {

    // Filter out candidate words that do not match previous guesses and feedback
    public static List<String> filterCandidates(List<String> candidates, List<String> guesses, List<Feedback[]> fbs) {
        List<String> filtered = new ArrayList<>(candidates);
        for (int gi = 0; gi < guesses.size(); gi++) {
            String g = guesses.get(gi);
            Feedback[] fb = fbs.get(gi);
            filtered = filtered.stream()
                    .filter(w -> compatible(w, g, fb))
                    .collect(Collectors.toList());
        }
        return filtered;
    }

    // Check if a word is compatible with the feedback from a previous guess
    private static boolean compatible(String w, String g, Feedback[] fb) {
        int L = g.length();

        // Check green letters - must match exactly
        for (int i = 0; i < L; i++)
            if (fb[i] == Feedback.GREEN && w.charAt(i) != g.charAt(i))
                return false;

        // Check yellow letters - must exist elsewhere in the word
        Map<Character, Integer> need = new HashMap<>();
        for (int i = 0; i < L; i++)
            if (fb[i] == Feedback.YELLOW) {
                if (w.charAt(i) == g.charAt(i)) return false;
                need.put(g.charAt(i), need.getOrDefault(g.charAt(i), 0) + 1);
            }

        // Verify yellow letters appear enough times
        for (var e : need.entrySet()) {
            int have = 0;
            for (char c : w.toCharArray())
                if (c == e.getKey()) have++;
            if (have < e.getValue()) return false;
        }

        // Check gray letters - should not appear unless already confirmed elsewhere
        for (int i = 0; i < L; i++)
            if (fb[i] == Feedback.GRAY) {
                char c = g.charAt(i);
                boolean elsewhere = false;
                for (int j = 0; j < L; j++)
                    if (j != i && (fb[j] == Feedback.GREEN || fb[j] == Feedback.YELLOW) && g.charAt(j) == c) {
                        elsewhere = true;
                        break;
                    }
                if (!elsewhere && w.indexOf(c) >= 0)
                    return false;
            }

        return true;
    }

    // Choose the best hint word from the remaining candidates
    public static String bestHint(List<String> candidates) {
        if (candidates.isEmpty()) return "(no hint)";

        int L = GameState.WORD_LEN;
        List<Map<Character, Integer>> pos = new ArrayList<>();
        for (int i = 0; i < L; i++) pos.add(new HashMap<>());

        // Count letter frequency by position
        for (String w : candidates)
            for (int i = 0; i < L; i++) {
                char c = w.charAt(i);
                pos.get(i).put(c, pos.get(i).getOrDefault(c, 0) + 1);
            }

        // Pick the candidate with the highest score based on frequency and uniqueness
        String best = candidates.get(0);
        int bestScore = -1;
        for (String w : candidates) {
            int score = 0;
            Set<Character> uniq = new HashSet<>();
            for (int i = 0; i < L; i++) {
                char c = w.charAt(i);
                score += pos.get(i).getOrDefault(c, 0);
                if (uniq.add(c)) score += 2; // reward unique letters
            }
            if (score > bestScore) {
                bestScore = score;
                best = w;
            }
        }

        return best.toUpperCase();
    }
}
