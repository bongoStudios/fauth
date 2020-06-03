package tk.bongostudios.fauth.utils;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class PlayerPos {
    public double x;
    public double y;
    public double z;
    public DimensionType dim;

    public PlayerPos(double x, double y, double z, String identifier) {
        this.x = x;
        this.y = y;
        this.z = z;

        dim = Registry.DIMENSION_TYPE.get(new Identifier(identifier));
    }
    
}