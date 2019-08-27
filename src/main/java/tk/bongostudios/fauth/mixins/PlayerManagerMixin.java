package tk.bongostudios.fauth.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bongostudios.fauth.Auth;
import tk.bongostudios.fauth.utils.Descriptor;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {



	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		//C2SPlayConnectCallback.EVENT.invoker().onJoin(connection, (ServerPlayPacketListener) connection.getPacketListener());
        if(Auth.hasAccount(player.getUuid())) {
            Auth.addDescriptor(player.getUuid(), Descriptor.LOGIN);
        } else {
            Auth.addDescriptor(player.getUuid(), Descriptor.REGISTER);
        }
    }
}