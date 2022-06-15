package richiesams.enderio.reforged.recipes;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class ModRecipes {
    public static void registerRecipes() {
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(EnderIOReforgedBaseMod.MOD_ID, AlloyingRecipe.Serializer.ID),
                AlloyingRecipe.Serializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(EnderIOReforgedBaseMod.MOD_ID, AlloyingRecipe.Type.ID),
                AlloyingRecipe.Type.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(EnderIOReforgedBaseMod.MOD_ID, VanillaSmeltingRecipe.Serializer.ID),
                VanillaSmeltingRecipe.Serializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(EnderIOReforgedBaseMod.MOD_ID, VanillaSmeltingRecipe.Type.ID),
                VanillaSmeltingRecipe.Type.INSTANCE);
    }
}
