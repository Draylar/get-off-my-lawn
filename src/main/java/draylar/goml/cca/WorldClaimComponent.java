package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import nerdhub.cardinal.components.api.ComponentType;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimInfo;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldClaimComponent implements ClaimComponent {

    private RTreeMap<Box, ClaimInfo> claims = RTreeMap.create(new ConfigurationBuilder().star().build());
    private final World world;

    public WorldClaimComponent(World world) {
        this.world = world;
    }

    @Override
    public RTreeMap<Box, ClaimInfo> getClaims() {
        return claims;
    }

    @Override
    public void add(Box box, ClaimInfo info) {
        this.claims = this.claims.put(box, info);
        sync();
    }

    @Override
    public void remove(Box box) {
        this.claims = this.claims.remove(box);
        sync();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.claims = RTreeMap.create(new ConfigurationBuilder().star().build());

        ListTag listTag = tag.getList("Claims", NbtType.COMPOUND);

        listTag.forEach(child -> {
            CompoundTag childCompound = (CompoundTag) child;
            Box box = boxFromTag((CompoundTag) childCompound.get("Box"));
            ClaimInfo claimInfo = ClaimInfo.fromTag((CompoundTag) childCompound.get("Info"));
            add(box, claimInfo);
        });
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag listTagClaims = new ListTag();

        claims.entries().forEach(claim -> {
            CompoundTag claimTag = new CompoundTag();

            claimTag.put("Box", serializeBox(claim.getKey()));
            claimTag.put("Info", claim.getValue().asTag());

            listTagClaims.add(claimTag);
        });

        tag.put("Claims", listTagClaims);
        return tag;
    }

    public CompoundTag serializeBox(Box box) {
        CompoundTag boxTag = new CompoundTag();

        boxTag.putLong("Pos1", new BlockPos(box.x1(), box.y1(), box.z1()).asLong());
        boxTag.putLong("Pos2", new BlockPos(box.x2(), box.y2(), box.z2()).asLong());

        return boxTag;
    }

    public Box boxFromTag(CompoundTag tag) {
        BlockPos pos1 = BlockPos.fromLong(tag.getLong("Pos1"));
        BlockPos pos2 = BlockPos.fromLong(tag.getLong("Pos2"));

        return Box.create(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public ComponentType<?> getComponentType() {
        return GetOffMyLawn.CLAIM;
    }
}
