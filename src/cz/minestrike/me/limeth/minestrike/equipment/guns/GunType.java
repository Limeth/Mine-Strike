package cz.minestrike.me.limeth.minestrike.equipment.guns;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.guns.extensions.BurstFireExtension;
import cz.minestrike.me.limeth.minestrike.equipment.guns.extensions.GunExtension;
import cz.minestrike.me.limeth.minestrike.equipment.guns.extensions.ScopeExtension;
import cz.minestrike.me.limeth.minestrike.equipment.guns.extensions.SilencableExtension;
import cz.minestrike.me.limeth.minestrike.equipment.guns.extensions.ZoomExtension;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;


public enum GunType implements Equipment
{
	//{{Pistols
	DEAGLE(null, "Desert Eagle", "deagle", false, false, 2.3F, 1.864F, 63, 0.81F, 0.225F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 230, 7, 35, 800, 4096, false, 0.4F, 0.55F, 2, 3.78F, 7.7F, 72.23F, 48.1F, 1.966F, 0.73F, 152, 0.449927F, 0.8112F, 60, 48.2F, 18, 1),
	ELITE(null, "Beretta Elite", "elite", false, false, 3.82F, 1.05F, 38, 0.75F, 0.12F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 30, 120, 700, 4096, false, 0.5F, 0.65F, 2, 5.25F, 7, 11.16F, 17.85F, 0.849F, 0.255F, 102, 0.437491F, 0.524989F, 20, 27, 4, 1),
	FIVESEVEN(null, "Five-SeveN", "fiveseven", false, false, 2.3F, 1.823F, 32, 0.81F, 0.15F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 20, 100, 500, 4096, false, 0.5F, 0.65F, 2, 6.83F, 9.1F, 32.45F, 13.41F, 0.633F, 0.19F, 138, 0.273844F, 0.332613F, 5, 25, 4, 1),
	GLOCK(BurstFireExtension.class, "Glock-18", "glock18", false, false, 2.3F, 0.94F, 28, 0.9F, 0.15F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 20, 120, 200, 4096, false, 0.5F, 0.65F, 2, 4.2F, 5.6F, 56, 12, 0.616F, 0.185F, 137, 0.27631F, 0.331572F, 20, 18, 0, 1, false, 15F, 3F, 5.6F, 45F, 12.95F, 0.15F, 0.185F, 119.25F, 20, 30F, 5, 3), //TODO burst delay
	P2000(null, "P2000", "hkp2000", false, false, 2.3F, 1.01F, 35, 0.91F, 0.17F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 13, 52, 200, 4096, false, 0.5F, 0.65F, 2, 3.68F, 4.9F, 50, 13, 0.638F, 0.191F, 138.32F, 0.291277F, 0.349532F, 0, 26, 0, 1, false, 1.5F, 3.68F, 4.9F, 13.15F, 13.87F, 0.66F, 0.198F, 119.9F, 0, 0F, 0, 1),
	USP_S(SilencableExtension.class, "USP-S", "usp", false, false, 2.3F /*TODO*/, 1.01F, 35, 0.91F, 0.17F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 12, 24, 200, 4096, false, 0.5F, 0.65F, 2.5F, 3.68F, 4.9F, 71, 13.87F, 0.638F, 0.191F, 138.32F, 0.291277F, 0.349532F, 0, 29, 0, 1, false, 1.5F, 3.68F, 4.9F, 52F, 13.87F, 0.66F, 0.198F, 119.9F, 0, 23F, 0, 1),
	P250(null, "P250", "p250", false, false, 2.3F, 1.553F, 35, 0.85F, 0.15F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 13, 52, 300, 4096, false, 0.5F, 0.65F, 2, 6.83F, 9.1F, 52.45F, 13.41F, 0.633F, 0.190F, 138, 0.287823F, 0.345388F, 10, 26, 3, 1),
	CZ75(null, "CZ75-Auto", "cz75a", false, false, 2.3F /*TODO*/, 1.553F, 35, 0.85F, 0.1F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 12, 12, 300, 4096, true, 0.5F, 0.65F, 3, 6.83F, 9.1F, 25, 13.41F, 0.633F, 0.19F, 138, 0.287823F, 0.345388F, 180, 25, 10, 1),
	TEC9(null, "Tec-9", "tec9", false, false, 2.7F, 1.812F, 33, 0.831F, 0.12F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 32, 120, 500, 4096, false, 0.5F, 0.65F, 2, 8.57F, 11.43F, 52.88F, 13.81F, 0.504F, 0.211F, 120.6F, 0.322362F, 0.386834F, 60, 29, 3, 1),
	//}}Pistols
	//{{Shotguns
	MAG7(null, "MAG-7", "mag7", true, false, 2.5F, 1.5F, 28, 0.45F, 0.85F, 0, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 225, 5, 32, 1800, 1500, true, 0.4F, 0.45F, 40, 5.25F, 7, 11.19F, 15.99F, 0.343F, 0.103F, 134.26F, 0.285521F, 0.399729F, 20, 165, 25, 9),
	NOVA(null, "Nova", "nova", true, true, 2F /*TODO*/, 1, 26, 0.7F, 0.88F, 0, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 220, 8, 32, 1200, 3000, true, 0.4F, 0.45F, 40, 5.25F, 7, 9.72F, 36.75F, 0.788F, 0.236F, 78.75F, 0.328941F, 0.460517F, 20, 143, 22, 9),
	SAWEDOFF(null, "Sawed-Off", "sawedoff", true, true, 2F /*TODO*/, 1.5F, 30, 0.45F, 0.85F, 1, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 210, 7, 32, 1200, 750, false, 0.4F, 0.45F, 60, 5.25F, 7, 9.72F, 16.8F, 0.36F, 0.108F, 36, 0.328941F, 0.460517F, 20, 143, 22, 9),
	XM1014(null, "XM1014", "xm1014", true, true, 2F /*TODO*/, 1.6F, 20, 0.7F, 0.35F, 0, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 215, 7, 32, 2200, 3000, true, 0.4F, 0.45F, 40, 5.25F, 7, 8.83F, 36.03F, 0.772F, 0.232F, 77.21F, 0.361835F, 0.506569F, 20, 80, 20, 6),
	//}}Shotguns
	//{{SMGs
	BIZON(null, "PP-Bizon", "bizon", true, false, 2.5F, 0.950F, 27, 0.800F, 0.080F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 240, 64, 120, 1400, 3600, true, 0.45F, 0.60F, 1.00F, 10.50F, 14.00F, 2.88F, 27.57F, 0.265F, 0.080F, 169.650F, 0.236837F, 0.331572F, 70, 18F, 1, 1),
	MAC10(null, "MAC-10", "mac10", true, false, 2.6F, 0.950F, 29, 0.800F, 0.075F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 240, 30, 100, 1050, 3600, true, 0.45F, 0.60F, 1.00F, 9.98F, 13.30F, 2.76F, 24.99F, 0.228F, 0.069F, 34.260F, 0.285521F, 0.399729F, 70, 18F, 1, 1),
	MP7(null, "MP7", "mp7", true, false, 3.15F, 1.050F, 29, 0.850F, 0.080F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 220, 30, 120, 1700, 3600, true, 0.45F, 0.60F, 1.00F, 9.02F, 12.03F, 2.18F, 39.86F, 0.384F, 0.115F, 57.560F, 0.312494F, 0.437491F, 70, 16F, 1, 1),
	MP9(null, "MP9", "mp9", true, false, 2.25F, 1.000F, 26, 0.830F, 0.070F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 240, 30, 120, 1250, 3600, true, 0.45F, 0.60F, 0.75F, 10.50F, 14.00F, 3.70F, 19.04F, 0.186F, 0.056F, 148.913F, 0.184207F, 0.257890F, 70, 19F, 1, 1),
	P90(null, "P90", "p90", true, false, 3.4F, 1.300F, 26, 0.860F, 0.070F, 1, MoneyAward.KILL_P90.getAmount(), MoneyAward.KILL_P90.getAmount(), 230, 50, 100, 2350, 3700, true, 0.45F, 0.60F, 1.00F, 10.24F, 13.65F, 2.85F, 31.00F, 0.650F, 0.082F, 132.170F, 0.265784F, 0.372098F, 70, 16F, 1, 1),
	UMP45(null, "UMP-45", "ump45", true, false, 3.5F, 1.100F, 35, 0.850F, 0.090F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 230, 25, 100, 1200, 3700, true, 0.45F, 0.60F, 1.00F, 10.07F, 13.43F, 3.42F, 28.76F, 0.282F, 0.085F, 42.350F, 0.249995F, 0.349993F, 40, 23F, 1, 1),
	//}}SMGs
	//{{Automatic Rifles
	AK_47(null, "AK-47", "ak47", true, false, 2.5F, 1.55F, 36, 0.98F, 0.1F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 30, 90, 2700, 8192, true, 0.40F, 0.55F, 0.6F, 4.81F, 6.41F, 7.8F, 175.06F, 0.807F, 0.242F, 140F, 0.381571F, 0.46F, 70, 30, 0, 1),
	AUG(ZoomExtension.class, "AUG", "aug", true, false, 3.8F, 1.800F, 28, 0.980F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 220, 30, 90, 3300, 8192, true, 0.40F, 0.55F, 0.50F, 2.88F, 3.85F, 6.16F, 135.45F, 0.693F, 0.208F, 110.040F, 0.305520F, 0.429727F, 60, 26F, 0, 1, true, 0.30F, 1.01F, 2.12F, 6.16F, 105.45F, 0.693F, 0.208F, 100.040F, 0, 18F, 0, 1),
	FAMAS(BurstFireExtension.class, "FAMAS", "famas", true, false, 3.35F, 1.400F, 30, 0.960F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 220, 25, 90, 2250, 8192, true, 0.40F, 0.55F, 0.60F, 7.39F, 9.85F, 6.70F, 99.34F, 0.685F, 0.205F, 118.716F, 0.336177F, 0.470648F, 60, 20F, 1, 1, false, 0.60F, 3.95F, 3.69F, 3.35F, 99.34F, 0.685F, 0.205F, 118.716F, 50, 20F, 1, 3), //TODO burst delay
	GALIL_AR(null, "Galil AR", "galilar", true, false, 3.05F, 1.550F, 30, 0.980F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 35, 90, 2000, 8192, true, 0.40F, 0.55F, 0.60F, 6.58F, 8.77F, 8.78F, 123.56F, 0.852F, 0.256F, 113.580F, 0.384861F, 0.538805F, 70, 21F, 1, 1, true, 0.60F, 4.84F, 7.78F, 5.85F, 106.50F, 0.852F, 0.256F, 113.580F, 0, 0F, 0, 1),
	M4A4(null, "M4A4", "m4a1", true, false, 3.15F, 1.400F, 33, 0.970F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 225, 30, 90, 3100, 8192, true, 0.40F, 0.55F, 0.60F, 3.68F, 4.90F, 7.00F, 137.88F, 0.640F, 0.192F, 110.994F, 0.302625F, 0.423676F, 70, 23F, 0, 1, true, 0.45F, 3.68F, 4.90F, 6.34F, 122.00F, 0.656F, 0.197F, 113.672F, 0, 0F, 0, 1),
	M4A1_S(SilencableExtension.class, "M4A1-S", "m4a1", true, false, 3.15F, 1.400F, 33, 0.990F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 225, 20, 40, 2900, 8192, true, 0.40F, 0.55F, 0.60F, 3.68F, 4.90F, 12.00F, 92.88F, 0.656F, 0.197F, 110.994F, 0.302625F, 0.423676F, 65, 25F, 3, 1, true, 0.45F, 3.68F, 4.90F, 7.00F, 122.00F, 0.656F, 0.197F, 113.672F, 65, 21F, 0, 1),
	SG_556(ZoomExtension.class, "SG 556", "sg556", true, false, 2.8F, 2.000F, 30, 0.980F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 210, 30, 90, 3000, 8192, true, 0.40F, 0.55F, 0.50F, 2.84F, 3.78F, 6.68F, 136.01F, 0.627F, 0.188F, 83.660F, 0.379204F, 0.452886F, 60, 28F, 2, 1, true, 0.30F, 1.04F, 2.18F, 6.68F, 136.01F, 0.627F, 0.188F, 138.758F, 0, 19F, 0, 1),
	//}}Automatic Rifles
	//{{LMGs
	M249(null, "M249", "m249", true, false, 5.73F, 1.600F, 32, 0.970F, 0.080F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 195, 100, 200, 5200, 8192, true, 0.40F, 0.55F, 2.00F, 5.34F, 7.70F, 3.56F, 156.25F, 1.328F, 0.398F, 132.810F, 0.592093F, 0.828931F, 50, 25F, 2, 1),
	NEGEV(null, "Negev", "negev", true, false, 5.73F, 1.500F, 35, 0.970F, 0.060F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 195, 150, 200, 5700, 8192, true, 0.40F, 0.55F, 2.00F, 7.63F, 10.17F, 3.37F, 159.14F, 1.364F, 0.409F, 136.430F, 0.624987F, 0.874982F, 50, 22F, 2, 1),
	//}}LMGs
	//{{Sniper Rifles
	AWP(ScopeExtension.class, "AWP", "awp", true, false, 3.7F, 1.950F, 115, 0.990F, 1.455F, 3, MoneyAward.KILL_AWP.getAmount(), MoneyAward.KILL_AWP.getAmount(), 200, 10, 30, 4750, 8192, false, 0.35F, 0.40F, 0.20F, 60.60F, 80.80F, 53.85F, 176.48F, 1.024F, 0.307F, 136.500F, 0.246710F, 0.345390F, 20, 78F, 15, 1, false, 0.20F, 1.50F, 2.00F, 53.85F, 176.48F, 1.024F, 0.100F, 136.500F, 0, 0F, 0, 1),
	G3SG1(ScopeExtension.class, "G3SG/1", "g3sg1", true, false, 4.65F, 1.650F, 80, 0.980F, 0.250F, 3, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 20, 90, 5000, 8192, true, 0.50F, 0.65F, 0.30F, 19.35F, 25.80F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0.388808F, 0.544331F, 30, 30F, 4, 1, true, 0.30F, 1.50F, 2.00F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0, 0F, 0, 1),
	SCAR_20(ScopeExtension.class, "SCAR-20", "scar20", true, false, 3.11F, 1.650F, 80, 0.980F, 0.250F, 3, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 20, 90, 5000, 8192, true, 0.35F, 0.40F, 0.30F, 19.35F, 25.80F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0.388808F, 0.544331F, 30, 31F, 4, 1, true, 0.30F, 1.50F, 2.00F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0, 0F, 0, 1),
	SSG_08(ScopeExtension.class, "SSG 08", "ssg08", true, false, 3.6F, 1.700F, 88, 0.980F, 1.250F, 3, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 230, 10, 90, 2000, 8192, false, 0.35F, 0.40F, 0.30F, 23.78F, 31.70F, 22.92F, 123.45F, 0.716F, 0.215F, 95.490F, 0.055783F, 0.142096F, 20, 33F, 15, 1, false, 0.30F, 3.00F, 4.00F, 22.92F, 123.45F, 0.716F, 0.215F, 95.490F, 0, 0F, 0, 1),
	//}}Sniper Rifles
	;
	
