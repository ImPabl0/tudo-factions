package models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Faction {
    private String name;
    private boolean respawnable=true;
    private String leader;
    private List<String> members=new ArrayList<>();


    public boolean playerIsMember(String playerName){
        return members.contains(playerName)||leader.equals(playerName);
    }

    public void addMember(String name) {
        members.add(name);
    }
}
