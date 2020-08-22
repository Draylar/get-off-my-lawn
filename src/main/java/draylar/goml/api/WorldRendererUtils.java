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

        GetOffMyLawn.CLAIM.get(MinecraftClient.getInstance().world).getClaims().entries().forEach(boxClaimInfoEntry -> {
            BlockPos startPos = new BlockPos(boxClaimInfoEntry.getKey().x1(), boxClaimInfoEntry.getKey().y1(), boxClaimInfoEntry.getKey().z1());
            BlockPos endPos = new BlockPos(boxClaimInfoEntry.getKey().x2(), boxClaimInfoEntry.getKey().y2(), boxClaimInfoEntry.getKey().z2());

            int sizeX = endPos.getX() - startPos.getX();
            int sizeY = endPos.getY() - startPos.getY();
            int sizeZ = endPos.getZ() - startPos.getZ();

            stack.push();
            stack.translate(startPos.getX() - camPos.x, startPos.getY() - camPos.y, startPos.getZ() - camPos.z);
            net.minecraft.client.render.WorldRenderer.drawBox(stack, bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.getLines()), 0, 0, 0, sizeX, sizeY, sizeZ, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
            stack.pop();
        });
    }
}
