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
    public static String Command_No_Permission = "%tag% &cNo Permission!";
    public static String Command_not_found = "%tag% &cUnknown command!";
    public static String Command_host_event_already = "%tag% &cThis event is already running!";
    public static String Command_event_not_found = "%tag% &cCould not find event!";
    public static String Command_pause_name_missing = "%tag% &cType the name of the event you wish to toggle!";
    public static String Command_pause_paused = "%tag% &cEvent paused!";
    public static String Command_pause_started = "%tag% &aEvent started!";
    public static String Command_join_not_running = "%tag% &cThis event is not running currently!";
    public static String Command_join_event_not_exists = "%tag% &cThis event does not exist!";
    public static String Command_join_missing_event = "%tag% &cPlease specify the event you wish to join!";
    public static String Command_host_event_event_missing = "%tag% &cPlease specify the event you wish to host!";
    public static String Command_create_missing_name = "%tag% &cYou need to specify the eventname and type";
    public static String Command_create_created="%tag% &aEvent created, /events setup to config it!";
    public static String Command_create_already_created="%tag% &cAn Event with this name already exists!";
    public static String Command_create_invalid_eventtype="%tag% &cInvalid EventType!";
    public static String Command_create_missing_eventtype="%tag% You need to specify eventtype";
    public static String Command_setup_done="%tag% &aNothing more to configure!";
    public static String Command_setup_missing_field="%tag% &cYou need to select a field!";
    public static String Command_setup_set_success="%tag% &aSuccessfully set field!";
    public static String Command_setup_set_invalid="%tag% &cInvalid field!";
    public static String Command_setup_set_illegal="%tag% &cIllegal field!";
    public static String Command_setup_set_field_not_needed="%tag% &cThis field is not needed for this eventtype!";
    public static String Command_setup_entered="%tag% &aEntered setup mode!";
    public static String Command_setup_missing_eventname="%tag% &cMissing eventname!";
    public static String Command_setup_set_missing_value="%tag% &cMissing value!";
    public static String LastManStanding_nowinner="%tag% &cAll participants are dead, nobody won the event!";
    public static String LastManStanding_winner="%tag% &a%player% has won the event!";
    public static String LastManStanding_killed="%tag% &c%killed% was killed by %killer%";
    public static String LastManStanding_eliminated="%tag% %killed% was eliminated!";
}
