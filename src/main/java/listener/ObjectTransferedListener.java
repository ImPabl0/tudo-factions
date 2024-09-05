package listener;

import models.Faction;
import models.ServerContext;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class ObjectTransferedListener implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        System.out.println("Player joined");
        ServerContext serverContext = ServerContext.getInstance();
        if(serverContext == null) return;
        Player player = event.getPlayer();
        Optional<Faction> playerFaction = serverContext.getFactionByPlayer(player.getName());
        if(playerFaction.isPresent()&&playerFaction.get().isRespawnable()){
            World world = player.getWorld();
            player.sendMessage("§c A sua facção não pode reviver.");
        }
    }

    @EventHandler
    public void onObjectDropped(PlayerDropItemEvent event){
        System.out.println("Item dropped");
        ServerContext serverContext = ServerContext.getInstance();
        Item item = event.getItemDrop();
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        Player player = event.getPlayer();
        Server server = player.getServer();

        if(itemMeta==null) return;
        boolean isCustomItem= itemMeta.getLore()!=null&&!itemMeta.getLore().isEmpty();
        String droppedFaction = itemMeta.getDisplayName().replace("§4", "");
        if(serverContext == null||!isCustomItem) return;
        Optional<Faction> faction = serverContext.getFactionByName(droppedFaction);
        if(faction.isPresent()){
            Faction foundFaction = faction.get();
            if(foundFaction.isRespawnable()){
                foundFaction.setRespawnable(false);
                server.getOnlinePlayers().forEach(onlinePlayer->{

                    if(foundFaction.playerIsMember(onlinePlayer.getName())){
                        onlinePlayer.sendMessage(String.format("§c %s dropou a sua jóia e você não pode reviver.",player.getName()));
                    }
                });
                serverContext.save();
            }
        }

    }

    private void spawnLightning(Player player){
        World world = player.getWorld();
        world.strikeLightningEffect(player.getLocation());
    }

    @EventHandler
    public void onObjectPicked(EntityPickupItemEvent event){
        System.out.println("Item picked");
        ServerContext serverContext = ServerContext.getInstance();
        Item item = event.getItem();
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        Server server = event.getEntity().getServer();
        if(itemMeta==null) return;
        boolean isCustomItem= itemMeta.getDisplayName().contains("§4")&&itemMeta.getLore()!=null&&!itemMeta.getLore().isEmpty();
        String pickedFactionName = itemMeta.getDisplayName().replace("§4", "");
        if(serverContext == null||!isCustomItem) return;
        if(event.getEntity() instanceof Player playerPicker){
            Optional<Faction> pickerFaction = serverContext.getFactionByPlayer(playerPicker.getName());
            Optional<Faction> pickedFaction = serverContext.getFactionByName(pickedFactionName);
            boolean isSameFaction = pickerFaction.isPresent()&&pickedFaction.isPresent()&&pickerFaction.get().getName().equals(pickedFaction.get().getName());
            if(isSameFaction&&!pickerFaction.get().isRespawnable()){
                pickerFaction.get().setRespawnable(true);
                server.getOnlinePlayers().forEach(onlinePlayer->{
                    if(pickerFaction.get().playerIsMember(onlinePlayer.getName())){
                        onlinePlayer.sendMessage("§a A sua facção pode reviver novamente.");
                    }
                });
                serverContext.save();
            }
            if(!isSameFaction&&pickedFaction.isPresent()&&pickedFaction.get().isRespawnable()){
                pickedFaction.get().setRespawnable(false);
                server.getOnlinePlayers().forEach(onlinePlayer->{
                    if(pickedFaction.get().playerIsMember(onlinePlayer.getName())){
                        spawnLightning(onlinePlayer);
                        onlinePlayer.sendMessage("§c Alguém pegou a sua jóia e você não pode reviver.");
                    }
                });
                serverContext.save();
            }

        }
    }
}
