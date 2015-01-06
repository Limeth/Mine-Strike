package cz.minestrike.me.limeth.minestrike.dbi;

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
	String value() default "___jdbi_bare___";
	
	class BindEquipmentFactory implements BinderFactory
	{
	    public Binder<BindEquipment, Equipment> build(Annotation annotation)
	    {
	        return new Binder<BindEquipment, Equipment>()
	        {
				public void bind(SQLStatement<?> q, BindEquipment bind, Equipment arg)
	            {
	                String prefix;
	                
	                if ("___jdbi_bare___".equals(bind.value()))
	                    prefix = "";
	                else
	                    prefix = bind.value() + ".";
	                
                	q.bind(prefix + "id", arg.getId());
                	q.bind(prefix + "data", EquipmentManager.toJson(arg));
	            }
	        };
	    }
	}
}
