package me.erksterk.openeventsmc.libraries.clicktunnel;

import java.util.ArrayList;
import java.util.List;

public class GuiAction {
    public String openGui="";
    public List<String> commandsPlayer = new ArrayList<>();
    public List<String> commandsCmd = new ArrayList<>();
    public boolean closeGui=false;
    public boolean itemLock;

    public GuiAction(boolean itemLock){
        this.itemLock=itemLock;
    }
}
