package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spleef extends Event {

    BukkitTask taskGame;

    public List<Location> snow = new ArrayList<>();

    public boolean snowball_block_break;

    public Spleef(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.dead");
        requiredFields.add("arena.game");
        requiredFields.add("config.snowball_block_break");
        requiredFields.add("inventory.start_gear");

        forcedMaps.put("region.game.block_break", false);
        HashMap<Material, List<Material>> breakIf = new HashMap<>();
        List<Material> conds = new ArrayList<>();
        conds.add(Material.SNOW_BLOCK);
        breakIf.put(Material.AIR, conds);
        forcedMaps.put("region.game.breakIf", breakIf);

        announce_to_all_online=Config.spleef_announce_to_all_online;
        announce_to_all_partaking=Config.spleef_announce_to_all_partaking;

    }

    public void generateLocations() {
        Region re = getArena().getRegionByname("game");
        snow.clear();
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
                                        if (b.getType() == Material.SNOW_BLOCK) {
                                            snow.add(tmpblock);
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

    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());

        for (ItemStack it : getEventStartGear()) {
            p.getInventory().addItem(it);
        }
    }


    public void resetSnow() {
        for (Location l : snow) {
            l.getBlock().setType(Material.SNOW_BLOCK);
        }
    }

    public void checkForWinner() {
        List<Player> alive = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (isPlayerAlive(p)) {
                alive.add(p);
            }
        }
        if (alive.size() == 0) {
            announceMessage(MessageUtils.translateMessage(Language.Spleef_nowinner, new HashMap<>()));
            Bukkit.getScheduler().runTask(Main.plugin, () -> {
                resetSnow();
            });
            running = false;
            clear();
            setStatus(EventStatus.STOPPED);
            setHoster(null);
            taskGame.cancel();
        } else if (alive.size() == 1) {
            Player p = alive.get(0);
            HashMap<String, String> hm = new HashMap<>();
            hm.put("%player%", p.getName());
            p.teleport(getArena().getRegionByname("wait").getRandomLoc());
            announceMessage(MessageUtils.translateMessage(Language.Spleef_winner, hm));
            Bukkit.getScheduler().runTask(Main.plugin, () -> {
                resetSnow();
            });
            running = false;
            clear();
            setStatus(EventStatus.STOPPED);
            setHoster(null);
            taskGame.cancel();
        }
    }

    public void start() {
        generateLocations();
        Region r = getArena().getRegionByname("wait");
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {

            if (running) {
                setStatus(EventStatus.RUNNING);

                for (Player p : getAllPlayersInRegionXZ(r)) {
                    Location l = getArena().getRegionByname("game").getRandomLoc();
                    l.setY(getArena().getRegionByname("game").getMax().getBlockY());
                    p.teleport(l);
                }
                checkForWinner();
            }
        }, 0, 20);
    }

    private void clear() {
        getPlayers().clear();
        eliminated.clear();
    }
}