	private final Class<? extends GunExtension> extensionClass;
	private final String name, directoryName;
	private final boolean primary, loadingContinuously, automatic, secondMode;
	private final int killAwardCompetitive, killAwardCasual, penetration, clipSize, spareCapacity, price, range, bullets;
	private final float reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, movementSpeed, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak,
						inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand,
						recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, armorReducedDamage, armorReduction, armorAbsorptionCost;
	private final Float spreadAlt, inaccuracySneakAlt, inaccuracyStandAlt, inaccuracyFireAlt, inaccuracyMoveAlt, inaccuracyJumpAlt, inaccuracyLandAlt, inaccuracyLadderAlt,
						recoilMagnitudeAlt;
	private final Integer recoilAngleVarianceAlt, recoilMagnitudeVarianceAlt, bulletsAlt;
	private final Boolean automaticAlt;
	
	private GunType(Class<? extends GunExtension> extensionClass, String name, String directoryName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed,
			int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread,
			float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder,
			float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets,
			Boolean automaticAlt, Float spreadAlt, Float inaccuracySneakAlt, Float inaccuracyStandAlt, Float inaccuracyFireAlt, Float inaccuracyMoveAlt, Float inaccuracyJumpAlt,
			Float inaccuracyLandAlt, Float inaccuracyLadderAlt, Integer recoilAngleVarianceAlt, Float recoilMagnitudeAlt, Integer recoilMagnitudeVarianceAlt, Integer bulletsAlt)
	{
		this.extensionClass = extensionClass != null ? extensionClass : GunExtension.class;
		this.name = name;
		this.directoryName = directoryName;
		this.primary = primary;
		this.loadingContinuously = loadingContinuously;
		this.reloadTime = reloadTime * 20F;
		this.weaponArmorRatio = weaponArmorRatio / 2F;
		this.damage = damage * 20 / MSConstant.CS_MAX_HEALTH;
		this.armorReducedDamage = this.damage * weaponArmorRatio;
		this.armorReduction = 1F - weaponArmorRatio;
		this.armorAbsorptionCost = armorReduction / 2F;
		this.rangeModifier = rangeModifier * MSConstant.CS_UNITS_TO_METERS_MODIFIER;
		this.cycleTime = cycleTime * 20F;
		this.penetration = penetration;
		this.killAwardCompetitive = killAwardCompetitive;
		this.killAwardCasual = killAwardCasual;
		this.movementSpeed = movementSpeed * MSConstant.CS_UNITS_TO_METERS_PER_TICK_MODIFIER;
		this.clipSize = clipSize;
		this.spareCapacity = spareCapacity;
		this.price = price;
		this.range = (int) (range * MSConstant.CS_UNITS_TO_METERS_MODIFIER);
		this.automatic = automatic;
		this.flinchVelocityModifierLarge = flinchVelocityModifierLarge;
		this.flinchVelocityModifierSmall = flinchVelocityModifierSmall;
		this.spread = spread;
		this.inaccuracySneak = inaccuracySneak;
		this.inaccuracyStand = inaccuracyStand;
		this.inaccuracyFire = inaccuracyFire;
		this.inaccuracyMove = inaccuracyMove;
		this.inaccuracyJump = inaccuracyJump;
		this.inaccuracyLand = inaccuracyLand;
		this.inaccuracyLadder = inaccuracyLadder;
		this.recoveryTimeSneak = recoveryTimeSneak;
		this.recoveryTimeStand = recoveryTimeStand;
		this.recoilAngleVariance = recoilAngleVariance;
		this.recoilMagnitude = recoilMagnitude;
		this.recoilMagnitudeVariance = recoilAngleVariance;
		this.bullets = bullets;
		
		if(spreadAlt == null || inaccuracySneakAlt == null || inaccuracyStandAlt == null || inaccuracyFireAlt == null || inaccuracyMoveAlt == null || inaccuracyJumpAlt == null
				|| inaccuracyLandAlt == null || inaccuracyLadderAlt == null || recoilAngleVarianceAlt == null || recoilMagnitudeAlt == null || recoilMagnitudeVarianceAlt == null || bulletsAlt == null)
		{
			secondMode = false;
			this.spreadAlt = this.inaccuracySneakAlt = this.inaccuracyStandAlt = this.inaccuracyFireAlt = this.inaccuracyMoveAlt = this.inaccuracyJumpAlt = this.inaccuracyLandAlt
					= this.inaccuracyLadderAlt = this.recoilMagnitudeAlt = null;
			this.recoilAngleVarianceAlt = this.recoilMagnitudeVarianceAlt = this.bulletsAlt = null;
			this.automaticAlt = null;
		}
		else
		{
			secondMode = true;
			this.automaticAlt = automaticAlt;
			this.spreadAlt = spreadAlt;
			this.inaccuracySneakAlt = inaccuracySneakAlt;
			this.inaccuracyStandAlt = inaccuracyStandAlt;
			this.inaccuracyFireAlt = inaccuracyFireAlt;
			this.inaccuracyMoveAlt = inaccuracyMoveAlt;
			this.inaccuracyJumpAlt = inaccuracyJumpAlt;
			this.inaccuracyLandAlt = inaccuracyLandAlt;
			this.inaccuracyLadderAlt = inaccuracyLadderAlt;
			this.recoilMagnitudeAlt = recoilMagnitudeAlt;
			this.recoilAngleVarianceAlt = recoilAngleVarianceAlt;
			this.recoilMagnitudeVarianceAlt = recoilMagnitudeVarianceAlt;
			this.bulletsAlt = bulletsAlt;
		}
	}

