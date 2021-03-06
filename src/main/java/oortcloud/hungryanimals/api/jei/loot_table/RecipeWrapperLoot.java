package oortcloud.hungryanimals.api.jei.loot_table;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.entity.EntityLiving;
import oortcloud.hungryanimals.entities.production.IProductionJEI;

public class RecipeWrapperLoot implements IRecipeWrapper {

	private IProductionJEI recipe;
	private Class<? extends EntityLiving> entityClass;
	private IJeiHelpers jeiHelpers;

	public RecipeWrapperLoot(IJeiHelpers jeiHelpers, Class<? extends EntityLiving> entityClass,
			IProductionJEI production) {
		this.jeiHelpers = jeiHelpers;
		this.entityClass = entityClass;
		this.recipe = production;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		recipe.getIngredients(jeiHelpers, ingredients);
	}

}
