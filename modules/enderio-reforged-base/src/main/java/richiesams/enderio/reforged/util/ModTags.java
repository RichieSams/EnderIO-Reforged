package richiesams.enderio.reforged.util;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import richiesams.enderio.reforged.EnderIOReforgedBaseMod;

public class ModTags {
    public static class Items {
        public static TagKey<Item> CAPACITORS;
        public static TagKey<Item> GRINDING_BALLS;

        public static void registerItemTags() {
            CAPACITORS = TagKey.of(Registry.ITEM_KEY, new Identifier(EnderIOReforgedBaseMod.MOD_ID, "capacitors"));
            GRINDING_BALLS = TagKey.of(Registry.ITEM_KEY, new Identifier(EnderIOReforgedBaseMod.MOD_ID, "grinding_balls"));
        }
    }

    public static class Blocks {
        public static void registerBlockTags() {
            // Nothing at the moment
        }
    }
}
