package tk.bongostudios.fauth.utils;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PlayerPos {
    public double x;
    public double y;
    public double z;
    public RegistryKey<World> dim;

    public PlayerPos(double x, double y, double z, String identifier) {
        this.x = x;
        this.y = y;
        this.z = z;

        dim = RegistryKey.of(Registry.DIMENSION, new Identifier(identifier));
    }
    
}