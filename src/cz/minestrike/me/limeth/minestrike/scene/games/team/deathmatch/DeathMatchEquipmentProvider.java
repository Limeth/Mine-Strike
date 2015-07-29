package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSection;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSectionEntry;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProviderImpl;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;

/**
 * Created by limeth on 28.7.15.
 */
public class DeathMatchEquipmentProvider extends EquipmentProviderImpl<DeathMatchGame>
{
    static
    {
        FilledArrayList<EquipmentSection> categories = new FilledArrayList<>();

        categories.add(EquipmentSection.PISTOLS);
        categories.add(EquipmentSection.HEAVY);
        categories.add(EquipmentSection.SMGS);
        categories.add(EquipmentSection.RIFLES);
        //No need to add unused categories
        /*categories.add(EquipmentSection.GEAR);
        categories.add(EquipmentSection.GRENADES);*/

        EQUIPMENT_CATEGORIES = categories;
    }

    private static final FilledArrayList<EquipmentSection> EQUIPMENT_CATEGORIES;

    public DeathMatchEquipmentProvider(DeathMatchGame game)
    {
        super(game);
    }

    @Override
    public boolean pickup(MSPlayer msPlayer, Equipment equipment)
    {
        return false; //Don't pick up anything
    }

    @Override
    public void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException
    {
        String additionError = getAdditionError(msPlayer, equipment, true);

        if(additionError != null)
            throw new EquipmentPurchaseException(equipment, additionError);

        /*Equipment thrown =*/ add(msPlayer, equipment); //No need to throw out anything
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilledArrayList<EquipmentSection> getEquipmentCategories()
    {
        return (FilledArrayList<EquipmentSection>) EQUIPMENT_CATEGORIES.clone();
    }
}
