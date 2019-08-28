package tk.bongostudios.fauth.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import tk.bongostudios.fauth.Auth;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow @Final
    private Block block;

    @Shadow @Nullable
    protected abstract BlockState getPlacementState(ItemPlacementContext itemPlacementContext_1);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"), method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    private void onPlaceBlock(ItemPlacementContext itemPlacementContext_1, CallbackInfoReturnable<ActionResult> ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) itemPlacementContext_1.getPlayer();

        if(Auth.hasLoggedIn(player)) return;

        player.world.setBlockState(itemPlacementContext_1.getBlockPos(), Blocks.AIR.getDefaultState());
        ci.setReturnValue(ActionResult.FAIL);
        ci.cancel();
    }
}