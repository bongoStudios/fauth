package tk.bongostudios.fauth.mixins;

import com.google.gson.JsonElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelGeneratorType;
import tk.bongostudios.fauth.FauthMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void serverLoaded(String string_1, String string_2, long long_1, LevelGeneratorType levelGeneratorType_1, JsonElement jsonElement_1, CallbackInfo ci) {
        FauthMod.onInitializeServer((MinecraftServer) (Object) this);
    }
}
