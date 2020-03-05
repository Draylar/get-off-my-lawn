package draylar.goml.api;

import com.jamieswhiteshirt.rtree3i.Box;
import net.minecraft.util.math.BlockPos;

public class ClaimBox {

    private BlockPos origin;
    private int radius;

    public ClaimBox(BlockPos origin, int radius) {
        this.origin = origin;
        this.radius = radius;
    }

    public Box toBox() {
        BlockPos lower = origin.add(-radius, -radius, -radius);
        BlockPos upper = origin.add(radius, radius, radius);
        return Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ());
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public int getRadius() {
        return radius;
    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
