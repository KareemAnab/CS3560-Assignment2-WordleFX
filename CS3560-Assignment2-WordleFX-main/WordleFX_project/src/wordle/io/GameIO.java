
package wordle.io; import com.google.gson.Gson; import wordle.model.*; import java.io.IOException; import java.nio.file.*; import java.util.*;
public class GameIO {
    public static class SaveData { public String secret; public List<String> guesses=new ArrayList<>(); public List<Feedback[]> feedback=new ArrayList<>(); public int turn; public boolean hardMode; public String statsJson; }
    public static void save(GameState s, Stats stats, Path path) throws IOException { SaveData d=new SaveData(); d.secret=s.getSecret(); d.guesses=s.getGuesses(); d.feedback=s.getFeedbacks(); d.turn=s.getTurn(); d.hardMode=s.isHardMode(); d.statsJson=stats.toJson(); Files.writeString(path, new Gson().toJson(d)); }
    public static void loadInto(GameState s, Stats stats, Path path) throws IOException { SaveData d=new Gson().fromJson(Files.readString(path), SaveData.class);
        s.reset(d.secret); s.getGuesses().addAll(d.guesses); s.getFeedbacks().addAll(d.feedback); s.setTurn(d.turn); s.setHardMode(d.hardMode);
        Stats t=Stats.fromJson(d.statsJson); stats.played=t.played; stats.wins=t.wins; stats.currentStreak=t.currentStreak; stats.bestStreak=t.bestStreak; stats.dist=t.dist; }
}
