package com.finshope.gtsecore.client.renderer.machine;

import com.finshope.gtsecore.common.machine.multiblock.electric.LargeCombustionSetMachine;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import static com.finshope.gtsecore.client.renderer.GTSERenderTypes.SIMPLE_TRIANGLE_STRIP;

public class LargeCombustionSetRenderer extends WorkableCasingMachineRenderer {
    public LargeCombustionSetRenderer(ResourceLocation baseCasing, ResourceLocation workableModel) {
        super(baseCasing, workableModel);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof IMachineBlockEntity machineBlockEntity &&
                machineBlockEntity.getMetaMachine() instanceof LargeCombustionSetMachine machine) {
            if (!machine.recipeLogic.isWorking()) {
                return;
            }
            renderMachine(machine, blockEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);
        }
    }

    private void translate(Matrix4f mat, Direction direction, float mv) {
        float x = direction.getStepX() * mv;
        float y = direction.getStepY() * mv;
        float z = direction.getStepZ() * mv;
        mat.translate(x, y, z);
    }

    private VertexConsumer vertexDirection(VertexConsumer vc, Matrix4f mat, Direction dx, Direction dy, Direction dz, float x, float y, float z) {
        float px = dx.getStepX() * x + dy.getStepX() * y + dz.getStepX() * z;
        float py = dx.getStepY() * x + dy.getStepY() * y + dz.getStepY() * z;
        float pz = dx.getStepZ() * x + dy.getStepZ() * y + dz.getStepZ() * z;
        return vc.vertex(mat, px, py, pz);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderMachine(LargeCombustionSetMachine machine, BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        // render a rotated block
        stack.pushPose();

        var mat4 = stack.last().pose();
        var vc = buffer.getBuffer(SIMPLE_TRIANGLE_STRIP);
        var level = (ClientLevel) blockEntity.getLevel();
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var up = RelativeDirection.UP.getRelativeFacing(front, upwards, flipped);
        var back = RelativeDirection.BACK.getRelativeFacing(front, upwards, flipped);
        var right = RelativeDirection.RIGHT.getRelativeFacing(front, upwards, flipped);

        mat4.translate(0.5f, 0.5f, 0.5f);
        float radius = 1.5f;
        var angle = System.currentTimeMillis() % 1000 / 1000f * 360f;

        Matrix4f backMat = new Matrix4f(mat4);
        translate(backMat, up, 3f);
        translate(backMat, back, 4.501f);
        drawFace(machine, backMat, angle, buffer, up, back, right, radius);

        Matrix4f frontMat = new Matrix4f(mat4);
        translate(frontMat, up, 3f);
        translate(frontMat, back, 1.499f);
        drawFace(machine, frontMat, angle, buffer, up, back, right, radius);

        Matrix4f rightMat = new Matrix4f(mat4);
        translate(rightMat, up, 3f);
        translate(rightMat, right, 1.501f);
        translate(rightMat, back, 3f);
        drawFace(machine, rightMat, angle, buffer, up, right, back, radius);

        Matrix4f leftMat = new Matrix4f(mat4);
        translate(leftMat, up, 3f);
        translate(leftMat, right, -1.501f);
        translate(leftMat, back, 3f);
        drawFace(machine, leftMat, angle, buffer, up, right, back, radius);
        stack.popPose();
    }

    private void drawFace(LargeCombustionSetMachine machine, Matrix4f mat4, float angle, MultiBufferSource buffer, Direction up, Direction back, Direction right, float radius) {
        var vc = buffer.getBuffer(SIMPLE_TRIANGLE_STRIP);
        angle = 0;
        switch (back) {
            case UP:
            case DOWN:
                mat4.rotateAround(Axis.YP.rotationDegrees(angle), 0, 0, 0);
                break;
            case NORTH:
            case SOUTH:
                mat4.rotateAround(Axis.ZP.rotationDegrees(angle), 0, 0, 0);
                break;
            case WEST:
            case EAST:
                mat4.rotateAround(Axis.XP.rotationDegrees(angle), 0, 0, 0);
                break;
        }

        // make color change as time pass
        var red = (System.currentTimeMillis() % 100000) / 100000f;
        if (red < 0.5f) {
            red = 1 - red;
        }
        var green = (System.currentTimeMillis() % 10000) / 10000f;
        if (green < 0.2f) {
            green = 0.4f - green;
        }
        var blue = (System.currentTimeMillis() % 30000) / 30000f;
        if (blue < 0.7f) {
            blue = 1.4f - blue;
        }
        var alpha = 0.9f - (System.currentTimeMillis() % 5000) / 10000f;
        // debounce alpha
        if (alpha < 0.65) {
            alpha = 1.3f - alpha;
        }

        vertexDirection(vc, mat4, right, up, back, -radius, -radius, 0).color(red, green, 0, alpha).uv(0, 0).uv2(0, 0).normal(1, 0, 0).endVertex();
        vertexDirection(vc, mat4, right, up, back, radius, -radius, 0).color(0, green, 0, alpha).uv(0, 1).uv2(0, 1).normal(0, 1, 0).endVertex();
        vertexDirection(vc, mat4, right, up, back, radius, radius, 0).color(red, green, blue, alpha).uv(1, 1).uv2(1, 1).normal(1, 1, 1).endVertex();
        vertexDirection(vc, mat4, right, up, back, radius, radius, 0).color(red, green, blue, alpha).uv(1, 1).uv2(1, 1).normal(1, 1, 1).endVertex();
        vertexDirection(vc, mat4, right, up, back, -radius, radius, 0).color(0f, 0, blue, alpha).uv(1, 0).uv2(1, 0).normal(0, 0, 1).endVertex();
        vertexDirection(vc, mat4, right, up, back, -radius, -radius, 0).color(red, green, 0, alpha).uv(0, 0).uv2(0, 0).normal(1, 0, 0).endVertex();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasTESR(BlockEntity blockEntity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isGlobalRenderer(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
//
//    @Override
//    public boolean shouldRender(BlockEntity blockEntity, Vec3 cameraPos) {
//        return true;
//    }
}
