package me.erksterk.openeventsmc.libraries.scoreboard;

import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public class SBoard {

    public Scoreboard sidebar;

    public SBoard(List<String> r, HashMap<String, String> pholder) {
        int pos = 0;
        sidebar = null;
        sidebar = Bukkit.getScoreboardManager().getNewScoreboard(); // sidebar is the name of the scoreboard
        Objective objective = sidebar.registerNewObjective("test", "dummy"); // Just supposed to be, doesn't actually add anything
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', MessageUtils.translateMessage(Config.oitc_scoreboard_title, new HashMap<>()))); // The name that shows up on the top of the sidebar
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // setting it as sidebar


        for (String s : r) {
            try {
                String score = s;
                if (score != null) {

                    for (String key : pholder.keySet()) {
                        if (score != null) {
                            String val = pholder.get(key);
                            if (key == null) {
                                score = null;
                            } else {

                                if (score.contains(key)) {
                                    if (val == null) {
                                        score = null;
                                    } else {
                                        score = score.replace(key, val);
                                    }
                                }
                            }
                        }
                    }
                    if (score != null) {
                        score = ChatColor.translateAlternateColorCodes('&', score);
                        objective.getScore(score).setScore(pos); //sets the score on the scoreboard to the string.
                        pos++;
                    }

                }
            } catch (NumberFormatException ex) {
                System.out.println("Config error for scoreboard!");
                ex.printStackTrace();
                break;
            }
        }
    }
}
