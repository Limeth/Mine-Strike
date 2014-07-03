package cz.minestrike.me.limeth.minestrike.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
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
				Color color = Color.fromRGB(image.getRGB(x, y));
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				
				bytes[x + y * width] = MapPalette.matchColor(r, g, b);
			}
		
		return new Bytemap(bytes, width, height, area);
	}
	
	public static Bytemap fromFile(File file) throws IOException
	{
		return fromImage(ImageIO.read(file));
	}
	
	public void draw(MapCanvas canvas, int absX, int absY)
	{
		Validate.notNull(canvas, "The canvas must not be null!");
		
		for(int x = 0; x < width; x++)
		{
			int relX = absX + x;
			
			if(relX < 0 || relX >= RendererUtil.MAP_SIZE)
				continue;
			
			for(int y = 0; y < height; y++)
			{
				int relY = absY + y;
				
				if(relY < 0 || relY >= RendererUtil.MAP_SIZE)
					continue;
				
				int index = toIndex(x, y);
				Byte color = bytes[index];
				
				if(color == null)
					continue;
				
				canvas.setPixel(relX, relY, color);
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
