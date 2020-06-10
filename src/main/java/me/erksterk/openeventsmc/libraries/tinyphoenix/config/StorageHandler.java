//author: ErkSterk
package me.erksterk.openeventsmc.libraries.tinyphoenix.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class StorageHandler {

    //We all love some autistic config methods right?
    //Keokix_ couldnt be bothered learning config files properly, so I made some cancer reflection stuff!


    private static void load(String field, Object configObj, File configFile, YamlConfiguration config) {
        if (!config.contains(field)) {
            saveFieldToConfig(field, configObj, config);
        } else {
            loadFieldFromConfig(field, configObj, config);
        }

    }


    public static void loadConfig(String folder, String filename, Object configObject) {
        File configFile = new File(folder, filename + ".yml");

        if (!configFile.exists()) {
            File ff = new File(folder);
            ff.mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create " + filename + ".yml!");
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (Field f : configObject.getClass().getFields()) {
            load(f.getName(), configObject, configFile, config);
        }
        saveConfig(configFile, config, configObject);
    }


    public static void reloadConfig(String folder, String filename, Object configObject) {
        File configFile = new File(folder, filename + ".yml");

        if (!configFile.exists()) {
            File ff = new File(folder);
            ff.mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create " + filename + ".yml!");
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        for (Field f : configObject.getClass().getFields()) {
            load(f.getName(), configObject, configFile, config);
        }
    }


    public static void saveConfig(String folder, String filename, Object configObject) {
        File configFile = new File(folder, filename + ".yml");

        if (!configFile.exists()) {
            File ff = new File(folder);
            ff.mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create " + filename + ".yml!");
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (Field f : configObject.getClass().getFields()) {
            saveFieldToConfig(f.getName(), configObject, config);
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFieldToConfig(String field, Object configObj, YamlConfiguration config) {

        try {
            Field f = configObj.getClass().getField(field);

            Type t = f.getGenericType();
            if (t.toString().equalsIgnoreCase("class java.lang.String")) {
                //STRING
                String s = f.get(configObj).toString();
                config.set(field, s);
            } else if (t.toString().equalsIgnoreCase("boolean")) {
                //BOOLEAN
                boolean b = f.getBoolean(configObj);
                config.set(field, b);
            } else if (t.toString().equalsIgnoreCase("short")) {
                //Short
                int i = f.getShort(configObj);
                config.set(field, i);
            } else if (t.toString().equalsIgnoreCase("int")) {
                //Integer
                int i = f.getInt(configObj);
                config.set(field, i);
            } else if (t.toString().equalsIgnoreCase("double")) {
                //DOUBLE
                double d = f.getDouble(configObj);
                config.set(field, d);
            } else if (t.toString().equalsIgnoreCase("byte")) {
                //BYTE
                byte b = f.getByte(configObj);
                config.set(field, b);
            } else if (t.toString().startsWith("java.util.List<")) {
                //LISTS
                if (t.toString().equalsIgnoreCase("java.util.List<java.lang.String>")) {
                    //String List
                    List<String> l = (List<String>) f.get(configObj);
                    config.set(field, l);
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Short>")) {
                    //Short List
                    List<Short> l = (List<Short>) f.get(configObj);
                    config.set(field, l);
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Integer>")) {
                    //Integer List
                    List<Integer> l = (List<Integer>) f.get(configObj);
                    config.set(field, l);
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Double>")) {
                    //Double List
                    List<Double> l = (List<Double>) f.get(configObj);
                    config.set(field, l);
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Byte>")) {
                    //Byte List
                    List<Byte> l = (List<Byte>) f.get(configObj);
                    config.set(field, l);
                } else {
                    System.out.println("INVALID ListType Under Saving: " + t.toString());
                }
            }
            //HASHMAPS? Why Though, Keo is being autistic again :P
            else if (t.toString().startsWith("java.util.HashMap<")) {
                if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.String, java.lang.String>")) {
                    //STRING STRING HashMaps
                    Map<String, String> hm = (HashMap<String, String>) f.get(configObj);
                    config.set(field, hm);
                } else if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.String, java.lang.Integer>")) {
                    //STRING INTEGER HashMaps
                    Map<String, Integer> hm = (HashMap<String, Integer>) f.get(configObj);
                    config.set(field, hm);
                } else if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.Integer, java.lang.String>")) {
                    //INTEGER STRING HashMaps
                    Map<Integer, String> hm = (HashMap<Integer, String>) f.get(configObj);
                    config.set(field, hm);
                } else if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.Integer, java.lang.Integer>")) {
                    //INTEGER INTEGR HashMaps
                    Map<Integer, Integer> hm = (HashMap<Integer, Integer>) f.get(configObj);
                    config.set(field, hm);
                } else {
                    System.out.println("INVALID MapType Under Saving: " + t.toString());
                }
            } else {
                System.out.println("INVALID FieldType Under Saving: " + t.toString());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private static void saveConfig(File configFile, YamlConfiguration config, Object configObject) {

        for (Field f : configObject.getClass().getFields()) {
            saveFieldToConfig(f.getName(), configObject, config);
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFieldFromConfig(String field, Object configObj, YamlConfiguration config) {
        //f.setBoolean(configObj, config.getBoolean(field));
        try {
            Field f = configObj.getClass().getField(field);
            Type t = f.getGenericType();
            if (t.toString().equalsIgnoreCase("class java.lang.String")) {
                //STRING
                f.set(configObj, config.getString(field));
            } else if (t.toString().equalsIgnoreCase("boolean")) {
                //BOOLEAN
                f.setBoolean(configObj, config.getBoolean(field));
            } else if (t.toString().equalsIgnoreCase("short")) {
                //Short
                f.setShort(configObj, (short) config.getInt(field));
            } else if (t.toString().equalsIgnoreCase("int")) {
                //Integer
                f.setInt(configObj, config.getInt(field));
            } else if (t.toString().equalsIgnoreCase("double")) {
                //DOUBLE
                f.setDouble(configObj, config.getDouble(field));
            } else if (t.toString().equalsIgnoreCase("byte")) {
                //BYTE
                f.setByte(configObj, (byte) config.getInt(field));
            } else if (t.toString().startsWith("java.util.List<")) {
                //LISTS
                if (t.toString().equalsIgnoreCase("java.util.List<java.lang.String>")) {
                    //String List
                    f.set(configObj, config.getStringList(field));
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Short>")) {
                    //Short List
                    f.set(configObj, config.getShortList(field));
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Integer>")) {
                    //Integer List
                    f.set(configObj, config.getIntegerList(field));
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Double>")) {
                    //Double List
                    f.set(configObj, config.getDoubleList(field));
                } else if (t.toString().equalsIgnoreCase("java.util.List<java.lang.Byte>")) {
                    //Byte List
                    f.set(configObj, config.getByteList(field));
                } else {
                    System.out.println("INVALID ListType Under Loading: " + t.toString());
                }
            } //HASHMAPS? Why Though, Keo is being autistic again :P
            else if (t.toString().startsWith("java.util.HashMap<")) {
                //Okay so basically, our hashmaps work the same as normal fields stored but in a 'nested' way perhaps.
                //We still need to parse the maps though to the apropiate values
                ConfigurationSection section = config.getConfigurationSection(field);
                Map<String, Object> vals = section.getValues(true);
                if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.String, java.lang.String>")) {
                    //STRING STRING HASHMAPS
                    HashMap<String, String> hm = new HashMap<>();
                    for (String s : vals.keySet()) {
                        hm.put(s, vals.get(s).toString());
                    }
                    f.set(configObj, hm);
                } else if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.String, java.lang.Integer>")) {
                    //STRING INTEGER HASHMAPS
                    HashMap<String, Integer> hm = new HashMap<>();
                    for (String s : vals.keySet()) {
                        hm.put(s, (Integer) vals.get(s));
                    }
                    f.set(configObj, hm);
                } else if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.Integer, java.lang.String>")) {
                    //Integer String HASHMAPS
                    HashMap<Integer, String> hm = new HashMap<>();
                    for (String s : vals.keySet()) {
                        hm.put(Integer.valueOf(s), vals.get(s).toString());
                    }
                    f.set(configObj, hm);
                } else if (t.toString().equalsIgnoreCase("java.util.HashMap<java.lang.Integer, java.lang.Integer>")) {
                    //Integer INTEGER HASHMAPS
                    HashMap<Integer, Integer> hm = new HashMap<>();
                    for (String s : vals.keySet()) {
                        hm.put(Integer.valueOf(s), (Integer) vals.get(s));
                    }
                    f.set(configObj, hm);
                } else {
                    System.out.println("INVALID MapType Under Loading: " + t.toString());
                }

            } else {
                System.out.println("INVALID FieldType Under Loading: " + t.toString());
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
