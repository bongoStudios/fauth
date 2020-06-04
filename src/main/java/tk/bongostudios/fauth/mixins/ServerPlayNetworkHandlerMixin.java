package tk.bongostudios.fauth.mixins;

import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bongostudios.fauth.Auth;

import java.util.Collections;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements PacketListener {

    @Shadow @Final
    private MinecraftServer server;
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onDisconnect(Text text_1, CallbackInfo ci) {
        if(Auth.hasLoggedIn(player)) {
            Auth.savePosition(
                player.getUuid(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.world.getRegistryKey().getValue().toString()
            );
        }
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        // Apparently you cant getSpawnPos() from server, kind of weird its client-only
        WorldProperties properties = overworld.getLevelProperties();
        BlockPos spawn = new BlockPos(properties.getSpawnX(), properties.getSpawnY(), properties.getSpawnZ());
        if (!overworld.getWorldBorder().contains(spawn)) {
            spawn = overworld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(overworld.getWorldBorder().getCenterX(), 0.0D, overworld.getWorldBorder().getCenterZ()));
        }

        player.teleport(overworld, spawn.getX(), spawn.getY(), spawn.getZ(), player.yaw, player.pitch);
        Auth.removeLoggedIn(player);
    }

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String string_1, CallbackInfo ci) {
        if(!string_1.startsWith("/") || Auth.hasLoggedIn(player)) return;
        string_1 = string_1.substring(1).toLowerCase();
        if(string_1.startsWith("login") || string_1.startsWith("register")) return;
        ci.cancel();
    }

    private int moveCancel = 0;
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerInput(PlayerMoveC2SPacket playerMoveC2SPacket_1, CallbackInfo ci) {
        if(!Auth.hasLoggedIn(player)) {
            moveCancel++;

            if (moveCancel > 5) {
                this.player.networkHandler.sendPacket(
                    new PlayerPositionLookS2CPacket(
                        this.player.getX(), 
                        this.player.getY(), 
                        this.player.getZ(), 
                        this.player.yaw, 
                        this.player.pitch, 
                        Collections.emptySet(), 0
                    )
                );
                moveCancel = 0;
            }
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    public void onPlayerDrop(PlayerActionC2SPacket playerActionC2SPacket_1, CallbackInfo ci) {
        if(playerActionC2SPacket_1.getAction() != PlayerActionC2SPacket.Action.DROP_ITEM
        || Auth.hasLoggedIn(player)) return;
        ci.cancel();
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    public void onPlayerBreak(PlayerActionC2SPacket playerActionC2SPacket_1, CallbackInfo ci) {
        if(!this.player.isCreative()) {
            if(playerActionC2SPacket_1.getAction() != PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) return;
        }
        if(!Auth.hasLoggedIn(player)) {
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.player.world, playerActionC2SPacket_1.getPos()));
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket_1, CallbackInfo ci) {
        if(!Auth.hasLoggedIn(player)) ci.cancel();
    }

    @Inject(method = "onPlayerInteractItem", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket playerInteractItemC2SPacket_1, CallbackInfo ci) {
        if(!Auth.hasLoggedIn(player)) ci.cancel();
    }

    @Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket_1, CallbackInfo ci) {
        if(!Auth.hasLoggedIn(player)) ci.cancel();
    }

    @Inject(method = "onClickWindow", at = @At("HEAD"), cancellable = true)
    public void onPickFromInventory(ClickWindowC2SPacket clickWindowC2SPacket_1, CallbackInfo ci) {
        if(Auth.hasLoggedIn(player)) return;
        this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(clickWindowC2SPacket_1.getSyncId(), clickWindowC2SPacket_1.getActionId(), false));
        ci.cancel();
    }
}