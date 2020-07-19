package me.erksterk.openeventsmc.config;

public class Config {
    //This is a phoenix config file, fields are automatically loaded from config into this variables using reflection. For now there is no way to define nonstatic instances in a config but these are planned to be implemented at a later time
    public static int configVersion=1;

    public static boolean waterdrop_announce_to_all_partaking = true;
    public static boolean waterdrop_announce_to_all_online = false;
    public static int waterdrop_warn_before_drop_amount = 10;
    public static int waterdrop_elimination_after_drop = 10;
}
