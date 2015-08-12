package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.util.BukkitRepeater;
import darkBlade12.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

public class BombRepeater extends BukkitRepeater
{
    public static final String SOUND_BEEP_LAST = "projectsurvive:counterstrike.ui.arm_bomb";
    public static final String SOUND_BEEP = "projectsurvive:counterstrike.ui.beep";
    public static final double DELAY_BEEP_MAX = 20;
    public static final double DELAY_BEEP_MIN = 2;
    public static final double DURATION_BEEPING = DefuseRound.PHASE_BOMB.getDuration() - 20;
    private final DefuseGame game;

    public BombRepeater(DefuseGame game)
    {
        this.game = game;
    }

    @Override
    public void start()
    {
        launch();
    }

    @Override
    public void onInterrupt()
    {

    }

    @Override
    public Optional<Double> onTick()
    {
        long totalTicksScheduled = getTotalTicksScheduled();

        if(totalTicksScheduled >= DURATION_BEEPING)
        {
            beepLast();

            return Optional.empty();
        }

        double beepingFraction = 1 - (double) totalTicksScheduled / DURATION_BEEPING;
        double delay = DELAY_BEEP_MIN + (DELAY_BEEP_MAX - DELAY_BEEP_MIN) * beepingFraction;

        if(totalTicksScheduled + delay > DURATION_BEEPING)
            delay = DURATION_BEEPING - totalTicksScheduled;

        beep();

        return Optional.of(delay);
    }

    private void beep()
    {
        playEffectAndSound(SOUND_BEEP, ParticleEffect.RED_DUST, 0, 0, 5);
    }

    private void beepLast()
    {
        playEffectAndSound(SOUND_BEEP_LAST, ParticleEffect.HAPPY_VILLAGER, 0.125F, 1, 15);
    }

    private void playEffectAndSound(String sound, ParticleEffect effect, float effectSpread, float effectSpeed, int effectAmount)
    {
        Block bombBlock = game.getBombBlock();
        Location bombLocation = bombBlock.getLocation().add(0.5, 0.5, 0.5);
        Location effectLocation = bombBlock.getLocation().add(0.5, 1.125, 0.5);
        Set<Player> players = game.getBukkitPlayers();
        Player[] playerArray = players.toArray(new Player[players.size()]);

        game.playSound(sound, bombLocation);
        effect.display(effectLocation, effectSpread, effectSpread, effectSpread, effectSpeed, effectAmount, playerArray);
    }
}
