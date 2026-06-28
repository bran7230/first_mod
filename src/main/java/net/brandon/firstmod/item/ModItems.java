package net.brandon.firstmod.item;

import net.brandon.firstmod.First_mod;
import net.brandon.firstmod.item.behavoiur.Drill;
import net.brandon.firstmod.item.behavoiur.DrillTier;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;


public class ModItems {

    public static final Drill TEST_ITEM = registerDrill("test_item", DrillTier.FIRST);
    public static final Drill TEST_ITEM_UP1 = registerDrill("test_item_up1", DrillTier.SECOND);
    public static final Drill TEST_ITEM_MAX = registerDrill("test_item_max", DrillTier.MAX);

    private static Drill registerDrill(String name, DrillTier tier) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(First_mod.MOD_ID, name));

        Drill drill = new Drill(new Item.Properties()
                .setId(itemKey)
                .durability(tier.durability)
                .enchantable(tier.enchantmentValue)
                .repairable(tier.repairIngredient)
                , tier);
        Registry.register(BuiltInRegistries.ITEM, itemKey, drill);

        return drill;
    }

    public static void registerModItems() {
        First_mod.LOGGER.info("Registering Mod Items for " + First_mod.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((creativeTab) -> creativeTab.accept(ModItems.TEST_ITEM));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((creativeTab) -> creativeTab.accept(ModItems.TEST_ITEM_UP1));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((creativeTab) -> creativeTab.accept(ModItems.TEST_ITEM_MAX));
    }


}