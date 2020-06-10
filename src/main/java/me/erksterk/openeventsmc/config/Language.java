package me.erksterk.openeventsmc.config;

public class Language {
    //This is a phoenix config file, fields are automatically loaded from config into this variables using reflection. For now there is no way to define nonstatic instances in a config but these are planned to be implemented at a later time
    public static String TAG="&cOpenEventsMC &9>";
    public static String WATERDROP_PLAYERSUPPOSEDTOBEDEAD="%tag& &cPlayer &b%player% &care supposed to be dead but finished the waterdrop?";
    public static String EVENTS_UNKNOWNEVENT = "%tag% &cUnknown event!";
    public static String PLAYERNOTFOUND = "%tag% &cPlayer not found!";
    public static String EVENTS_KICKED = "%tag% &aSuccessfully kicked &c%player%!";
    public static String EVENTS_KICKED_PLAYER = "%tag% &cYou were kicked from the event!";
    public static String EVENTS_KICKED_ALREADYKICKED = "%tag% &cPlayer %player% is already kicked from the event! did you mean /events join %eventname% %player%?";
    public static String EVENTS_MISSING_ARGUMENTS = "%tag% &cError, missing argument, usage:";
    public static String COMMAND_NO_PERMISSIONS="%tag% &cYou do not have permissions to do this command!";
    public static String EVENTS_WATERDROP_RESET="%tag% &aWaterdrop was reset!";
    public static String EVENTS_REVIVED="%tag% &aPlayer %player% was successfully revived!";
    public static String EVENTS_REVIVED_PLAYER="%tag% &aYou got revived!";
    public static String EVENTS_REVIVED_ALREADYALIVE="%tag% &cPlayer %player% is already alive!";
    public static String EVENTS_LEAVE="%tag% &aYou left the event!";
    public static String EVENTS_LEAVE_ALREADYELEAVE="%tag% &cYou are not inside the event, did you mean /events join <event> <playername>?";
    public static String EVENTS_JOIN="%tag% &aSuccessfully joined event!";
    public static String EVENTS_JOIN_ALREADYJOIN="%tag% &cAlready joined, did you mean to revive?";
    public static String EVENTS_WATERDROP_ANNOUNCEKILL="%tag% &cEliminating all players that didnt finish in &b%warn% &cseconds!";
    public static String EVENTS_WATERDROP_ANNOUNCEDROP="%tag% &cDropping in &b%warn% &cseconds!";
    public static String EVENTS_WINNER="%tag% &c&l%player% &a&lHas won the Event!";
}
