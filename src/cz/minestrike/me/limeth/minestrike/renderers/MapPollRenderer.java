package cz.minestrike.me.limeth.minestrike.renderers;

import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.util.Bytemap;

public class MapPollRenderer extends MapRenderer
{
	public static final File DIRECTORY = new File("plugins/MineStrike/data/maps");
	private Bytemap background;
	private String label;
	
	public MapPollRenderer(Bytemap background, String label)
	{
		this.background = background;
		this.label = label;
	}
	
	public static MapPollRenderer forGameMap(GameMap map) throws IOException
	{
		String name = map.getName();
		File backgroundFile = new File(DIRECTORY, name + ".png");
		Bytemap background = null;
		
		if(backgroundFile.isFile())
			background = Bytemap.fromFile(backgroundFile);
		
		return new MapPollRenderer(background, name);
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		if(background != null)
			background.draw(canvas);
		
		if(label != null)
			canvas.drawText(2, 2, MinecraftFont.Font, label);
	}

	public Bytemap getBackgroundBytemap()
	{
		return background;
	}

	public void setBackgroundBytemap(Bytemap backgroundBytemap)
	{
		this.background = backgroundBytemap;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
