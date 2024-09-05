package commands;

import models.Faction;
import models.Invite;
import models.ServerContext;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class InviteFactionCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
            if (argsList.size() != 1) {
                return false;
            }
            ServerContext serverContext = ServerContext.getInstance();
            if (serverContext == null) return false;
            Player player = (Player) sender;
            Server server = player.getServer();
            Optional<Faction> playerFaction = serverContext.getFactionByPlayer(player.getName());
            if (playerFaction.isEmpty()) {
                player.sendMessage("Você não faz parte de nenhuma facção.");
                return true;
            }
            Faction getPlayerFaction = playerFaction.get();
            if (!getPlayerFaction.getLeader().equals(player.getName())) {
                player.sendMessage("Você não é lider desta facção.");
                return true;
            }
            Player invitedPlayer = server.getPlayerExact(argsList.getFirst());
            if (invitedPlayer == null) {
                player.sendMessage("Jogador não encontrado.");
                return true;
            }
            if (serverContext.hasInvite(invitedPlayer.getName())) {
                player.sendMessage("Jogador já foi convidado.");
                return true;
            }
            serverContext.addInvite(new Invite(invitedPlayer.getName(), getPlayerFaction.getName()));
            invitedPlayer.sendMessage(String.format("§2Você foi convidado para a facção %s, digite /aceitar %s para se juntar!", getPlayerFaction.getName(), getPlayerFaction.getName()));
            invitedPlayer.playNote(invitedPlayer.getLocation(), Instrument.PIANO, Note.natural(5, Note.Tone.G));
            serverContext.save();
            return true;
        }
        return false;
    }
}