	private GunType(Class<? extends GunExtension> extensionClass, String name, String directoryName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int playerSpeedModifier,
			int clipSize, int clipAmount, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread,
			float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder,
			float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets)
	{
		this(extensionClass, name, directoryName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, playerSpeedModifier,
				clipSize, clipAmount, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread,
				inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder,
				recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null);
	}
	
	public boolean isSecondMode()
	{
		return secondMode;
	}

	public int getPenetration()
	{
		return penetration;
	}

	public int getKillAward(boolean competitive)
	{
		return competitive ? killAwardCompetitive : killAwardCasual;
	}

	public int getPrice()
	{
		return price;
	}

	public int getRange()
	{
		return range;
	}

	public int getBullets()
	{
		return bullets;
	}

	public float getWeaponArmorRatio()
	{
		return weaponArmorRatio;
	}

	public float getDamage()
	{
		return damage;
	}

	public float getRangeModifier()
	{
		return rangeModifier;
	}

	public float getCycleTime()
	{
		return cycleTime;
	}

	public float getFlinchVelocityModifierLarge()
	{
		return flinchVelocityModifierLarge;
	}

	public float getFlinchVelocityModifierSmall()
	{
		return flinchVelocityModifierSmall;
	}

	public float getSpread()
	{
		return spread;
	}

