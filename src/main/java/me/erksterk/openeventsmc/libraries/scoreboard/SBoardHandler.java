package me.erksterk.openeventsmc.libraries.scoreboard;

import org.bukkit.entity.Player;

public class SBoardHandler {

    //I need to find a proper name for the scoreboard library, this will be a github repo such as clicktunnel at some point.
    public static void sendSBoard(SBoard sboard, Player p){
        p.setScoreboard(sboard.sidebar);
    }
}
