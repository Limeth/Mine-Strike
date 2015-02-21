package cz.minestrike.me.limeth.minestrike.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.StructureModifier;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.Plot;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeType;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.cases.Case;
import cz.minestrike.me.limeth.minestrike.equipment.cases.CaseContent;
import cz.minestrike.me.limeth.minestrike.equipment.cases.CaseContentRarity;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GameManager;
import cz.minestrike.me.limeth.minestrike.scene.games.GameType;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import net.minecraft.server.v1_7_R4.WatchableObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MSExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!sender.hasPermission("MineStrike.admin"))
		{
			sender.sendMessage(ChatColor.RED + "Přístup odepřen!");
			return true;
		}
		
		if(args.length <= 0)
		{
			sender.sendMessage("/ms give [Equipment ID] (Player)");
			sender.sendMessage("/ms case [Case ID] [Rarity] [Index] (Player) (Drop)");
			sender.sendMessage("/ms xp [set|add] [Amount] (Player)");
			sender.sendMessage("/ms data [save|load|reload] (Player) (Comparison TRUE|false)");
			sender.sendMessage("/ms packet ...");
			sender.sendMessage("/ms scheme ...");
			sender.sendMessage("/ms game ...");
		}
		else if(args[0].equalsIgnoreCase("packet"))
		{
			if(args.length <= 1)
			{
				sender.sendMessage("/ms packet [Play|Handshake|Legacy|Login|Status] ...");
				return true;
			}
			
			Class<?> event;
			
			switch(args[1].toLowerCase())
			{
			case "play": event = PacketType.Play.class; break;
			case "handshake": event = PacketType.Handshake.class; break;
			case "legacy": event = PacketType.Legacy.class; break;
			case "login": event = PacketType.Login.class; break;
			case "status": event = PacketType.Status.class; break;
			default:
				sender.sendMessage(ChatColor.RED + "Unknown event " + args[1]);
				return true;
			}
			
			if(args.length <= 2)
			{
				sender.sendMessage("/ms packet " + args[1] + " [Client|Server]");
				return true;
			}
			
			Class<?> direction = null;
			
			for(Class<?> curDirection : event.getClasses())
				if(curDirection.getSimpleName().equalsIgnoreCase(args[2]))
				{
					direction = curDirection;
					break;
				}
			
			if(direction == null)
			{
				sender.sendMessage(ChatColor.RED + "Invalid direction " + args[2]);
				return true;
			}
			
			if(args.length <= 3)
			{
				String packets = "";
				
				for(Field field : direction.getFields())
					if(field.getType() == PacketType.class)
						packets += field.getName() + ", ";
				
				sender.sendMessage("/ms packet " + args[1] + " " + args[2] + " [Packet]\n" + packets);
				return true;
			}
			
			PacketType type;
			
			try
			{
				Field field = direction.getField(args[3].toUpperCase());
				
				if(field.getType() != PacketType.class)
					throw new NoSuchFieldException(args[3]);
				
				type = (PacketType) field.get(null);
			}
			catch(NoSuchFieldException | ClassCastException e)
			{
				sender.sendMessage(ChatColor.RED + "Packet " + args[3] + " not found");
				return true;
			}
			catch(SecurityException | IllegalAccessException e)
			{
				sender.sendMessage(ChatColor.RED + "Security exception");
				return true;
			}
			
			if(args.length <= 4)
			{
				sender.sendMessage("/ms packet " + args[1] + " " + args[2] + " " + type.name() + " [+|-]");
				return true;
			}
			
			if(args[4].equals("+"))
			{
				MineStrike.getProtocolManager().addPacketListener(new PacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL, type)
				{
					@Override
					public void onPacketReceiving(PacketEvent event)
					{
						PacketContainer packet = event.getPacket();
						StructureModifier<Object> modifier = packet.getModifier();
						List<Field> fields = modifier.getFields();
						
						MineStrike.warn("Receiving packet " + event.getPacketType().name());
						
						for(int i = 0; i < fields.size(); i++)
						{
							Object value = modifier.read(i);
							
							if(value instanceof WatchableObject)
							{
								WatchableObject watchable = (WatchableObject) value;
								
								value = watchable.a() + " " + watchable.c();
							}
							
							MineStrike.warn("  " + fields.get(i).getName() + ": " + value);
						}
					}
					
					@Override
					public void onPacketSending(PacketEvent event)
					{
						PacketContainer packet = event.getPacket();
						StructureModifier<Object> modifier = packet.getModifier();
						List<Field> fields = modifier.getFields();
						
						MineStrike.warn("Sending packet " + event.getPacketType().name());
						
						for(int i = 0; i < fields.size(); i++)
						{
							Object value = modifier.read(i);
							
							if(value instanceof WatchableObject)
							{
								WatchableObject watchable = (WatchableObject) value;
								
								value = watchable.b().toString();
							}
							
							MineStrike.warn("  " + fields.get(i).getName() + ": " + value);
						}
					}
					
					@Override
					public String toString()
					{
						return "PACKET ANALYZER";
					}
				});
				
				sender.sendMessage(ChatColor.GREEN + "Packet listener " + type.name() + " registered");
			}
			else if(args[4].equals("-"))
			{
				for(PacketListener listener : MineStrike.getProtocolManager().getPacketListeners())
					if(listener.toString().equals("PACKET ANALYZER"))
					{
						MineStrike.getProtocolManager().removePacketListener(listener);
						sender.sendMessage(ChatColor.GREEN + "Packet listener " + type.name() + " unregistered");
						break;
					}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Unknown operation");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("case"))
		{
			if(args.length <= 1)
			{
				String cases = "";
				
				for(Case caze : Case.values())
					cases += caze.getId() + ", ";
				
				sender.sendMessage(ChatColor.RED + cases);
				sender.sendMessage("/ms case [Case ID] [Rarity] [Index] (Player) (Drop)");
				return true;
			}
			
			Equipment potentialCase = EquipmentManager.getEquipment(args[1]);
			
			if(potentialCase == null || !(potentialCase.getSource() instanceof Case))
			{
				sender.sendMessage(ChatColor.RED + "The specified equipment isn't a case.");
				return true;
			}
			
			Case caze = (Case) potentialCase;
			
			if(args.length <= 3)
			{
				for(CaseContentRarity rarity : CaseContentRarity.values())
				{
					FilledArrayList<CaseContent> contents = caze.getContents(rarity);
					
					sender.sendMessage(rarity.getColoredName() + ChatColor.RESET + " (" + rarity.name() + "):");
					
					for(int i = 0; i < contents.size(); i++)
					{
						CaseContent content = contents.get(i);
						Equipment equipment = content.getEquipment();
						String equipmentName = equipment.getDisplayName();
						
						sender.sendMessage("  [" + i + "] " + equipmentName);
					}
				}
				
				sender.sendMessage("/ms case [Case ID] [Rarity] [Index] (Player)");
				return true;
			}
			
			CaseContentRarity rarity;
			int index;
			
			try
			{
				rarity = CaseContentRarity.valueOf(args[2].toUpperCase());
				index = Integer.parseInt(args[3]);
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage(ChatColor.RED + "Invalid index: " + args[3]);
				return true;
			}
			catch(IllegalArgumentException e)
			{
				sender.sendMessage(ChatColor.RED + "Invalid rarity: " + args[2]);
				return true;
			}
			
			FilledArrayList<CaseContent> selectedContents = caze.getContents(rarity);
			
			if(index < 0 || index >= selectedContents.size())
			{
				sender.sendMessage(ChatColor.RED + "Index out of bounds.");
				return true;
			}
			
			CaseContent selectedContent = selectedContents.get(index);
			Equipment equipment = selectedContent.getEquipment();
			Player target;
			boolean drop = false;
			
			if(args.length >= 5)
			{
				target = Bukkit.getPlayer(args[4]);
				
				if(target == null)
				{
					sender.sendMessage(ChatColor.RED + "Target " + args[4] + " not found!");
					return true;
				}

				if(args.length >= 6)
					drop = Boolean.parseBoolean(args[5]);
			}
			else if(sender instanceof Player)
				target = (Player) sender;
			else
			{
				sender.sendMessage(ChatColor.RED + "Specify the target player.");
				return true;
			}
			
			MSPlayer msTarget = MSPlayer.get(target);

			if(drop)
			{
				Location eyeLoc = target.getEyeLocation();
				World world = eyeLoc.getWorld();
				ItemStack itemStack = equipment.newItemStack(msTarget);

				world.dropItemNaturally(eyeLoc, itemStack);
				sender.sendMessage(ChatColor.GREEN + "Equipment " + equipment.getDisplayName() + ChatColor.GREEN + " dropped near player " + target.getName() + ".");
			}
			else
			{
				InventoryContainer container = msTarget.getInventoryContainer();

				container.addItem(equipment);
				sender.sendMessage(ChatColor.GREEN + "Equipment " + equipment.getDisplayName() + ChatColor.GREEN + " added to " + target.getName() + "'s inventory.");
			}
		}
		else if(args[0].equalsIgnoreCase("xp"))
		{
			if(args.length <= 2)
			{
				sender.sendMessage("/ms xp [set|add] [Amount] (Player)");
				return true;
			}
			
			int amount;
			Player target;
			
			try
			{
				amount = Integer.parseInt(args[2]);
			}
			catch(Exception e)
			{
				sender.sendMessage(ChatColor.RED + "Invalid amount");
				return true;
			}
			
			if(args.length >= 4)
			{
				target = Bukkit.getPlayer(args[3]);
				
				if(target == null)
				{
					sender.sendMessage(ChatColor.RED + "Player '" + args[3] + "' not found.");
					return true;
				}
			}
			else if(sender instanceof Player)
				target = (Player) sender;
			else
			{
				sender.sendMessage(ChatColor.RED + "Please, select a player.");
				return true;
			}
			
			MSPlayer msTarget = MSPlayer.get(target);
			
			if(args[1].equalsIgnoreCase("set"))
			{
				msTarget.setXP(amount);
				
				sender.sendMessage(target.getName() + "'s XP was set to " + amount + ".");
			}
			else if(args[1].equalsIgnoreCase("add"))
			{
				int result = msTarget.addXP(amount);
				
				sender.sendMessage(target.getName() + "'s XP was increased by " + amount + " to " + result + ".");
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Invalid argument - set/add");
			}
		}
		else if(args[0].equalsIgnoreCase("data"))
		{
			if(args.length <= 1)
			{
				sender.sendMessage("/ms data [save|load|reload] (Player) (Comparison TRUE|false)");
				return true;
			}

			Player target;
			MSPlayer msTarget;

			if(args.length >= 3)
			{
				target = Bukkit.getPlayerExact(args[2]);

				if(target == null || (msTarget = MSPlayer.get(target)) == null)
				{
					sender.sendMessage(ChatColor.RED + "Player " + args[2] + " not found.");
					return true;
				}
			}
			else if(sender instanceof Player)
			{
				target = (Player) sender;
				msTarget = MSPlayer.get(target);

				if(msTarget == null)
				{
					sender.sendMessage(ChatColor.RED + "Player " + args[2] + " not loaded.");
					return true;
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Specify a player, please.");
				return true;
			}

			boolean comparison = true;

			if(args.length >= 4)
				try
				{
					comparison = Boolean.parseBoolean(args[3]);
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Invalid boolean value " + args[3]);
					return true;
				}

			if(args[1].equalsIgnoreCase("save"))
			{
				msTarget.save(comparison);
				sender.sendMessage("Data of player " + target.getName() + " saved.");
			}
			else if(args[1].equalsIgnoreCase("load"))
			{
				msTarget.load(comparison);
				sender.sendMessage("Data of player " + target.getName() + " loaded.");
			}
			else if(args[1].equalsIgnoreCase("reload"))
			{
				msTarget.reload();
				sender.sendMessage("Data of player " + target.getName() + " reloaded.");
			}
			else
			{
				sender.sendMessage("/ms data [save|load|reload] (Player) (Comparison TRUE|false)");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("give"))
		{
			if(args.length <= 1)
			{
				sender.sendMessage("/ms give [Equipment ID] (Player)");
				return true;
			}

			Player player;

			if(args.length >= 3)
			{
				player = Bukkit.getPlayer(args[2]);

				if(player == null)
				{
					sender.sendMessage(ChatColor.RED + "Player '" + args[2] + "' not found!");
					return true;
				}
			}
			else if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Select a player.");
				return true;
			}
			else
				player = (Player) sender;

			Equipment equipment = EquipmentManager.getEquipment(args[1]);

			if(equipment == null)
			{
				sender.sendMessage(ChatColor.RED + "Unknown equipment of id '" + args[1] + "'!");
				return true;
			}

			MSPlayer msPlayer = MSPlayer.get(player);
			InventoryContainer container = msPlayer.getInventoryContainer();

			container.addItem(equipment);
			sender.sendMessage(ChatColor.GREEN + "Equipment '" + equipment.getDisplayName() + ChatColor.GREEN + "' added to " + player.getName() + "'s inventory.");
		}
		else if(args[0].equalsIgnoreCase("game"))
		{
			if(args.length < 2)
			{
				sender.sendMessage("/ms game list");
				sender.sendMessage("/ms game tp [Id]");
				sender.sendMessage("/ms game add [GameType] [Id] [Name]");
				sender.sendMessage("/ms game setup [Id]");
				sender.sendMessage("/ms game remove [Id]");
				sender.sendMessage("/ms game select [Id] ...");
			}
			else if(args[1].equalsIgnoreCase("list"))
			{
				String games = "";

				for(Game game : GameManager.GAMES)
					games += (game.isReadyForSetup() ? ChatColor.GREEN : ChatColor.RED) + game.getName() + " (" + game.getId() + ")" + ChatColor.RESET + ", ";

				sender.sendMessage(games);
			}
			else if(args[1].equalsIgnoreCase("tp") && args.length > 2)
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only.");
					return true;
				}

				Player player = (Player) sender;
				String id = args[2];
				Game game = GameManager.getGame(id);

				if(game == null)
				{
					player.sendMessage(ChatColor.RED + "Game id '" + id + "' not found.");
					return true;
				}

				Structure<? extends GameMap> map = game.getMapStructure();
				Plot<? extends GameMap> plot = map.getPlot();
				Location loc = plot.getLocation();
				World world = loc.getWorld();
				int y = world.getHighestBlockYAt(loc) + 1;

				loc.setY(y);
				player.teleport(loc);
				player.sendMessage(ChatColor.GREEN + "Teleported to game id '" + id + "'.");
			}
			else if(args[1].equalsIgnoreCase("add") && args.length > 4)
			{
				try
				{
					String rawType = args[2];
					GameType type = GameType.valueOf(rawType);
					String id = args[3];
					String name = args[4];

					for(int i = 5; i < args.length; i++)
						name += " " + args[i];

					Game game = type.construct(id, name);

					game.register();
					sender.sendMessage(ChatColor.GREEN + "Game " + ChatColor.YELLOW + name + ChatColor.GREEN + " (id '" + id + "') successfully added.");
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "An error occured: " + e.getMessage());
					e.printStackTrace();
				}
			}
			else if(args[1].equalsIgnoreCase("setup") && args.length > 2)
			{
				String id = args[2];
				Game game = GameManager.getGame(id);

				if(game == null)
				{
					sender.sendMessage(ChatColor.RED + "Game id '" + id + "' not found.");
					return true;
				}

				try
				{
					game.setup();
					sender.sendMessage(ChatColor.GREEN + "Game successfully set up.");
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "An error occured: " + e.getMessage());
					e.printStackTrace();
				}
			}
			else if(args[1].equalsIgnoreCase("remove") && args.length > 2)
			{
				String id = args[2];
				boolean success = GameManager.unregister(id);

				if(success)
					sender.sendMessage(ChatColor.GREEN + "Game id '" + id + "' unregistered.");
				else
					sender.sendMessage(ChatColor.RED + "Game id '" + id + "' not found.");
			}
			else if(args[1].equalsIgnoreCase("select") && args.length > 2)
			{
				String id = args[2];
				Game game = GameManager.getGame(id);

				if(game == null)
				{
					sender.sendMessage(ChatColor.RED + "Game id '" + id + "' not found.");
					return true;
				}

				if(args.length > 3)
					game.handleCommand(sender, args[3], Arrays.copyOfRange(args, 4, args.length));
				else
					game.listCommands(sender);
			}
		}
		else if(args[0].equalsIgnoreCase("scheme"))
		{
			if(args.length < 2)
			{
				sender.sendMessage("/ms scheme list");
				sender.sendMessage("/ms scheme tp");
				sender.sendMessage("/ms scheme add [Type] [Id]");
				sender.sendMessage("/ms scheme remove");
				sender.sendMessage("/ms scheme select [Id] ...");
			}
			else if(args[1].equalsIgnoreCase("list"))
			{
				String schemes = "";

				for(Scheme scheme : SchemeManager.SCHEMES)
					schemes += (scheme.isSetUp() ? ChatColor.GREEN : ChatColor.RED) + scheme.getId() + ChatColor.RESET + ", ";

				sender.sendMessage(schemes);
			}
			else if(args[1].equalsIgnoreCase("tp") && args.length > 2)
			{
				if(!(sender instanceof Player))
					return true;

				String id = args[2];
				Scheme scheme = SchemeManager.getScheme(id);

				if(scheme == null)
				{
					sender.sendMessage(ChatColor.RED + "Scheme '" + id + "' not found.");
					return true;
				}

				Point midpoint = scheme.getRegion().getMidpoint();
				World world = MSConfig.getWorld();
				Block block = midpoint.getBlock(world);

				try
				{
					while(block.getType() != Material.AIR)
						block = block.getRelative(BlockFace.UP);
				}
				catch(Exception e)
				{ /* REACHED THE TOP OF THE WORLD */ }

				Location loc = block.getLocation();
				Player player = (Player) sender;

				player.teleport(loc);
				player.sendMessage(ChatColor.GREEN + "Teleported to scheme '" + id + "'.");
			}
			else if(args[1].equalsIgnoreCase("add") && args.length > 3)
			{
				if(!(sender instanceof Player))
					return true;

				String rawType = args[2];
				SchemeType type;

				try
				{
					type = SchemeType.valueOf(rawType.toUpperCase());
				}
				catch(Exception e)
				{
					String types = "";

					for(SchemeType curType : SchemeType.values())
						types += curType + ", ";

					sender.sendMessage(ChatColor.RED + "SchemeType '" + rawType + "' not found. Those are the available types:");
					sender.sendMessage(types);
					return true;
				}

				String id = args[3];
				Scheme scheme = SchemeManager.getScheme(id);

				if(scheme != null)
				{
					sender.sendMessage(ChatColor.RED + "Scheme '" + id + "' already exists.");
					return true;
				}

				Player player = (Player) sender;
				String playerName = player.getName();
				WorldEdit worldEdit = WorldEdit.getInstance();
				SessionManager sessionManager = worldEdit.getSessionManager();
				LocalSession weSession = sessionManager.findByName(playerName);
				com.sk89q.worldedit.world.World weWorld = weSession.getSelectionWorld();
				RegionSelector selector = weSession.getRegionSelector(weWorld);
				com.sk89q.worldedit.regions.Region weRegion;

				try
				{
					weRegion = selector.getRegion();
				}
				catch(IncompleteRegionException e)
				{
					player.sendMessage(ChatColor.RED + "Incomplete WorldEdit region!");
					return true;
				}

				Region region = Region.valueOf(weRegion);

				if(region.getWidth() > Plot.PLOT_SIZE)
				{
					player.sendMessage("Region is too long [x]! (" + region.getWidth() + "; max " + Plot.PLOT_SIZE + ")");
					return true;
				}
				else if(region.getDepth() > Plot.PLOT_SIZE)
				{
					player.sendMessage("Region is too deep [z]! (" + region.getDepth() + "; max " + Plot.PLOT_SIZE + ")");
					return true;
				}

				try
				{
					scheme = type.newInstance(id, region);
				}
				catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
				{
					sender.sendMessage(ChatColor.RED + "An error occured: " + e.getMessage());
					e.printStackTrace();
					return true;
				}

				SchemeManager.SCHEMES.add(scheme);
				sender.sendMessage(ChatColor.GREEN + "Scheme '" + id + "' successfully added.");
			}
			else if(args[1].equalsIgnoreCase("remove") && args.length > 2)
			{
				String id = args[2];
				Scheme scheme = SchemeManager.getScheme(id);

				if(scheme == null)
				{
					sender.sendMessage(ChatColor.RED + "Scheme '" + id + "' not found.");
					return true;
				}

				SchemeManager.SCHEMES.remove(scheme);
				sender.sendMessage(ChatColor.GREEN + "Scheme '" + id + "' removed.");
			}
			else if(args[1].equalsIgnoreCase("select") && args.length > 2)
			{
				String id = args[2];
				Scheme scheme = SchemeManager.getScheme(id);

				if(scheme == null)
				{
					sender.sendMessage(ChatColor.RED + "Scheme '" + id + "' not found.");
					return true;
				}

				if(args.length < 4)
				{
					ArrayList<SchemeCommandHandler> handlers = scheme.getCommandHandlers();
					String[] output = new String[handlers.size()];
					int i = 0;

					for(SchemeCommandHandler handler : handlers)
						output[i++] = handler.getDisplay();

					sender.sendMessage(output);
				}
				else
				{
					SchemeCommandHandler handler = scheme.getCommandHandler(args[3]);

					if(handler == null)
						sender.sendMessage(ChatColor.RED + "Unknown scheme edit subcommand.");
					else
						handler.execute(sender, scheme, Arrays.copyOfRange(args, 4, args.length));
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Unknown scheme subcommand.");
			}
		}
		
		return true;
	}
}
