package draylar.goml.mixin;

import draylar.goml.api.WorldRendererUtils;
import draylar.goml.registry.GOMLItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Shadow
    private ClientWorld world;


    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "net/minecraft/util/profiler/Profiler.swap(Ljava/lang/String;)V",
                    args = "ldc=blockentities",shift = At.Shift.BEFORE))
    private void renderClaims(
            MatrixStack stack,
            float tickDelta,
            long limitTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f,
            CallbackInfo ci)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(player != null && MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(GOMLItems.GOGGLES)) {
            Profiler profiler = world.getProfiler();
            Vec3d camPos = camera.getPos();
            profiler.swap("goml");
            WorldRendererUtils.renderBoxes(stack, camPos, bufferBuilders);
        }
    }
}