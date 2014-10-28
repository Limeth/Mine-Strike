package cz.minestrike.me.limeth.minestrike.scene.games;

import org.apache.commons.lang.Validate;

public enum VoiceSound
{
	AFFIRMATIVE("affirmative"), AGREE("agree"), BLINDED("blinded"),
	BOMBSITE_CLEAR("bombsiteclear"), BOMB_TICKING_DOWN("bombtickingdown"),
	CLEARED_AREA("clearedarea"), COMMANDER_DOWN("commanderdown"),
	COVERING_FRIEND("coveringfriend"), COVER_ME("coverme"),
	BOMB_EXPLODING(null, "ct_bombexploding"),
	DEATH("t_death", "ct_death"),
	DECOY("t_decoy", "ct_decoy"),
	FLASHBANG("t_flashbang", "ct_flashbang"),
	GRENADE("t_grenade", "ct_grenade"),
	MOLOTOV("t_molotov", "ct_molotov"),
	SMOKE("t_smoke", "ct_smoke"),
	BOMB_DEFUSING(null, "defusingbomb"),
	DISAGREE("disagree"), ENEMY_DOWN("enemydown"),
	FOLLOWING_FRIEND("followingfriend"), FRIENDLY_FIRE("friendlyfire"),
	GUARDING_LOOSE_BOMB(null, "goingtoguardloosebomb"),
	HEARD_NOISE("heardnoise"), HELP("help"), IN_COMBAT("incombat"),
	IN_POSITION("inposition"), KILLED_FRIEND("killedfriend"),
	LAST_MAN_STANDING("lastmanstanding"),
	ENEMY_LOST("lostenemy"), NEGATIVE("negative"),
	NICE_SHOT("niceshot"), NO_ENEMIES_LEFT("noenemiesleft"),
	NO_ENEMIES_LEFT_BOMB("noenemiesleftbomb"),
	ON_A_ROLL_BRAG("onarollbrag"), ONE_ENEMY_LEFT("oneenemyleft"),
	ON_MY_WAY("onmyway"), PEP_TALK("peptalk"),
	PINNED_DOWN("pinneddown"), BOMB_PLANTED_SAFE("plantedbombplacesafe"),
	ENEMY_SPOTTED("radio_enemyspotted"), FOLLOW_ME("radio_followme"),
	LETS_GO("radio_letsgo"), LOCK_AND_LOAD("radio_locknload"),
	NEED_BACKUP("radio_needbackup"), TAKING_FIRE("radio_takingfire"),
	REPORTING_IN("reportingin"), REPORT_REQUEST("requestreport"),
	SCARED_EMOTE("scaredemote"), SNIPER_KILLED("sniperkilled"),
	SNIPER_WARNING("sniperwarning"), SPOTTED_BOMBER("spottedbomber"),
	SPOTTED_LOOSE_BOMB("spottedloosebomb"), THANKS("thanks"),
	BOMB_PICKED_UP(null, "theypickedupthebomb"),
	THREE_ENEMIES_LEFT("threeenemiesleft"),
	TWO_ENEMIES_LEFT("twoenemiesleft"),
	COVERING_DEFUSER(null, "waitingforhumantodefusebomb"),
	WAITING_HERE("waitinghere"), WHERE_IS_THE_BOMB("whereisthebomb"),
	GOING_TO_GUARD_ESCAPE_ZONE("goingtoguardhostageescapezone", null),
	GOING_TO_GUARD_HOSTAGES("goingtoguardhostages", null),
	GUARDING_ESCAPE_ZONE("guardinghostageescapezone", null),
	GUARDING_HOSTAGES("guardinghostages", null);
	
	private final String tSoundName;
	private final String ctSoundName;
	
	private VoiceSound(String tSoundName, String ctSoundName)
	{
		if(tSoundName == null && ctSoundName == null)
			throw new IllegalArgumentException("Both names cannot be null at the same time!");
		
		this.tSoundName = tSoundName;
		this.ctSoundName = ctSoundName;
	}
	
	private VoiceSound(String soundName)
	{
		this(soundName, soundName);
	}
	
	public String getRelativeName(Team team)
	{
		Validate.notNull(team, "The team must not be null!");
		
		if(team == Team.TERRORISTS)
			return tSoundName;
		else if(team == Team.COUNTER_TERRORISTS)
			return ctSoundName;
		else
			return null;
	}
	
	public String getAbsoluteName(Team team)
	{
		return team.getVoiceDirectory() + getRelativeName(team);
	}
	
	public boolean isAvailable(Team team)
	{
		return getRelativeName(team) != null;
	}
}
