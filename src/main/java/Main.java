import commands.AcceptInviteFactionCommand;
import commands.CreateFactionCommand;
import commands.InviteFactionCommand;
import listener.ObjectTransferedListener;
import models.ServerContext;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
        getLogger().info("TudoFaction has been enabled!");
    }

    private void registerListeners(){
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ObjectTransferedListener(), this);
    }

    private void registerCommands(){
        this.getCommand("criarfac").setExecutor(new CreateFactionCommand());
        this.getCommand("convidar").setExecutor(new InviteFactionCommand());
        this.getCommand("aceitar").setExecutor(new AcceptInviteFactionCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("TudoFaction has been disabled!");
    }
}
