package tk.bongostudios.fauth.mixins;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bongostudios.fauth.Auth;
import tk.bongostudios.fauth.FauthMod;
import tk.bongostudios.fauth.utils.Descriptor;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {


	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if(!(player.hasStatusEffect(StatusEffects.BLINDNESS) || player.hasStatusEffect(StatusEffects.INVISIBILITY))) {
            player.addPotionEffect(FauthMod.blindness);
            player.addPotionEffect(FauthMod.invisibility);
            Auth.addPotion(player);
        }
        if(Auth.hasAccount(player.getUuid())) {
            Auth.addDescriptor(player.getUuid(), Descriptor.LOGIN);
        } else {
            Auth.addDescriptor(player.getUuid(), Descriptor.REGISTER);
        }
        ScheduledFuture<?> future = Auth.scheduler.scheduleAtFixedRate(() -> player.sendMessage(new LiteralText(Auth.whichDescriptor(player.getUuid()).msg)), 0, 3, TimeUnit.SECONDS);

        Auth.scheduler.schedule(() -> {
            if((player.hasStatusEffect(StatusEffects.BLINDNESS) || player.hasStatusEffect(StatusEffects.INVISIBILITY)) && Auth.hasPotion(player)) {
                player.removeStatusEffect(StatusEffects.INVISIBILITY);
                player.removeStatusEffect(StatusEffects.BLINDNESS);
                Auth.removePotion(player);
            }
            future.cancel(true);
            if(!Auth.hasLoggedIn(player)) {
                player.networkHandler.disconnect(new LiteralText("You took too much time!"));
            }
        }, 18, TimeUnit.SECONDS);
    }
}