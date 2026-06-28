package net.brandon.firstmod.item.behavoiur;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum DrillTier {
    FIRST(2, 2, 1, 250, 5, 14, Items.IRON_INGOT),
    SECOND(4, 3, 2, 500, 8, 18, Items.EMERALD),
    MAX(5, 4, 3, 900, 10, 22, Items.DIAMOND),;

    public final int depth;
    public final int width;
    public final int height;
    public final int durability;
    public final int breakRange;

    // allow them to be enchanted
    public final int enchantmentValue;
    public final Item repairIngredient;;

    DrillTier(int depth, int width, int height, int durability, int breakRange, int enchantmentValue, Item repairIngredient) {
        this.depth = depth;
        this.width = width;
        this.height = height;
        this.durability = durability;
        this.breakRange = breakRange;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }
}
