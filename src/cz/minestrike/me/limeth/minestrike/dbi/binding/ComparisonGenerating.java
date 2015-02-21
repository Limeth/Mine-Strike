package cz.minestrike.me.limeth.minestrike.dbi.binding;

import java.util.List;

/**
 * @author Limeth
 */
public interface ComparisonGenerating <T extends Object>
{
	List<String> generateComparison(T other);
}
