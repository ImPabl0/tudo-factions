package commands;

import models.Faction;
import models.Invite;
import models.ServerContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AcceptInviteFactionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player){
            ServerContext serverContext = ServerContext.getInstance();
            if(serverContext == null) return false;
            if(args.length != 1) return false;
            String factionName = args[0];
            Optional<Invite> foundInvite = serverContext.findInvite(factionName, player.getName());
            if(foundInvite.isEmpty()){
                player.sendMessage("Você não foi convidado para esta facção.");
                return true;
            }
            Optional<Faction> playerFaction = serverContext.getFactionByPlayer(player.getName());
            if(playerFaction.isPresent()){
                player.sendMessage("Você já faz parte de uma facção.");
                return true;
            }
            Faction faction = serverContext.getFaction(factionName);
            if(faction == null){
                player.sendMessage("Facção não encontrada.");
                return true;
            }
            faction.addMember(player.getName());
            serverContext.removeInvite(foundInvite.get());
            serverContext.save();
            return true;
        }
        return false;
    }
}
