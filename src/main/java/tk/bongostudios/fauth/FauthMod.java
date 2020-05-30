package tk.bongostudios.fauth;

import java.util.concurrent.TimeUnit;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import tk.bongostudios.fauth.commands.ChangePasswordCommand;
import tk.bongostudios.fauth.commands.DeleteAccountCommand;
import tk.bongostudios.fauth.commands.ForceLoginCommand;
import tk.bongostudios.fauth.commands.LoginCommand;
import tk.bongostudios.fauth.commands.RegisterCommand;
import tk.bongostudios.fauth.db.Database;

public class FauthMod {
	static Database db;
	public static MinecraftServer server;

	public static void onInitializeServer(MinecraftServer server) {
		FauthMod.server = server;
		String sqliteFile = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + "/fauth.db";
		FauthMod.db = new Database(sqliteFile);
		Auth.scheduler.scheduleAtFixedRate(() -> {
			for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if(Auth.hasLoggedIn(player)) {
					Auth.savePosition(
						player.getUuid(),
						player.getX(),
						player.getY(),
						player.getZ(),
						DimensionType.getId(player.world.dimension.getType()).toString()
            		);
				}
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		RegisterCommand.register(dispatcher);
		LoginCommand.register(dispatcher);
		ChangePasswordCommand.register(dispatcher);
		ForceLoginCommand.register(dispatcher);
		DeleteAccountCommand.register(dispatcher);
	}

	public static void close() {
		db.close();
	}
}