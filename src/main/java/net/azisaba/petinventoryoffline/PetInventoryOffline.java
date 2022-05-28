package net.azisaba.petinventoryoffline;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PetInventoryOffline extends JavaPlugin {
    @Override
    public void onEnable() {
        Objects.requireNonNull(Bukkit.getPluginCommand("petinventoryoffline"))
                .setExecutor(new PioCommand());
    }
}
