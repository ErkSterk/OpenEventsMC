package me.erksterk.openeventsmc.libraries.tinyphoenix.cooldown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CooldownHandler{
    //TODO: option to get time left of cooldowns.
    //Player, Commands
    static HashMap<String, List<String>> Cooldowns = new HashMap<>();
    static List<Cooldown> threads = new ArrayList<>();
    static int nextid=0;
    //puts a player on cooldown
    public static void createNewCooldown(String pname,String command, int seconds){
        if(Cooldowns.containsKey(pname)){
            List<String> cmds = Cooldowns.get(pname);
            if(cmds.contains(command)){

            }else{
                cmds.add(command);
                Cooldowns.put(pname,cmds);
                Cooldown cd = new Cooldown(seconds,pname,command,nextid+"");
                nextid++;
                cd.start();
            }
        }else{
            List<String> cmds = new ArrayList<>();
            cmds.add(command);
            Cooldowns.put(pname,cmds);
            Cooldown cd = new Cooldown(seconds,pname,command,nextid+"");
            nextid++;
            cd.start();
        }

    }
    public static void removePlayer(String player, String command){
        if(Cooldowns.containsKey(player)){
            List<String> comm = Cooldowns.get(player);
            if(comm.contains(command)){
                comm.remove(command);
                if(comm.isEmpty()){
                    Cooldowns.remove(player);
                }else{
                    Cooldowns.put(player,comm);
                }
            }
        }
    }
    public static void removeObject(Cooldown c){
        threads.remove(c);
    }

    //checks if the player is on cooldown
    public static boolean isPlayerOnCooldown(String player, String command){
        if(Cooldowns.containsKey(player)){
            List<String> cmds = Cooldowns.get(player);
            if(cmds.contains(command)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