	public float getInaccuracySneak()
	{
		return inaccuracySneak;
	}

	public float getInaccuracyStand()
	{
		return inaccuracyStand;
	}

	public float getInaccuracyFire()
	{
		return inaccuracyFire;
	}

	public float getInaccuracyMove()
	{
		return inaccuracyMove;
	}

	public float getInaccuracyJump()
	{
		return inaccuracyJump;
	}

	public float getInaccuracyLand()
	{
		return inaccuracyLand;
	}

	public float getInaccuracyLadder()
	{
		return inaccuracyLadder;
	}

	public float getRecoveryTimeSneak()
	{
		return recoveryTimeSneak;
	}

	public float getRecoveryTimeStand()
	{
		return recoveryTimeStand;
	}

	public float getRecoilAngleVariance()
	{
		return recoilAngleVariance;
	}

	public float getRecoilMagnitude()
	{
		return recoilMagnitude;
	}

	public float getRecoilMagnitudeVariance()
	{
		return recoilMagnitudeVariance;
	}

	public Float getSpreadAlt()
	{
		return spreadAlt;
	}

	public Float getInaccuracySneakAlt()
	{
		return inaccuracySneakAlt;
	}

	public Float getInaccuracyStandAlt()
	{
		return inaccuracyStandAlt;
	}

