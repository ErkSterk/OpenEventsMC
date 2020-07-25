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
    public static List<String> oitc_scoreboard = Arrays.asList("&a%player1% &7: &c%p1stat%","&a%player2% &7: &c%p2stat%","&a%player3% &7: &c%p3stat%","&a%player4% &7: &c%p4stat%","&a%player5% &7: &c%p5stat%", "&7Stats : &c%pstats%");
    public static boolean oitc_announce_to_all_partaking = true;
    public static boolean oitc_announce_to_all_online = false;
    public static boolean oitc_scoreboard_enabled = true;
    public static String oitc_scoreboard_title = "&cOne In the chamber";
    public static boolean redrover_announce_to_all_partaking=true;
    public static boolean redrover_announce_to_all_online=false;
    public static boolean lms_announce_to_all_partaking=true;
    public static boolean lms_announce_to_all_online=false;
}
