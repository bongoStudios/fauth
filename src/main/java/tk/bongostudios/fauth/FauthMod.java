package tk.bongostudios.fauth;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.mindrot.jbcrypt.BCrypt;

public class FauthMod implements DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");
	}
}