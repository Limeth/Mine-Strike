package cz.minestrike.me.limeth.minestrike.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.Validate;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;

public class Bytemap
{
	private final Byte[] bytes;
	private final int width, height, area;
	
	private Bytemap(Byte[] bytes, int width, int height, int area)
	{
		this.bytes = bytes;
		this.width = width;
		this.height = height;
		this.area = area;
	}

	@SuppressWarnings("deprecation")
	public static Bytemap fromImage(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int area = width * height;
		Byte[] bytes = new Byte[width * height];
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				int rgb = image.getRGB(x, y);
				Color color = Color.decode(Integer.toString(rgb));
				bytes[x + y * width] = MapPalette.matchColor(color);
			}
		
		return new Bytemap(bytes, width, height, area);
	}
	
	public static Bytemap fromFile(File file) throws IOException
	{
		return fromImage(ImageIO.read(file));
	}
	
	public void draw(MapCanvas canvas, int startX, int startY)
	{
		Validate.notNull(canvas, "The canvas must not be null!");
		
		for(int x = 0; x < width; x++)
		{
			int absX = startX + x;
			
			if(absX < 0 || absX >= RendererUtil.MAP_SIZE)
				continue;
			
			for(int y = 0; y < height; y++)
			{
				int absY = startY + y;
				
				if(absY < 0 || absY >= RendererUtil.MAP_SIZE)
					continue;
				
				int index = toIndex(x, y);
				Byte color = bytes[index];
				
				if(color == null)
					continue;
				
				canvas.setPixel(absX, absY, color);
			}
		}
	}
	
	public int toIndex(int x, int y)
	{
		return x + y * width;
	}
	
	public void draw(MapCanvas canvas)
	{
		draw(canvas, (RendererUtil.MAP_SIZE - width) / 2, (RendererUtil.MAP_SIZE - height) / 2);
	}

	public Byte[] getBytes()
	{
		return bytes;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getArea()
	{
		return area;
	}
}
