package me.erksterk.openeventsmc.events.shared.commands;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.utils.MessageUtils;
import me.erksterk.openeventsmc.events.waterdrop.Waterdrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EventsCommand implements CommandExecutor {

    //TODO: Implement a better system to manage commands, as this will be very unreadable at a later point
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        HashMap<String,String> placeholders = new HashMap<>();
        if(label.equalsIgnoreCase("events")){
            if(args.length==0){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Language.TAG+"&aRunning &bOpenEventsMC - V"+ Main.version+" By &cErkSterk"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Language.TAG+"&buse &a/events help &bto view available commands."));
                return true;
            }else if(args[0].equalsIgnoreCase("help")){
                if(sender.hasPermission("openeventmc.commands.help")){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events reset <event>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events toggle <event>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events modify <event> <field> <state>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events revive <event> <playername>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events kick <event> <playername>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events leave <event>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events join <event>"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events join <event> <player>"));

                    return true;
                    //send help information
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("reset")){
                if(sender.hasPermission("openeventmc.commands.reset")) {
                    if (args.length == 1) {
                        //missing  event
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events reset <event>"));
                        return true;
                    } else {
                        if (args[1].equalsIgnoreCase("waterdrop")) {
                            sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_WATERDROP_RESET, placeholders));
                            Waterdrop.reset();
                            return true;
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                            return true;
                        }
                    }
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("revive")){
                if(sender.hasPermission("openeventmc.commands.revive")) {
                    if (args.length < 3) {
                        //missing arguments
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events revive <event> <playername>"));
                        return true;
                    } else {
                        Player p = Bukkit.getPlayer(args[2]);
                        if (p != null) {
                            if (args[1].equalsIgnoreCase("waterdrop")) {
                                if (Waterdrop.waterdrop_dead_players.contains(p)) {
                                    Waterdrop.waterdrop_dead_players.remove(p);
                                    p.teleport(Waterdrop.waterdrop_wait);
                                    placeholders.put("%player%", p.getName());
                                    sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_REVIVED, placeholders));
                                    p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_REVIVED_PLAYER, placeholders));
                                    return true;
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_REVIVED_ALREADYALIVE, placeholders));
                                    return true;
                                }
                            } else {
                                sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                                return true;
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.PLAYERNOTFOUND, placeholders));
                            return true;
                        }
                    }
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("kick")){
                if(sender.hasPermission("openeventmc.commands.kick")) {

                    if (args.length < 3) {
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events kick <event> <playername>"));
                        return true;
                    } else {
                        Player p = Bukkit.getPlayer(args[2]);
                        if (p != null) {
                            if (args[1].equalsIgnoreCase("waterdrop")) {
                                placeholders.put("%player%", p.getName());
                                placeholders.put("%sender%", sender.getName());
                                placeholders.put("%eventname%", "Waterdrop");
                                if (Waterdrop.isPlayerParticipating(p)) {
                                    Waterdrop.waterdrop_not_partaking.add(p);
                                    sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_KICKED, placeholders));
                                    p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_KICKED_PLAYER, placeholders));
                                    return true;
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_KICKED_ALREADYKICKED, placeholders));
                                    return true;
                                }
                            } else {
                                sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                                return true;
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.PLAYERNOTFOUND, placeholders));
                            return true;
                        }
                    }
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("leave")){
                if(sender.hasPermission("openeventmc.commands.leave")) {
                    if (args.length < 2) {
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events leave <event>"));
                        return true;
                    } else {
                        Player p = (Player) sender;
                        if (args[1].equalsIgnoreCase("waterdrop")) {
                            if (Waterdrop.isPlayerParticipating(p)) {
                                Waterdrop.waterdrop_not_partaking.add(p);
                                sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_LEAVE, placeholders));
                                return true;
                            } else {
                                sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_LEAVE_ALREADYELEAVE, placeholders));
                                return true;
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                            return true;
                        }
                    }
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("join")){
                if(sender.hasPermission("openeventmc.commands.join")) {
                    if (args.length == 1) {
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events join <event>"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events join <event> <player>"));
                    } else if (args.length == 2) {
                        //join self
                        Player p = (Player) sender;
                        if (args[1].equalsIgnoreCase("waterdrop")) {
                            if (Waterdrop.waterdrop_not_partaking.contains(p)) {
                                Waterdrop.waterdrop_not_partaking.remove(p);
                                p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_JOIN, placeholders));
                                return true;
                            } else {
                                p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_JOIN_ALREADYJOIN, placeholders));
                                return true;
                            }
                        } else {
                            p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                            return true;
                        }
                    } else {
                        //join other
                        if(sender.hasPermission("openeventmc.commands.join.others")) {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (p != null) {
                                if (args[1].equalsIgnoreCase("waterdrop")) {
                                    if (Waterdrop.waterdrop_not_partaking.contains(p)) {
                                        Waterdrop.waterdrop_not_partaking.remove(p);
                                        p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_JOIN, placeholders));
                                        p.teleport(Waterdrop.waterdrop_wait);
                                        return true;
                                    } else {
                                        p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_JOIN_ALREADYJOIN, placeholders));
                                        return true;
                                    }
                                } else {
                                    p.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                                    return true;
                                }
                            } else {
                                sender.sendMessage(MessageUtils.translateMessage(Language.PLAYERNOTFOUND, placeholders));
                                return true;
                            }
                        }else{
                            sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                            return true;
                        }
                    }
                    return true;
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("status")){
                if(sender.hasPermission("openeventsmc.command.status")) {
                    if (args.length == 1) {
                        if (Waterdrop.running) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bWaterDrop: &aRunning"));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bWaterDrop: &cStopped"));
                        }
                    } else if (args[1].equalsIgnoreCase("waterdrop")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bStonePercentage: " + Waterdrop.stonepercentage));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLapisPercentage: " + Waterdrop.lapispercentage));
                        return true;
                    } else {
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_UNKNOWNEVENT, placeholders));
                        return true;
                    }
                    return true;
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("modify")){
                if(sender.hasPermission("openeventsmc.events.modify")) {
                    if (args.length == 0) {
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        return true;
                    } else {
                        if (args[1].equalsIgnoreCase("waterdrop")) {
                            if (args.length == 2) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events modify <event> <field> <state>"));
                                return true;
                            } else if (args.length == 3) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events modify <event> <field> <state>"));
                                return true;
                            } else {
                                if (args[2].equalsIgnoreCase("lapispercentage")) {
                                    try {
                                        int per = Integer.parseInt(args[3]);
                                        Waterdrop.lapispercentage = per;
                                        sender.sendMessage("Set lapis percentage to: " + per);
                                        return true;
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events modify <event> <field> <state>"));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("stonepercentage")) {
                                    try {
                                        int per = Integer.parseInt(args[3]);
                                        Waterdrop.stonepercentage = per;
                                        sender.sendMessage("Set stone percentage to: " + per);
                                        return true;
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b> &a/events modify <event> <field> <state>"));
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage("Unknown field");
                                    return true;
                                }
                            }
                        }
                    }
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("toggle")){
                if(sender.hasPermission("openeventsmc.event.toggle")) {
                    if (args.length == 1) {
                        sender.sendMessage(MessageUtils.translateMessage(Language.EVENTS_MISSING_ARGUMENTS, placeholders));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b> &a/events toggle <event>"));
                        return true;
                    } else if (args[1].equalsIgnoreCase("waterdrop")) {
                        Waterdrop.pause();
                        sender.sendMessage("toggled status!");
                        return true;
                    }
                }else{
                    sender.sendMessage(MessageUtils.translateMessage(Language.COMMAND_NO_PERMISSIONS,placeholders));
                    return true;
                }
            }
        }
        return false;
    }
}
