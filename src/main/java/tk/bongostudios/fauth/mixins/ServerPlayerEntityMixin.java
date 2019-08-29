package tk.bongostudios.fauth.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tk.bongostudios.fauth.Auth;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world_1, GameProfile gameProfile_1) {
        super(world_1, gameProfile_1);
    }

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource damageSource_1, float float_1, CallbackInfoReturnable<Boolean> ci) {
        if(Auth.hasLoggedIn(this)) return;
        ci.setReturnValue(false);
        ci.cancel();
    }
}