package tk.bongostudios.fauth.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bongostudios.fauth.Auth;
import tk.bongostudios.fauth.utils.Descriptor;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {


	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if(Auth.hasAccount(player.getUuid())) {
            Auth.addDescriptor(player.getUuid(), Descriptor.LOGIN);
        } else {
            Auth.addDescriptor(player.getUuid(), Descriptor.REGISTER);
        }
        ScheduledFuture<?> future = Auth.scheduler.scheduleAtFixedRate(() -> player.sendMessage(new LiteralText(Auth.whichDescriptor(player.getUuid()).msg)), 0, 3, TimeUnit.SECONDS);
    
        Auth.scheduler.schedule(() -> {
            future.cancel(true);
            if(!Auth.hasLoggedIn(player)) {
                player.networkHandler.disconnect(new LiteralText("You took too much time!"));
            }
        }, 18, TimeUnit.SECONDS);
    }
}