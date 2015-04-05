package cz.minestrike.me.limeth.minestrike.equipment.cases;

/**
 * @author Limeth
 */
public class Case extends AbstractCase
{
	private final CaseContent[] contents;

	public Case(String id, String name, CaseContent... contents)
	{
		super(id, name);

		this.contents = contents;
	}

	@Override
	protected CaseContent[] initContents()
	{
		return contents;
	}
}
