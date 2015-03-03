package cz.minestrike.me.limeth.minestrike.util;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.function.Supplier;

/**
 * @author Limeth
 */
public class SoundSequence
{
	private FilledArrayList<String> paths  = new FilledArrayList<>();
	private FilledArrayList<Float>  delays = new FilledArrayList<>();

	private SoundSequence(FilledArrayList<String> paths, FilledArrayList<Float> delays)
	{
		this.paths = paths;
		this.delays = delays;
	}

	public SoundSequencePlayer play(Supplier<Iterable<Player>> playerSupplier)
	{
		return new SoundSequencePlayer(this, playerSupplier).play();
	}

	public static Builder builder(String firstPath)
	{
		return new Builder(firstPath);
	}

	private static class Builder
	{
		private FilledArrayList<String> paths  = new FilledArrayList<>();
		private FilledArrayList<Float>  delays = new FilledArrayList<>();
		private String pathBase;

		public Builder(String pathBase, String firstSoundPath)
		{
			Preconditions.checkNotNull(firstSoundPath);
			Preconditions.checkArgument(firstSoundPath.length() > 0);

			this.pathBase = pathBase != null ? pathBase : "";

			paths.add(firstSoundPath);
		}

		public Builder(String firstSoundPath)
		{
			this("", firstSoundPath);
		}

		public Builder append(float delay, String path)
		{
			Preconditions.checkArgument(delay >= 0);
			Preconditions.checkNotNull(path);
			Preconditions.checkArgument(path.length() > 0);

			delays.add(delay);
			paths.add(path);

			return this;
		}

		public SoundSequence build()
		{
			return new SoundSequence(paths, delays);
		}
	}

	private static class SoundSequencePlayer
	{
		private final SoundSequence soundSequence;
		private Supplier<Iterable<Player>> playerSupplier;
		private float remainingDelay;
		private int currentIndex;

		public SoundSequencePlayer(SoundSequence soundSequence, Supplier<Iterable<Player>> playerSupplier)
		{
			this.soundSequence = soundSequence;
			this.playerSupplier = playerSupplier;
		}

		public SoundSequencePlayer play()
		{
			int soundAmount = soundSequence.paths.size();
			String sound = soundSequence.paths.get(currentIndex);

			SoundManager.play(sound, playerSupplier.get());

			if(currentIndex < soundAmount - 1)
			{
				float delayPrecise = soundSequence.delays.get(currentIndex) * 20 + remainingDelay;
				int delay = (int) Math.floor(delayPrecise);
				remainingDelay = delayPrecise - delay;
				BukkitScheduler scheduler = Bukkit.getScheduler();
				MineStrike plugin = MineStrike.getInstance();

				scheduler.scheduleSyncDelayedTask(plugin, this::play, delay);
			}

			currentIndex++;

			return this;
		}
	}
}
