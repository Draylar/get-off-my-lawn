package draylar.goml.api;

import draylar.goml.GetOffMyLawn;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class WorldRendererUtils {

    public static void renderBoxes(MatrixStack stack, Vec3d camPos, BufferBuilderStorage bufferBuilders) {
        if(MinecraftClient.getInstance().world == null) {
            return;
        }

        GetOffMyLawn.CLAIM.get(MinecraftClient.getInstance().world).getClaims().entries().forEach(claim -> {
            BlockPos claimPos = claim.getKey().getOrigin();
            int radius = claim.getKey().getRadius();

            stack.push();
            stack.translate(claimPos.getX() - camPos.x, claimPos.getY() - camPos.y, claimPos.getZ() - camPos.z);
            net.minecraft.client.render.WorldRenderer.drawBox(stack, bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.getLines()), -radius, -radius, -radius, radius, radius, radius, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
            stack.pop();
        });
    }
}
