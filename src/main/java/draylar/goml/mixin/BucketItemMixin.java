package draylar.goml.mixin;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimInfo;
import draylar.goml.api.ClaimUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * UseBlockCallback doesn't seem to cover buckets well.
 * This mixin serves as an extra protection layer against client desync when using buckets in claims you don't own.
 */
@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Shadow @Final private Fluid fluid;

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void preventBucketUsageInClaims(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        HitResult hitResult = rayTrace(world, user, this.fluid == Fluids.EMPTY ? RayTraceContext.FluidHandling.SOURCE_ONLY : RayTraceContext.FluidHandling.NONE);
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();

        Selection<Entry<ClaimBox, ClaimInfo>> claimsFound = ClaimUtils.getClaimsAt(world, blockPos);

        if (!claimsFound.isEmpty()) {
            boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, ClaimInfo> boxInfo) -> !boxInfo.getValue().getOwner().equals(user.getUuid()));

            if(noPermission) {
                // TODO: translatable text
                user.sendMessage(new LiteralText("This block is protected by a claim."), true);
                cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
            }
        }
    }

    /**
     * Copy of protected method {@link net.minecraft.item.Item#rayTrace(World, PlayerEntity, RayTraceContext.FluidHandling)}
     *
     * @param world  world to ray trace in
     * @param player  player to ray trace from
     * @param fluidHandling  fluid handling
     * @return  {@link HitResult} of raytrace
     */
    private static HitResult rayTrace(World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling) {
        float f = player.pitch;
        float g = player.yaw;
        Vec3d vec3d = player.getCameraPosVec(1.0F);
        float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
        float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
        float j = -MathHelper.cos(-f * 0.017453292F);
        float k = MathHelper.sin(-f * 0.017453292F);
        float l = i * j;
        float n = h * j;
        Vec3d vec3d2 = vec3d.add((double)l * 5.0D, (double)k * 5.0D, (double)n * 5.0D);
        return world.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.OUTLINE, fluidHandling, player));
    }
}