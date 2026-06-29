package net.brandon.firstmod.item.behavoiur;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class Drill extends Item{

    private final DrillTier tier;

    public Drill(Properties properties, DrillTier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        // Line 1
        textConsumer.accept(Component.translatable("item.first_mod.drill.tooltip.line1").withStyle(ChatFormatting.GOLD));

        // Line 2 (The "newline")
        textConsumer.accept(Component.translatable("item.first_mod.drill.tooltip.line2").withStyle(ChatFormatting.GOLD));

        int drillRange = tier.breakRange;
        int drillDepth = tier.depth;
        int currentDurability = stack.getMaxDamage() - stack.getDamageValue();

        textConsumer.accept(Component.translatable("item.first_mod.drill.tooltip.stat_lines", drillRange, drillDepth, currentDurability).withStyle(ChatFormatting.DARK_PURPLE));
    }




    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand)
    {
        // swing the arm once using the exact hand that triggered the action
        user.swing(hand);

        // check if client side and return CONSUME so the visual interaction succeeds locally
        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        int range = tier.depth;

        var rows = tier.height;
        var cols = tier.width;

        int breakRange = tier.breakRange;


        var hit = user.pick(breakRange, 0.0f, false);

        // early return
        if(hit.getType() == HitResult.Type.BLOCK)
        {

            BlockHitResult blockHit = (BlockHitResult) hit;

            // Get the exact face hit to determine the depth multiplier
            Direction hitFace = blockHit.getDirection();
            Direction.Axis axis = hitFace.getAxis();

            // If we hit a negative face (e.g., North / -Z), we want to mine positive (+Z)
            // If we hit a positive face (e.g., South / +Z), we want to mine negative (-Z)
            int depthMultiplier = -hitFace.getAxisDirection().getStep();

            BlockPos startPos = blockHit.getBlockPos();

            // track if we actually broke anything to determine if we should damage the tool
            boolean brokeBlocks = false;

           for(int i = -1; i < rows; i++) {
               for(int j = -1; j < cols; j++){
                   for(int k = -1; k < range; k++) {

                       // Multiply k by the depth direction so it mines into the block, not towards the player
                       int kOffset = k * depthMultiplier;

                       BlockPos pos = switch (axis) {
                           case X -> startPos.offset(kOffset, j - 1, i);
                           case Y -> startPos.offset(i, kOffset, j - 1);
                           default -> startPos.offset(i, j - 1, kOffset);
                       };

                       // get block state to prevent unbreakable from breaking
                       BlockState state = level.getBlockState(pos);
                       // prevent chests, bedrock, end portals, and the chests(all needed blocks)
                       if(state.getDestroySpeed(level, pos) < 0 || state.hasBlockEntity()) {
                           continue;
                       }
                       // still check, might be useless
                       if(state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.END_PORTAL
                       || state.getBlock() == Blocks.END_PORTAL_FRAME || state.getBlock() == Blocks.ANCIENT_DEBRIS || state.getBlock() == Blocks.RESPAWN_ANCHOR) {
                           continue;
                       }

                       // attempt to destroy the block and flag if successful
                       if (level.destroyBlock(pos, true)) {
                           brokeBlocks = true;
                       }
                   }
               }
           }
            // apply durability damage if the drill actually did work
            if (brokeBlocks) {
                // map the hand to the correct equipment slot for the break event
                net.minecraft.world.entity.EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ?
                        net.minecraft.world.entity.EquipmentSlot.MAINHAND :
                        net.minecraft.world.entity.EquipmentSlot.OFFHAND;

                // pass the slot directly to the method
                user.getItemInHand(hand).hurtAndBreak(1, user, slot);
                // the int is for ticks.
                // 20 ticks = 1 second.
                user.getCooldowns().addCooldown(this.getDefaultInstance(), 10);
            }
        }


        return InteractionResult.CONSUME;
    }
}
