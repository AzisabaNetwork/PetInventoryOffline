package net.azisaba.petinventoryoffline;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.StoredMyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.repository.RepositoryCallback;
import de.Keyle.MyPet.api.skill.skills.Backpack;
import de.Keyle.MyPet.api.skill.skilltree.Skill;
import de.Keyle.MyPet.api.util.NBTStorage;
import net.azisaba.petinventoryoffline.mypet.MyPetClass;
import net.azisaba.petinventoryoffline.util.MyPetUtil;
import net.azisaba.petinventoryoffline.util.TagCompound;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PioCommand implements TabExecutor {
    private final PetInventoryOffline plugin;

    public PioCommand(@NotNull PetInventoryOffline plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/pio <player or pet uuid>");
            return true;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (Exception e) {
            new Thread(() -> {
                MyPetPlayer myPetPlayer = MyPetApi.getRepository()
                        .getAllMyPetPlayers()
                        .stream()
                        .filter(player -> player.getName().equalsIgnoreCase(args[0]))
                        .findFirst()
                        .orElse(null);
                //MyPetPlayer myPetPlayer = MyPetApi.getPlayerManager().getMyPetPlayer(args[0]);
                if (myPetPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player or uuid: " + args[0]);
                    return;
                }
                MyPetApi.getRepository().getMyPets(myPetPlayer, new RepositoryCallback<List<StoredMyPet>>() {
                    @Override
                    public void callback(List<StoredMyPet> storedMyPets) {
                        sender.sendMessage(ChatColor.GREEN + "MyPets of " + ChatColor.YELLOW + myPetPlayer.getName() + ChatColor.GREEN + ":");
                        for (StoredMyPet storedMyPet : storedMyPets) {
                            TextComponent text = new TextComponent();
                            TextComponent dash = new TextComponent(" -> ");
                            dash.setColor(ChatColor.AQUA.asBungee());
                            text.addExtra(dash);
                            TextComponent name = new TextComponent(storedMyPet.getPetName());
                            name.setColor(ChatColor.YELLOW.asBungee());
                            TextComponent nameTooltip = new TextComponent("Click to open pet inventory");
                            name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { nameTooltip }));
                            name.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pio " + storedMyPet.getUUID()));
                            TextComponent petType = new TextComponent(" [" + storedMyPet.getPetType().name() + "] ");
                            petType.setColor(ChatColor.AQUA.asBungee());
                            name.addExtra(petType);
                            text.addExtra(name);
                            TextComponent uuid = new TextComponent("(" + storedMyPet.getUUID() + ")");
                            uuid.setColor(ChatColor.GRAY.asBungee());
                            TextComponent uuidTooltip = new TextComponent("Click to fill command");
                            uuid.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { uuidTooltip }));
                            uuid.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pio " + storedMyPet.getUUID()));
                            text.addExtra(uuid);
                            sender.spigot().sendMessage(text);
                        }
                    }
                });
            }).start();
            return true;
        }
        new Thread(() -> {
            StoredMyPet storedMyPet = MyPetApi.getRepository()
                    .getAllMyPets()
                    .stream()
                    .filter(p -> p.getUUID().equals(uuid))
                    .findFirst()
                    .orElse(null);
            if (storedMyPet == null) {
                sender.sendMessage(ChatColor.RED + "No pet found for UUID: " + args[0]);
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                MyPet myPet = MyPetClass.getByMyPetType(storedMyPet.getPetType()).getNewMyPetInstance(storedMyPet.getOwner());
                myPet.setUUID(storedMyPet.getUUID());
                myPet.setPetName(storedMyPet.getPetName());
                myPet.setExp(storedMyPet.getExp());
                myPet.setSkilltree(storedMyPet.getSkilltree());
                TagCompound skillInfo = new TagCompound(MyPetUtil.getSkillInfo(storedMyPet));
                Collection<Skill> skills = myPet.getSkills().all();
                if (skills.size() > 0) {
                    for (Skill skill : skills) {
                        if (skill instanceof NBTStorage) {
                            NBTStorage storageSkill = (NBTStorage) skill;
                            if (skillInfo.getCompoundData().containsKey(skill.getName())) {
                                //storageSkill.load(skillInfo.getAs(skill.getName(), skillInfo.getWrappedObject().getClass()));
                                MyPetUtil.NBTStorage_load(storageSkill, skillInfo.getAs(skill.getName(), skillInfo.getWrappedObject().getClass()));
                            }
                        }
                    }
                }
                try {
                    //noinspection unchecked
                    Skill skill = myPet.getSkills().get((Class<? extends Skill>) Class.forName("de.Keyle.MyPet.skill.skills.BackpackImpl"));
                    //noinspection JavaReflectionInvocation
                    skill.getClass().getMethod("openInventory", Player.class).invoke(skill, sender);
                } catch (Exception e) {
                    try {
                        myPet.getSkills().get(Backpack.class).getInventory().open((Player) sender);
                    } catch (Exception ex2) {
                        ex2.addSuppressed(e);
                        throw new RuntimeException(ex2);
                    }
                }
            });
        }).start();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName), args[0]).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static @NotNull Stream<String> filter(@NotNull Stream<String> stream, @NotNull String s) {
        return stream.filter(s1 -> s1.toLowerCase().startsWith(s.toLowerCase()));
    }
}
