package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WoolShuffle extends Event {

    public int waittime = 5;
    public int warntime = 5;
    int schedulerSeconds = 0;

    BukkitTask taskGame;
    BukkitTask taskMove;

    public WoolShuffle(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.kill");
        requiredFields.add("arena.floor");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.dead");
        requiredFields.add("config.waittime");
        requiredFields.add("config.warntime");

        announce_to_all_online=Config.woolshuffle_announce_to_all_online;
        announce_to_all_partaking=Config.woolshuffle_announce_to_all_partaking;
    }

    private void checkForWinner() {
        List<Player> alive = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!eliminated.contains(p)) alive.add(p);
        }

        if (alive.size() == 0) {
            running = false;
            setStatus(EventStatus.STOPPED);
            eliminated.clear();
            clearPlayers();
            setHoster(null);
            announceMessage(MessageUtils.translateMessage(Language.Woolshuffle_nowinner,new HashMap<>()));
            taskGame.cancel();
            taskMove.cancel();

        } else if (alive.size() == 1) {
            Player p = alive.get(0);
            HashMap<String,String> hm = new HashMap<>();
            hm.put("%player%",p.getName());
            announceMessage(MessageUtils.translateMessage(Language.Woolshuffle_winner,hm));
            running = false;
            setStatus(EventStatus.STOPPED);
            eliminated.clear();
            clearPlayers();
            setHoster(null);
            taskGame.cancel();
            taskMove.cancel();
        }
    }

    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());
    }


    public ChatColor translateDyeToCC(DyeColor dc){
        switch (dc){
            case YELLOW: return ChatColor.YELLOW;
            case SILVER: return ChatColor.GRAY;
            case GRAY: return ChatColor.DARK_GRAY;
            case RED: return ChatColor.RED;
            case BROWN:
            case ORANGE:
                return ChatColor.GOLD;
            case CYAN: return ChatColor.DARK_AQUA;
            case BLUE: return ChatColor.DARK_BLUE;
            case LIGHT_BLUE: return ChatColor.BLUE;
            case BLACK: return ChatColor.BLACK;
            case LIME: return ChatColor.GREEN;
            case PINK:
            case MAGENTA:
                return ChatColor.LIGHT_PURPLE;
            case GREEN: return ChatColor.DARK_GREEN;
            case PURPLE: return ChatColor.DARK_PURPLE;
            case WHITE:
            default: return ChatColor.WHITE;
        }
    }

    public DyeColor getColorByNumber(int c) {
        switch (c) {
            case 0:
                return DyeColor.BLACK;
            case 1:
                return DyeColor.BLUE;
            case 2:
                return DyeColor.BROWN;
            case 3:
                return DyeColor.CYAN;
            case 4:
                return DyeColor.GRAY;
            case 5:
                return DyeColor.GREEN;
            case 6:
                return DyeColor.LIGHT_BLUE;
            case 7:
                return DyeColor.LIME;
            case 8:
                return DyeColor.MAGENTA;
            case 9:
                return DyeColor.ORANGE;
            case 10:
                return DyeColor.PINK;
            case 11:
                return DyeColor.PURPLE;
            case 12:
                return DyeColor.RED;
            case 13:
                return DyeColor.SILVER;
            case 14:
                return DyeColor.YELLOW;
            default:
                return DyeColor.WHITE;
        }
    }

    public void randomizeGround() {
        Region re = getArena().getRegionByname("floor");
        if (re != null) {
            for (int x = re.getMin().getBlockX(); x <= re.getMax().getBlockX(); x = x + 1) {
                for (int y = re.getMin().getBlockY(); y <= re.getMax().getBlockY(); y = y + 1) {
                    for (int z = re.getMin().getBlockZ(); z <= re.getMax().getBlockZ(); z = z + 1) {
                        Location tmpblock = new Location(Bukkit.getWorld(re.getMax().getWorld().getName()), x, y, z);
                        if (tmpblock != null) {
                            try {
                                Block b = tmpblock.getBlock();
                                if (b != null) {
                                    int c = ThreadLocalRandom.current().nextInt(0, 15);
                                    b.setType(Material.WOOL);
                                    BlockState bs = b.getState();
                                    Wool wool = (Wool) bs.getData();
                                    wool.setColor(getColorByNumber(c));
                                    bs.update();

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public void clearGroundExcempt(int c) {
        Region re = getArena().getRegionByname("floor");
        if (re != null) {
            for (int x = re.getMin().getBlockX(); x <= re.getMax().getBlockX(); x = x + 1) {
                for (int y = re.getMin().getBlockY(); y <= re.getMax().getBlockY(); y = y + 1) {
                    for (int z = re.getMin().getBlockZ(); z <= re.getMax().getBlockZ(); z = z + 1) {
                        Location tmpblock = new Location(Bukkit.getWorld(re.getMax().getWorld().getName()), x, y, z);
                        if (tmpblock != null) {
                            try {
                                Block b = tmpblock.getBlock();
                                if (b != null) {
                                    if (b.getType() != null) {
                                        if (b.getType() == Material.WOOL) {
                                            BlockState bs = b.getState();
                                            Wool wool = (Wool) bs.getData();
                                            if (wool.getColor() != getColorByNumber(c)) {
                                                b.setType(Material.AIR);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    boolean announced = false;
    boolean wait = false;
    int currentColor = 0;
    boolean removed = false;
    boolean scheduled=false;

    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            if (running) {
                setStatus(EventStatus.RUNNING);
                checkForWinner(); //Check if someone has won and end the event.
                schedulerSeconds++;
                if (wait) {
                    if (schedulerSeconds > waittime) {
                        wait = false;
                        schedulerSeconds = 0;
                        announced = false;
                    }
                } else {
                    if (!announced) {
                        currentColor = ThreadLocalRandom.current().nextInt(0, 15);
                        HashMap<String,String> hm = new HashMap<>();
                        hm.put("%color%",translateDyeToCC(getColorByNumber(currentColor))+getColorByNumber(currentColor).toString());
                        announceMessage(MessageUtils.translateMessage(Language.Woolshuffle_goto,hm));
                        announced = true;
                    }
                    if (schedulerSeconds > warntime) {
                        if (!removed) {
                            Bukkit.getScheduler().runTask(Main.plugin, () -> {
                                clearGroundExcempt(currentColor);
                                removed = true;
                            });
                        }
                        if(!scheduled) {
                            scheduled=true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                                randomizeGround();
                                schedulerSeconds = 0;
                                wait = true;
                                removed = false;
                                scheduled=false;
                            }, waittime * 20);
                        }
                    }
                }



            }

        }, 0, 20);
        taskMove = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            if (running) {
                //Player is partaking in an event
                for (Player p : getAllAlivePlayers()) {
                    if (getArena().getRegionByname("wait").isInBoundsXZ(p.getLocation())) {
                        Location l = getArena().getRegionByname("floor").getRandomLoc();
                        l.setY(l.getBlockY() + 2);
                        p.teleport(l);
                    } else if (getArena().getRegionByname("kill").isInBounds(p.getLocation())) {
                        p.teleport(getArena().getRegionByname("dead").getRandomLoc());
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("%player%", p.getName());
                        eliminated.add(p);
                        announceMessage(MessageUtils.translateMessage(Language.Woolshuffle_eliminated, hm));
                    }
                }
            }

        }, 0, 1);
    }

    private List<Player> getAllAlivePlayers() {
        List<Player> alive = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!eliminated.contains(p)) {
                alive.add(p);
            }
        }
        return alive;
    }
}
