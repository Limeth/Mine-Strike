package cz.minestrike.me.limeth.minestrike.dbi.binding;

import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerDAO;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.*;

@BindingAnnotation(BindEquipment.BindEquipmentFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindEquipment
{
	String EMPTY_VALUE = "___jdbi_bare___";
	String value() default EMPTY_VALUE;
	
	class BindEquipmentFactory implements BinderFactory
	{
	    public Binder<BindEquipment, Equipment> build(Annotation annotation)
	    {
	        return new Binder<BindEquipment, Equipment>()
	        {
				public void bind(SQLStatement<?> q, BindEquipment bind, Equipment arg)
	            {
	                String prefix;
	                
	                if (EMPTY_VALUE.equals(bind.value()))
	                    prefix = "";
	                else
	                    prefix = bind.value() + ".";

		            q.bind(prefix + MSPlayerDAO.FIELD_EQUIPMENT_CATEGORY, arg.getCategory().name());
		            q.bind(prefix + MSPlayerDAO.FIELD_EQUIPMENT_TRADABLE, arg.isTradable());
                	q.bind(prefix + MSPlayerDAO.FIELD_EQUIPMENT_TYPE, arg.getId());
                	q.bind(prefix + MSPlayerDAO.FIELD_EQUIPMENT_DATA, EquipmentManager.toJson(arg));
	            }
	        };
	    }
	}
}