	public Float getInaccuracyFireAlt()
	{
		return inaccuracyFireAlt;
	}

	public Float getInaccuracyMoveAlt()
	{
		return inaccuracyMoveAlt;
	}

	public Float getInaccuracyJumpAlt()
	{
		return inaccuracyJumpAlt;
	}

	public Float getInaccuracyLandAlt()
	{
		return inaccuracyLandAlt;
	}

	public Float getInaccuracyLadderAlt()
	{
		return inaccuracyLadderAlt;
	}

	public Float getRecoilMagnitudeAlt()
	{
		return recoilMagnitudeAlt;
	}

	public Integer getRecoilAngleVarianceAlt()
	{
		return recoilAngleVarianceAlt;
	}

	public Integer getRecoilMagnitudeVarianceAlt()
	{
		return recoilMagnitudeVarianceAlt;
	}

	public Integer getBulletsAlt()
	{
		return bulletsAlt;
	}
	
	public int getTotalCapacity()
	{
		return clipSize + spareCapacity;
	}

	public String getName()
	{
		return name;
	}

	public int getClipSize()
	{
		return clipSize;
	}

	public int getSpareCapacity()
	{
		return spareCapacity;
	}

	public boolean isPrimary()
	{
		return primary;
	}

	public boolean isAutomatic()
	{
		return automatic;
	}

