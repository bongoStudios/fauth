package tk.bongostudios.fauth;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import tk.bongostudios.fauth.commands.ChangePasswordCommand;
import tk.bongostudios.fauth.commands.DeleteAccountCommand;
import tk.bongostudios.fauth.commands.ForceLoginCommand;
import tk.bongostudios.fauth.commands.LoginCommand;
import tk.bongostudios.fauth.commands.RegisterCommand;
import tk.bongostudios.fauth.db.Database;

public class FauthMod {
	static Database db;
	public static MinecraftServer server;
	public static StatusEffectInstance invisibility;
    public static StatusEffectInstance blindness;

	public static void onInitializeServer(MinecraftServer server) {
		FauthMod.server = server;
		String sqliteFile = "jdbc:sqlite:" + FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + "/fauth.db";
		FauthMod.db = new Database(sqliteFile);
		invisibility = new StatusEffectInstance(StatusEffects.INVISIBILITY, 100000);
		blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 100000);
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