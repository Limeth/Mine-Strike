package cz.minestrike.me.limeth.minestrike.scene.games.listeners;

import cz.minestrike.me.limeth.minestrike.BlockProperties;
import cz.minestrike.me.limeth.minestrike.BlockPropertiesManager;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.events.BlockShotEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import darkBlade12.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;

import java.util.Map;

/**
 * @author Limeth
 */
public class GameShotMSListener extends SceneMSListener<Game>
{
	public GameShotMSListener(Game scene)
	{
		super(scene);
	}

	@EventHandler
	public void onBlockShot(BlockShotEvent event, MSPlayer msPlayer)
	{
		Block block = event.getBlock();
		BlockProperties properties = BlockPropertiesManager.getProperties(block);

		if(!properties.isBreakable())
			return;

		Game game = getScene();
		Structure<? extends GameMap> structure = game.getMapStructure();
		GameMap scheme = structure.getScheme();
		Location relativeBlockLocation = structure.getRelativeLocation(block.getLocation());
		RegionList spectatorZones = scheme.getSpectatorZones();

		if(spectatorZones.isInside(relativeBlockLocation))
			return;

		double durability = properties.getDurability();
		Map<Block, Double> blockDamageMap = game.getBlockDamageMap();
		Double damagePrevious = blockDamageMap.get(block);
		boolean previouslyShot = damagePrevious != null;
		double damageNext = (damagePrevious == null ? 0 : damagePrevious) + event.getDamage();

		blockDamageMap.put(block, damageNext);

		boolean broken = (damagePrevious == null || damagePrevious < durability) && damageNext >= durability;

		if(broken)
		{
			Location effectLoc = block.getLocation().add(0.5, 0.5, 0.5);
			int id = block.getTypeId();
			byte data = block.getData();

			block.setType(Material.AIR);
			ParticleEffect.displayBlockDust(effectLoc, id, data, 0.5F, 0.5F, 0.5F, 0.1F, 25);
		}
	}
}