	public float getReloadTime()
	{
		return reloadTime;
	}

	public boolean isLoadingContinuously()
	{
		return loadingContinuously;
	}

	public int getKillAwardCompetitive()
	{
		return killAwardCompetitive;
	}

	public int getKillAwardCasual()
	{
		return killAwardCasual;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		return new Gun(this).newItemStack(msPlayer);
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		return price;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return movementSpeed;
	}

	@Override
	public String getDisplayName()
	{
		return name;
	}
	
	public String getId()
	{
		return "GUN_" + name();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends CustomizedEquipment> getEquipmentClass()
	{
		return Gun.class;
	}
	
	@Override
	public Equipment getSource()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		return getId();
	}

	public float getArmorReducedDamage()
	{
		return armorReducedDamage;
	}

	public float getArmorReduction()
	{
		return armorReduction;
	}

	public float getArmorAbsorptionCost()
	{
		return armorAbsorptionCost;
	}

	public String getDirectoryName()
	{
		return directoryName;
	}
	
	public Boolean isAutomaticAlt()
	{
		return automaticAlt;
	}
	
	public String getSoundShooting()
	{
		return "projectsurvive:counterstrike.weapons." + directoryName + "." + directoryName;
	}
	
	public String getSoundDraw()
	{
		return getSoundShooting() + "_draw";
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer)
	{
		return true;
	}
	
	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		return new FilledArrayList<ItemButton>();
	}
	
	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}
	
	@Override
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}
	
	public GunExtension newExtension(Gun gun)
	{
		try
		{
			return extensionClass.getConstructor(Gun.class).newInstance(gun);
		}
		catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
