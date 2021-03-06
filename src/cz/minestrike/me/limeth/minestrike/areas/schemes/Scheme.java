package cz.minestrike.me.limeth.minestrike.areas.schemes;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.*;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class Scheme
{
	@Expose private final SchemeType type;
	@Expose private String id;
	@Expose private Region region;
	
	public Scheme(SchemeType type, String id, Region region)
	{
		Validate.notNull(type, "The scheme type cannot be null!");
		Validate.notNull(id, "The ID cannot be null!");
		Validate.notNull(region, "The region cannot be null!");
		
		this.type = type;
		this.id = id;
		this.region = region;
	}
	
	public abstract boolean isSetUp();
	
	public FilledHashMap<String, RegionList> getRegionsLists()
	{
		return new FilledHashMap<>();
	}
	
	public FilledHashMap<String, Point> getPoints()
	{
		return new FilledHashMap<>();
	}
	
	public SchemeCommandHandler getCommandHandler(String command)
	{
		for(SchemeCommandHandler handler : getCommandHandlers())
			if(handler.getCommand().equalsIgnoreCase(command))
				return handler;
		
		return null;
	}
	
	public ArrayList<SchemeCommandHandler> getCommandHandlers()
	{
		ArrayList<SchemeCommandHandler> commands = new ArrayList<SchemeCommandHandler>();
		
		commands.add(COMMAND_MOVE);
		commands.add(COMMAND_REGION_SET);
		commands.add(COMMAND_HIGHLIGHT);
		
		return commands;
	}
	
	private static final SchemeCommandHandler COMMAND_MOVE = new SchemeCommandHandler("move", "ms scheme select [Scheme] move", "moves the selected scheme") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Players only.");
				return;
			}
			
			Player player = (Player) sender;
			Location loc = player.getLocation();
			Point playerPoint = Point.valueOf(loc);
			Region region = scheme.getRegion();
			String id = scheme.getId();
			
			region.moveTo(playerPoint);
			player.sendMessage(ChatColor.GREEN + "Scheme '" + id + "' moved.");
		}
	};
	
	private static final SchemeCommandHandler COMMAND_HIGHLIGHT = new SchemeCommandHandler("highlight", "ms scheme select [Scheme] highlight", "highlights the selected scheme") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Players only.");
				return;
			}
			
			Player player = (Player) sender;
			Region region = scheme.getRegion();
			String id = scheme.getId();
			
			region.highlight(null, player);
			player.sendMessage(ChatColor.GREEN + "Scheme '" + id + "' highlighted.");
		}
	};
	
	private static final SchemeCommandHandler COMMAND_REGION_SET = new SchemeCommandHandler("setRegion", "ms scheme select [Scheme] setRegion", "sets the new scheme region") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Players only.");
				return;
			}
			
			Player player = (Player) sender;
			String playerName = player.getName();
			WorldEdit worldEdit = WorldEdit.getInstance();
			SessionManager sessionManager = worldEdit.getSessionManager();
			LocalSession weSession = sessionManager.findByName(playerName);
			
			if(weSession == null)
			{
				player.sendMessage(ChatColor.RED + "Incomplete WorldEdit region!");
				return;
			}
			
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
				return;
			}
			
			Region region = Region.valueOf(weRegion);
			
			if(region.getWidth() > Plot.PLOT_SIZE)
			{
				player.sendMessage("Region is too long [x]! (" + region.getWidth() + "; max " + Plot.PLOT_SIZE + ")");
				return;
			}
			else if(region.getDepth() > Plot.PLOT_SIZE)
			{
				player.sendMessage("Region is too deep [z]! (" + region.getDepth() + "; max " + Plot.PLOT_SIZE + ")");
				return;
			}
			
			scheme.setRegion(region);
			
			player.sendMessage("Region of scheme '" + scheme.getId() + "' changed!");
		}
	};
	
	@SuppressWarnings("deprecation")
	public void build(World destWorld, Point destPoint)
	{
		CraftWorld world = (CraftWorld) MSConfig.getWorld();
		WorldServer nmsWorld = world.getHandle();
		WorldServer nmsDestWorld = ((CraftWorld) destWorld).getHandle();
		
		for(int x = 0; x < region.getWidth(); x++)
			for(int y = 0; y < region.getHeight(); y++)
				for(int z = 0; z < region.getDepth(); z++)
					build(x, y, z, destPoint, destWorld);
	}

	public void build(int x, int y, int z, Point absDestination, World destinationWorld)
	{
		CraftWorld world = (CraftWorld) MSConfig.getWorld();
		WorldServer nmsWorld = world.getHandle();
		WorldServer nmsDestinationWorld = ((CraftWorld) destinationWorld).getHandle();
		int srcX = region.getLower().getX() + x;
		int srcY = region.getLower().getY() + y;
		int srcZ = region.getLower().getZ() + z;
		int destX = absDestination.getX() + x;
		int destY = absDestination.getY() + y;
		int destZ = absDestination.getZ() + z;
		Block srcBlock = world.getBlockAt(srcX, srcY, srcZ);
		Block destBlock = destinationWorld.getBlockAt(destX, destY, destZ);
		TileEntity srcTileEntity = nmsWorld.getTileEntity(srcX, srcY, srcZ);

		destBlock.setTypeIdAndData(srcBlock.getType().getId(), srcBlock.getData(), false);

		if(srcTileEntity != null)
			try
			{
				TileEntity cloneTileEntity = cloneTileEntity(srcTileEntity);

				nmsDestinationWorld.setTileEntity(destX, destY, destZ, cloneTileEntity);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public void build(Point relativeSource, Point destinationBase, World destinationWorld)
	{
		build(relativeSource.getX(), relativeSource.getY(), relativeSource.getZ(), destinationBase, destinationWorld);
	}
	
	public static TileEntity cloneTileEntity(TileEntity value) throws InstantiationException, IllegalAccessException
	{
		NBTTagCompound compound = new NBTTagCompound();
		Class<? extends TileEntity> clazz = value.getClass();
		TileEntity clone = clazz.newInstance();
		
		value.b(compound);
		clone.a(compound);
		
		return clone;
	}
	
	public void build(Location loc)
	{
		build(loc.getWorld(), Point.valueOf(loc));
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		Validate.notNull(id, "The ID cannot be null!");
		
		this.id = id;
	}
	
	public Point getBase()
	{
		return region.getLower();
	}

	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		Validate.notNull(region, "The region cannot be null!");
		
		this.region = region;
	}

	public <T extends Scheme> StructureMSListener<T> newStructureListener(Structure<T> structure)
	{
		return null;
	}
}
