package tk.bongostudios.fauth;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import tk.bongostudios.fauth.commands.ChangePasswordCommand;
import tk.bongostudios.fauth.commands.DeleteAccountCommand;
import tk.bongostudios.fauth.commands.ForceLoginCommand;
import tk.bongostudios.fauth.commands.LoginCommand;
import tk.bongostudios.fauth.commands.RegisterCommand;
import tk.bongostudios.fauth.commands.ResetPosCommand;
import tk.bongostudios.fauth.db.Database;

public class FauthMod {
	public static Database db;
	public static MinecraftServer server;

	public static void onInitializeServer(MinecraftServer server) {
		Auth.scheduler.setRemoveOnCancelPolicy(true);
		FauthMod.server = server;
		String sqliteFile = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + "/fauth.db";
		FauthMod.db = new Database(sqliteFile);
	}

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		RegisterCommand.register(dispatcher);
		LoginCommand.register(dispatcher);
		ChangePasswordCommand.register(dispatcher);
		ForceLoginCommand.register(dispatcher);
		DeleteAccountCommand.register(dispatcher);
		ResetPosCommand.register(dispatcher);
	}

	public static void close() {
		Auth.clear();
		db.close();
		db = null;
		server = null;
	}
}