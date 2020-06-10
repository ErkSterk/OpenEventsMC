package me.erksterk.openeventsmc.libraries.tinyphoenix.cooldown;

public class Cooldown extends Thread{
    int sec;
    String player;
    String command;
    public Cooldown(int sec, String player, String command,String ID){
        this.sec=sec;
        this.player=player;
        this.command=command;
    }
    public void run(){
        try {
            sleep(sec*1000);
            CooldownHandler.removePlayer(player,command);
            CooldownHandler.removeObject(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
