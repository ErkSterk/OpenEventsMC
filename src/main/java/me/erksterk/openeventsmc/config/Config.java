package me.erksterk.openeventsmc.config;

import java.util.Arrays;
import java.util.List;

public class Config {
    //This is a phoenix config file, fields are automatically loaded from config into this variables using reflection. For now there is no way to define nonstatic instances in a config but these are planned to be implemented at a later time
    public static int configVersion=1;

    public static boolean waterdrop_announce_to_all_partaking = true;
    public static boolean waterdrop_announce_to_all_online = false;
    public static int waterdrop_warn_before_drop_amount = 10;
    public static int waterdrop_elimination_after_drop = 10;
    public static List<String> oitc_scoreboard = Arrays.asList("%player1% : %p1stat%","%player2% : %p2stat%","%player3% : %p3stat%","%player4% : %p4stat%","%player5% : %p5stat%", "Stats : %pstats%");
}
