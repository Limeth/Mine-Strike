package cz.minestrike.me.limeth.minestrike.scene.games;

public enum VoiceSound
{
	AFFIRMATIVE("affirmative"), AGREE("agree"), BLINDED("blinded"),
	BOMBSITE_CLEAR("bombsiteclear"), BOMB_TICKING_DOWN("bombtickingdown"),
	CLEARED_AREA("clearedarea"), COMMANDER_DOWN("commanderdown"),
	COVERING_FRIEND("coveringfriend"), COVER_ME("coverme"),
	BOMB_EXPLODING_CT(Team.COUNTER_TERRORISTS, "ct_bombexploding"),
	DEATH_CT(Team.COUNTER_TERRORISTS, "ct_death"),
	DEATH_T(Team.TERRORISTS, "t_death"),
	DECOY_CT(Team.COUNTER_TERRORISTS, "ct_decoy"),
	DECOY_T(Team.TERRORISTS, "t_decoy"),
	FLASHBANG_CT(Team.COUNTER_TERRORISTS, "ct_flashbang"),
	FLASHBANG_T(Team.TERRORISTS, "t_flashbang"),
	GRENADE_CT(Team.COUNTER_TERRORISTS, "ct_grenade"),
	GRENADE_T(Team.TERRORISTS, "t_grenade"),
	MOLOTOV_CT(Team.COUNTER_TERRORISTS, "ct_molotov"),
	MOLOTOV_T(Team.TERRORISTS, "t_molotov"),
	SMOKE_CT(Team.COUNTER_TERRORISTS, "ct_smoke"),
	SMOKE_T(Team.TERRORISTS, "t_smoke"),
	BOMB_DEFUSING(Team.COUNTER_TERRORISTS, "defusingbomb"),
	DISAGREE("disagree"), ENEMY_DOWN("enemydown"),
	FOLLOWING_FRIEND("followingfriend"), FRIENDLY_FIRE("friendlyfire"),
	GUARDING_LOOSE_BOMB(Team.COUNTER_TERRORISTS, "goingtoguardloosebomb"),
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
	BOMB_PICKED_UP_CT(Team.COUNTER_TERRORISTS, "theypickedupthebomb"),
	THREE_ENEMIES_LEFT("threeenemiesleft"),
	TWO_ENEMIES_LEFT("twoenemiesleft"),
	COVERING_DEFUSER(Team.COUNTER_TERRORISTS, "waitingforhumantodefusebomb"),
	WAITING_HERE("waitinghere"), WHERE_IS_THE_BOMB("whereisthebomb"),
	GOING_TO_GUARD_ESCAPE_ZONE(Team.TERRORISTS, "goingtoguardhostageescapezone"),
	GOING_TO_GUARD_HOSTAGES(Team.TERRORISTS, "goingtoguardhostages"),
	GUARDING_ESCAPE_ZONE(Team.TERRORISTS, "guardinghostageescapezone"),
	GUARDING_HOSTAGES(Team.TERRORISTS, "guardinghostages"),
	;
	
	private final Team team;
	private final String soundName;
	
	private VoiceSound(Team team, String soundName)
	{
		this.team = team;
		this.soundName = soundName;
	}
	
	private VoiceSound(String soundName)
	{
		this(null, soundName);
	}
	
	public boolean isTeamLimited()
	{
		return team != null;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public String getSoundName()
	{
		return soundName;
	}
}
