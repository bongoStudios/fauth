package tk.bongostudios.fauth.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import tk.bongostudios.fauth.Auth;
import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeleteAccountCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("delacc")
            .requires(src -> {
                try {
                    ServerPlayerEntity player = src.getPlayer();
                    return Auth.hasAccount(player.getUuid()) && Auth.hasLoggedIn(player);
                } catch(CommandSyntaxException e) {
                    return false;
                }
            })
            .then(argument("password", word())
                .executes(c -> {
                    ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                    String pass = getString(c, "password");
                    if(!Auth.login(player.getUuid(), pass)) {
                        player.sendSystemMessage(new LiteralText("§cThat isn't your password!"), Util.NIL_UUID);
                        return 1;
                    }
                    Auth.delete(player.getUuid());
                    player.sendSystemMessage(new LiteralText("§cYour account was deleted"), Util.NIL_UUID);
                    Auth.scheduler.schedule(() -> player.networkHandler.disconnect(new LiteralText("Bye, bye!")), 5, TimeUnit.SECONDS);
                    return 1;
                })
            );
        dispatcher.register(command);
    }
}