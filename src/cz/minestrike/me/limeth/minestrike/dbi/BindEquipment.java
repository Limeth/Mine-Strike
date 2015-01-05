package cz.minestrike.me.limeth.minestrike.dbi;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;

import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;

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
