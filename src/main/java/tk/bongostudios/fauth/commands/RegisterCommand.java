package tk.bongostudios.fauth.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import tk.bongostudios.fauth.Auth;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RegisterCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("register")
            .requires(src -> {
                try {
                    return !Auth.hasAccount(src.getPlayer().getUuid());
                } catch(CommandSyntaxException e) {
                    return false;
                }
            })
            .then(argument("password", word()))
            .then(argument("verify", word()))
            .executes(c -> {
                ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                String pass = getString(c, "password");
                if(pass != getString(c, "verifyPassword")) {
                    player.sendMessage(new LiteralText("§cThe password is not the same as the second one!"));
                    return 1;
                }
                Auth.register(player.getUuid(), pass);
                Auth.removeDescriptor(player.getUuid());
                Auth.addLoggedIn(player);
                return 1;
            });
        dispatcher.register(command);
    }
}