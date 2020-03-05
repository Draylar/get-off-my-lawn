package draylar.goml.cca;

import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimInfo;
import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldClaimComponent implements ClaimComponent {

    private RTreeMap<ClaimBox, ClaimInfo> claims = RTreeMap.create(new ConfigurationBuilder().star().build(), ClaimBox::toBox);
    private final World world;

    public WorldClaimComponent(World world) {
        this.world = world;
    }

    @Override
    public RTreeMap<ClaimBox, ClaimInfo> getClaims() {
        return claims;
    }

    @Override
    public void add(ClaimBox box, ClaimInfo info) {
        this.claims = this.claims.put(box, info);
        sync();
    }

    @Override
    public void remove(ClaimBox box) {
        this.claims = this.claims.remove(box);
        sync();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.claims = RTreeMap.create(new ConfigurationBuilder().star().build(), ClaimBox::toBox);

        ListTag listTag = tag.getList("Claims", NbtType.COMPOUND);

        listTag.forEach(child -> {
            CompoundTag childCompound = (CompoundTag) child;
            ClaimBox box = boxFromTag((CompoundTag) childCompound.get("Box"));
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

    public CompoundTag serializeBox(ClaimBox box) {
        CompoundTag boxTag = new CompoundTag();

        boxTag.putLong("OriginPos", box.getOrigin().asLong());
        boxTag.putInt("Radius", box.getRadius());

        return boxTag;
    }

    public ClaimBox boxFromTag(CompoundTag tag) {
        BlockPos originPos = BlockPos.fromLong(tag.getLong("OriginPos"));
        int radius = tag.getInt("Radius");
        return new ClaimBox(originPos, radius);
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
