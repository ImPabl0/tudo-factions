package commands;

import models.Faction;
import models.ServerContext;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class CreateFactionCommand implements CommandExecutor {

    private boolean hasSufficientGold(PlayerInventory inventario, int goldAmmount) {
        return inventario.contains(Material.GOLD_INGOT, goldAmmount);
    }

    private void setGuildItemName(ItemStack item, String name) {
        ItemMeta itemInfo = item.getItemMeta();
        if (itemInfo == null) return;
        itemInfo.setDisplayName(String.format("§4%s", name));
        itemInfo.setLore(new ArrayList<>() {{
            add(String.format("Bandeira da facção %s", name));
        }});
        item.setItemMeta(itemInfo);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerContext serverContext = ServerContext.getInstance();
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
        if(arguments.size() !=1){
            return false;
        }
        String factionName = arguments.getFirst();
        if (serverContext == null) {
            sender.getServer().getLogger().log(Level.SEVERE, "ServerContext não foi inicializado");
            return true;
        }
        if (sender instanceof Player player) {
            PlayerInventory playerInventory = player.getInventory();
            World world = player.getWorld();

            boolean playerHasFaction = serverContext.playerHasFaction(player.getName());
            boolean factionExists = serverContext.hasFaction(factionName);
            boolean hasSufficientGold = hasSufficientGold(playerInventory, 64);
            if (playerHasFaction) {
                player.sendMessage("Você já possui uma facção!");
                return true;
            }
            if (label.length() < 2) {
                player.sendMessage("A facção precisa ter pelo menos 3 letras");
                return true;
            }
            if (!hasSufficientGold) {
                player.sendMessage("Você não tem ouro suficiente");
                return true;
            }
            if(factionExists){
                player.sendMessage("Essa facção já existe");
                return true;
            }
            Faction faction = Faction.builder()
                    .leader(player.getName())
                    .name(factionName)
                    .respawnable(true)
                    .members(new ArrayList<>())
                    .build();

            playerInventory.remove(new ItemStack(Material.GOLD_INGOT, 64));
            world.strikeLightningEffect(player.getLocation());
            ItemStack guildItem = new ItemStack(Material.BLACK_BANNER, 1);
            setGuildItemName(guildItem, factionName);
            playerInventory.addItem(guildItem);
            serverContext.addFaction(faction);
            player.getServer().broadcastMessage(String.format("O jogador %s criou a facção %s", player.getName(), factionName));
            return true;
        }
        return false;
    }
}
