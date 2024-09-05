package models;

import com.google.gson.Gson;
import interfaces.Store;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class ServerContext extends Store {

    private List<Faction> factions = new ArrayList<>();
    private List<Invite> invites = new ArrayList<>();

    private static ServerContext instance;
    public static ServerContext getInstance() {
        if(instance==null){
            instance = new ServerContext();
        }
        instance.load();
        return instance;
    }

    protected Map<String, Object> toMap() {
        Gson gson = new Gson();
        Map map = gson.fromJson(gson.toJson(this), Map.class);
        return map;
    }

    protected void fromMap(Map<String, Object> map) {
        Gson gson = new Gson();
        ServerContext serverContext = gson.fromJson(gson.toJson(map), ServerContext.class);
        if(serverContext==null) return;
        if (serverContext.factions != null) this.factions = serverContext.factions;
    }

    public List<Faction> getFactions() {
        return factions;
    }

    public void addFaction(Faction faction) {
        factions.add(faction);
        save();
    }

    public void removeFaction(Faction faction) {
        factions.remove(faction);
        save();
    }

    public Faction getFaction(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equals(name)) {
                return faction;
            }
        }
        return null;
    }

    public boolean hasInvite(String player){
        for(Invite invite:invites){
            if(invite.getPlayer().equals(player)){
                return true;
            }
        }
        return false;
    }

    public Optional<Invite> findInvite(String faction, String player){
        for(Invite invite:invites){
            if(invite.getPlayer().equals(player)&&invite.getFaction().equals(faction)){
                return Optional.of(invite);
            }
        }
        return Optional.empty();
    }

    public boolean hasFaction(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean playerHasFaction(String player) {
        for (Faction faction : factions) {
            if (faction.getLeader().equals(player)) {
                return true;
            }
            for (String member : faction.getMembers()) {
                if (member.equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<Faction> getFactionByPlayer(String player){
        for(Faction faction:factions){
            if(faction.getLeader().equals(player)){
                return Optional.of(faction);
            }
            for(String member:faction.getMembers()){
                if(member.equals(player)){
                    return Optional.of(faction);
                }
            }
        }
        return Optional.empty();
    }



    public Optional<Faction> getFactionByName(String pickedFactionName) {
        for(Faction faction:factions){
            if(faction.getName().equals(pickedFactionName)){
                return Optional.of(faction);
            }
        }
        return Optional.empty();
    }

    public void addInvite(Invite invite) {
        invites.add(invite);
        save();
    }

    public void removeInvite(Invite invite) {
        invites.remove(invite);
        save();
    }
}
