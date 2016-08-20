package xyz.michaelsteffan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	FileConfiguration config = getConfig();

	String permission_denied = config.getString("permission-denied").replaceAll("&", "§");

	String prefix = config.getString("prefix").replaceAll("&", "§");

	String chat_prefix = config.getString("chat-prefix").replaceAll("&", "§");

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		config.options().copyDefaults(true);
		saveConfig();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();

		if (!(p.hasPlayedBefore())) {
			Bukkit.broadcastMessage(
					prefix + config.getString("new-join-message").replaceAll("&", "§").replace("$player", p.getName()));

		} else {
			e.setJoinMessage(prefix + getConfig().getString("join-message").replaceAll("&", "§"));
		}
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent e) {
		if (!e.getPlayer().isFlying() && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			e.getPlayer().setAllowFlight(true);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage(prefix + config.getString("quit-message").replaceAll("&", "§"));
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {

		e.setFormat(chat_prefix + ChatColor.translateAlternateColorCodes('&', "chat-name-color")
				+ e.getPlayer().getDisplayName() + " "
				+ ChatColor.translateAlternateColorCodes('&', "chat-color-message" + e.getMessage()));

	}

	@EventHandler
	public void onHungry(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onHealth(EntityRegainHealthEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerClickTab(PlayerChatTabCompleteEvent e) {
		e.getTabCompletions().clear();
		e.getChatMessage().charAt(0);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			if (e.getCause() == DamageCause.ENTITY_ATTACK)
				e.setCancelled(true);
			{
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

		Player player = (Player) sender;

		if (sender instanceof Player) {
			if (!(player.hasPermission("cub.sethub"))) {
				player.sendMessage(prefix + permission_denied);
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("sethub")) {
				if (player.hasPermission("cub.sethub")) {
					player.getLocation().getWorld().setSpawnLocation(player.getLocation().getBlockX(),
							player.getLocation().getBlockY() + 1, player.getLocation().getBlockZ());
					player.sendMessage(prefix + config.getString("sethub").replaceAll("&", "§"));
				}
			}

			if (cmd.getName().equalsIgnoreCase("hub")) {
				player.teleport(player.getWorld().getSpawnLocation());
				player.sendMessage(prefix + config.getString("hub").replaceAll("&", "§"));
			}

			if (sender instanceof Player) {
				if (cmd.getName().equalsIgnoreCase("?") || cmd.getName().equalsIgnoreCase("pl")
						|| cmd.getName().equalsIgnoreCase("plugins") || cmd.getName().equalsIgnoreCase("about")
						|| cmd.getName().equalsIgnoreCase("ver") || cmd.getName().equalsIgnoreCase("version")
						|| cmd.getName().equalsIgnoreCase("canihasbukkit")) {
					sender.sendMessage(prefix + config.getString("plugin-blocked").replaceAll("&", "§"));
				}
			}

			if (sender instanceof Player) {
				if (cmd.getName().equalsIgnoreCase("server")) {

					Inventory server_selector = Bukkit.createInventory(null, config.getInt("gui-slots"),
							config.getString("gui-name").replaceAll("&", "§"));

					ItemStack slot_1 = new ItemStack(
							Material.valueOf(config.getString("gui-slot-material").toUpperCase()));
					ItemMeta slot_1_meta = slot_1.getItemMeta();
					slot_1_meta.setDisplayName(config.getString("slot-1-name").replaceAll("&", "§"));

					ItemStack slot_2 = new ItemStack(
							Material.valueOf(config.getString("gui-slot2-material").toUpperCase()));
					ItemMeta slot_2_meta = slot_2.getItemMeta();
					slot_2_meta.setDisplayName(config.getString("slot-2-name").replaceAll("&", "§"));

					ItemStack slot_3 = new ItemStack(
							Material.valueOf(config.getString("gui-slot3-material").toUpperCase()));
					ItemMeta slot_3_meta = slot_3.getItemMeta();
					slot_3_meta.setDisplayName(config.getString("slot-3-name").replaceAll("&", "§"));

					ItemStack slot_4 = new ItemStack(
							Material.valueOf(config.getString("gui-slot4-material").toUpperCase()));
					ItemMeta slot_4_meta = slot_4.getItemMeta();
					slot_4_meta.setDisplayName(
							ChatColor.translateAlternateColorCodes('&', config.getString("slot-4-name")));

					server_selector.setItem(config.getInt("gui-slot-1"), slot_1);
					server_selector.setItem(config.getInt("gui-slot-2"), slot_2);
					server_selector.setItem(config.getInt("gui-slot-3"), slot_3);
					server_selector.setItem(config.getInt("gui-slot-4"), slot_4);

					player.openInventory(server_selector);
				}

			}

			if (cmd.getName().equalsIgnoreCase("cub")) {
				sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Cub by Michael Steffan."
						+ ChatColor.DARK_GRAY + "]");
				sender.sendMessage(ChatColor.GRAY + "/cub" + ChatColor.RED + " - returns this command.");
				sender.sendMessage(
						ChatColor.GRAY + "/cub permissions" + ChatColor.RED + " - gives full list of permissions.");
				sender.sendMessage(
						ChatColor.GRAY + "/cub credits" + ChatColor.RED + " - credits people on this plugin.");
				sender.sendMessage(ChatColor.GRAY + "/cub other" + ChatColor.RED + " - unrelated stuff.");
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("permissions")) {
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Cub by Michael Steffan."
							+ ChatColor.DARK_GRAY + "]");
					sender.sendMessage(ChatColor.GRAY + "cub.flight" + ChatColor.RED
							+ " - gives players to fly around the server.");
					sender.sendMessage(ChatColor.GRAY + "cub.sethub" + ChatColor.RED + " - Ability to set the hub.");
				}

				if (args.length == 1) {
					if (player.hasPermission("cub.reload")) {
						if (args[0].equalsIgnoreCase("reload")) {
							sender.sendMessage(prefix + ChatColor.RED + "Reloading config...");
							this.reloadConfig();
							this.saveConfig();
							sender.sendMessage(prefix + ChatColor.GREEN + "Config has been reloaded!");
						}
					}
				}

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("credits")) {
						sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Cub by Michael Steffan."
								+ ChatColor.DARK_GRAY + "]");
						sender.sendMessage(
								ChatColor.GRAY + "Michael Steffan" + ChatColor.RED + " - Developer for Cub.");
					}

					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("other")) {
							sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Cub by Michael Steffan."
									+ ChatColor.DARK_GRAY + "]");
							sender.sendMessage(ChatColor.GREEN + "insert some stuff here.");
						}
					}

				}
				return true;

			}
			return true;
		}
		return true;
	}
}
