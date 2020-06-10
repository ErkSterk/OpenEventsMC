package me.erksterk.openeventsmc.config;

public class Config {
    //This is a phoenix config file, fields are automatically loaded from config into this variables using reflection. For now there is no way to define nonstatic instances in a config but these are planned to be implemented at a later time
    public static int configVersion=1;
    public static boolean waterdrop_enabled = false;
    public static String waterdrop_region = "waterdrop";
    public static String waterdrop_death_location = "world_0_0_0";
    public static String waterdrop_drop_location = "world_0_0_0";
    public static String waterdrop_wait_location = "world_0_0_0";
    public static String waterdrop_water_region = "waterdrop_water";
    public static String waterdrop_waiting_region = "waterdrop_waiting";
    public static String waterdrop_dead_region = "waterdrop_dead";
    public static int waterdrop_warn_before_drop_amount = 10;
    public static int waterdrop_elimination_after_drop = 10;
}
