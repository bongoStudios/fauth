package tk.bongostudios.fauth;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import tk.bongostudios.fauth.db.Database;

public class FauthMod {
	static Database db;
	public static MinecraftServer server;

	public static void onInitializeServer(MinecraftServer server) {
		FauthMod.server = server;
		String sqliteFile = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + "/fauth.db";
		FauthMod.db = new Database(sqliteFile);
	}

	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		
	}
}