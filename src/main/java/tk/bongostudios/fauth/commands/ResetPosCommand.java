package tk.bongostudios.fauth.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import tk.bongostudios.fauth.FauthMod;

import static net.minecraft.server.command.CommandManager.literal;

public class ResetPosCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("resetpos")
            .requires(src -> src.hasPermissionLevel(4))
            .executes(c -> {
                ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                FauthMod.db.updateEveryPos(0, 0, 0, null);
                player.sendSystemMessage(new LiteralText("§aPositions have been nullified"), Util.NIL_UUID);
                return 1;
            });
        dispatcher.register(command);
    }
}