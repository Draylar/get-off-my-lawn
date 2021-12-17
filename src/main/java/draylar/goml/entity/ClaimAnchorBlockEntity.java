package draylar.goml.entity;

import com.jamieswhiteshirt.rtree3i.Entry;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Augment;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.registry.PropertyTiles;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClaimAnchorBlockEntity extends BlockEntity {

    private static final String AUGMENT_LIST_KEY = "AugmentPositions";

    private final Map<BlockPos, ClaimAugmentBlockEntity> augmentEntities = new HashMap<>();
    private final List<BlockPos> loadPositions = new ArrayList<>();
    private final List<PlayerEntity> previousTickPlayers = new ArrayList<>();
    private Claim claim;

    public ClaimAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(PropertyTiles.CLAIM_ANCHOR, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        NbtList positions = new NbtList();
        augmentEntities.forEach((pos, be) -> positions.add(NbtLong.of(pos.asLong())));
        tag.put(AUGMENT_LIST_KEY, positions);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        NbtList positions = tag.getList(AUGMENT_LIST_KEY, NbtType.LONG);
        positions.forEach(sub -> {
            BlockPos foundPos = BlockPos.fromLong(((NbtLong) sub).longValue());
            loadPositions.add(foundPos);
        });

        super.readNbt(tag);
    }

    public void update() {

    }

    public void addChild(BlockPos pos, ClaimAugmentBlockEntity augment) {
        augmentEntities.put(pos, augment);
    }

    public void removeChild(BlockPos pos) {
        augmentEntities.remove(pos);
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public void tick() {
        assert world != null;

        if(world.isClient) {
            return;
        }

        // Claim is null, world probably just loaded, re-grab claim
        if(claim == null) {
            List<Entry<ClaimBox, Claim>> collect = ClaimUtils.getClaimsAt(world, pos).collect(Collectors.toList());

            if(collect.isEmpty()) {
                GetOffMyLawn.LOGGER.warn(String.format("A Claim Anchor at %s tried to initialize its claim, but one could not be found! Was the claim removed without the anchor?", pos.toString()));
            } else {
                claim = collect.get(0).getValue();
            }
        }

        // no augments, some queued from fromTag
        if(augmentEntities.isEmpty() && !loadPositions.isEmpty()) {
            loadPositions.forEach(foundPos -> {
                BlockEntity foundEntity = world.getBlockEntity(foundPos);

                if(foundEntity instanceof ClaimAugmentBlockEntity) {
                    augmentEntities.put(foundPos, (ClaimAugmentBlockEntity) foundEntity);
                } else {
                    GetOffMyLawn.LOGGER.warn(String.format("A Claim Anchor at %s tried to load a child at %s, but none were found!", pos.toString(), foundPos.toString()));
                }
            });

            loadPositions.clear();
        }

        ClaimAnchorBlock block = (ClaimAnchorBlock) world.getBlockState(pos).getBlock();
        int radius = block.getRadius();
        List<PlayerEntity> playersInClaim = world.getEntitiesByClass(PlayerEntity.class, new Box(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)), entity -> true);

        // Tick all augments
        augmentEntities.forEach((position, augmentBE) -> {
            Augment augment = augmentBE.getAugment();

            if(augment != null) {
                if (augment.ticks()) {
                    augment.tick(claim, world, augmentBE);
                    playersInClaim.forEach(augment::playerTick);
                }

                // Enter/Exit behavior
                playersInClaim.forEach(player -> {
                    // this player was NOT in the claim last tick, call entry method
                    if(!previousTickPlayers.contains(player)) {
                        augment.onPlayerEnter(claim, player);
                    }
                });

                // Tick exit behavior
                previousTickPlayers.stream().filter(player -> !playersInClaim.contains(player)).forEach(player -> {
                    augment.onPlayerExit(claim, player);
                });
            }
        });

        // Reset players in claim
        previousTickPlayers.clear();
        previousTickPlayers.addAll(playersInClaim);
    }

    public boolean hasAugment(ClaimAugmentBlock augment) {
        assert world != null;

        for (Map.Entry<BlockPos, ClaimAugmentBlockEntity> entry : augmentEntities.entrySet()) {
            BlockPos position = entry.getKey();
            ClaimAugmentBlock block = (ClaimAugmentBlock) world.getBlockState(position).getBlock();

            if (block.equals(augment)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAugment() {
        return augmentEntities.size() > 0;
    }

    public Map<BlockPos, ClaimAugmentBlockEntity> getAugmentEntities() {
        return augmentEntities;
    }

    public List<PlayerEntity> getPreviousTickPlayers() {
        return previousTickPlayers;
    }

    public void from(ClaimAnchorBlockEntity be) {
        this.previousTickPlayers.addAll(be.getPreviousTickPlayers());
        this.augmentEntities.putAll(be.getAugmentEntities());
    }
}
