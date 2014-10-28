package cz.minestrike.me.limeth.minestrike.equipment;

public enum ClickSound
{
	DEFAULT("buttonclick"), ACCEPT("menu_accept"), BACK("menu_back"), INVALID("menu_invalid");
	
	private final String soundName;
	
	private ClickSound(String soundName)
	{
		this.soundName = soundName;
	}
	
	public String getRelativeName()
	{
		return soundName;
	}
	
	public String getAbsolouteName()
	{
		return "projectsurvive:counterstrike.ui." + soundName;
	}
}
