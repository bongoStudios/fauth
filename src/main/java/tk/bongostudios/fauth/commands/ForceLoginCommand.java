package tk.bongostudios.fauth.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import tk.bongostudios.fauth.Auth;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandSource.suggestMatching;

public class ForceLoginCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("forcelogin")
            .requires(src -> src.hasPermissionLevel(4))
            .then(argument("player", word())
                .suggests((c, b) -> suggestMatching(c.getSource().getPlayerNames(),b))
                .executes(c -> {
                    String playerString = getString(c, "player");
                    PlayerEntity player = c.getSource().getMinecraftServer().getPlayerManager().getPlayer(playerString);
                    if(player == null) {
                        c.getSource().sendFeedback(new LiteralText("§cNo player specified"), false);
                        return 0;
                    }
                    if(Auth.hasLoggedIn(player)) {
                        c.getSource().sendFeedback(new LiteralText("§cThat player is already logged in"), false);
                        return 1;
                    }
                    Auth.removeDescriptor(player.getUuid());
                    Auth.addLoggedIn(player);
                    c.getSource().sendFeedback(new LiteralText("§aLogin has been forced!"), false);
                    return 1;
                })
            );
        dispatcher.register(command);
    }
}