package me.erksterk.openeventsmc.config;

import java.util.Arrays;
import java.util.List;

public class Language {
    //This is a phoenix config file, fields are automatically loaded from config into this variables using reflection. For now there is no way to define nonstatic instances in a config but these are planned to be implemented at a later time
    public static String TAG="&cOpenEventsMC &9>";
    public static String Waterdrop_drop_countdown = "%tag% &aDropping in &c%time%!";
    public static String Waterdrop_dropping = "%tag% &cDropping!";
    public static String Waterdrop_nowinner = "%tag% &cNobody won the event!";
    public static String Waterdrop_winner = "%tag% &a%player% won the event!";
    public static String Waterdrop_eliminated = "%tag% &c%player% was eliminated!";
    public static String Waterdrop_eliminate_countdown = "%tag% &cEliminating in %time%!";
    public static String Waterdrop_dropped_success = "%tag% &aSuccessfully dropped!";
    public static String Oitc_Add_Arrow = "%tag% &a+1 &cArrow!";
    public static String Oitc_Time_Out = "%tag% &cTime ran out, &a%player% had the highest score!";
    public static String Oitc_Kill_Win = "%tag% &c%player% was the fastest to reach the required kills!";
    public static String Oitc_Kill_Player = "%tag% &c%killed% was killed by %killer%";
    public static List<String> Event_host_announce = Arrays.asList("&a%player% is hosting a &b%type% with the name %name%", "&7join by doing &a/events join %name%");
    public static String Event_join= "%tag% &aJoined event!";
    public static String Redrover_run_red = "%tag% &cRun to Red!";
    public static String Redrover_run_blue = "%tag% &bRun to Blue!";
    public static String Redrover_peace = "%tag% &7%time% Seconds of waiting";
    public static String Redrover_peace_over = "%tag% Peace time is over";
    public static String Redrover_killed  = "%tag% &c%killed% was eliminated by %killer%";
    public static String Redrover_nowinner = "%tag% &cAll players are dead, nobody won the event!";
    public static String Redrover_winner = "%tag% &a%player% won the event!";
    public static String Redrover_eliminated="%tag% &c%killed% was eliminated!";
}
