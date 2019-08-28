package tk.bongostudios.fauth.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow @Final
    private Block block;

    @Shadow
    protected abstract BlockState getPlacementState(ItemPlacementContext itemPlacementContext_1);
}