package com.finshope.gtsecore.client.renderer.machine;

import com.finshope.gtsecore.common.machine.multiblock.electric.LargeCombustionSetMachine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.model.ModelUtil;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

import static com.finshope.gtsecore.client.renderer.GTSERenderTypes.SIMPLE_TRIANGLE_STRIP;

public class LargeCombustionSetRenderer extends WorkableCasingMachineRenderer {

    public LargeCombustionSetRenderer(ResourceLocation baseCasing, ResourceLocation workableModel) {
        super(baseCasing, workableModel);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
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

    private VertexConsumer vertexDirection(VertexConsumer vc, Matrix4f mat, Direction dx, Direction dy, Direction dz,
                                           float x, float y, float z) {
        float px = dx.getStepX() * x + dy.getStepX() * y + dz.getStepX() * z;
        float py = dx.getStepY() * x + dy.getStepY() * y + dz.getStepY() * z;
        float pz = dx.getStepZ() * x + dy.getStepZ() * y + dz.getStepZ() * z;
        return vc.vertex(mat, px, py, pz);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderMachine(LargeCombustionSetMachine machine, BlockEntity blockEntity, float partialTicks,
                               PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
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
        int period = 1000;
        var angle = (System.currentTimeMillis() % period) / (float) period * 360f;

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

        // drawRotation(stack, blockEntity.getBlockPos(), level, buffer, combinedLight, combinedOverlay, up, back,
        // right);
    }

    private void drawRotation(PoseStack stack, BlockPos pos, ClientLevel level, MultiBufferSource buffer,
                              int combinedLight, int combinedOverlay, Direction up, Direction back, Direction right) {
        stack.pushPose();
        var pose = stack.last();
        var mat4 = pose.pose();
        mat4.translate(0, 8.001f, 0);
        var rel = pos.relative(right, 4).subtract(pos);
        mat4.translate(rel.getX(), rel.getY(), rel.getZ());

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        RandomSource rand = level.getRandom();
        rel = pos.relative(right, -4).relative(back, 3).subtract(pos);
        var axis = Axis.YP;
        switch (back) {
            case UP:
                axis = Axis.YP;
                break;
            case DOWN:
                axis = Axis.YN;
                break;
            case NORTH:
                axis = Axis.ZP;
                break;
            case SOUTH:
                axis = Axis.ZN;
                break;
            case EAST:
                axis = Axis.XP;
                break;
            case WEST:
                axis = Axis.XN;
                break;
        }
        int period = 1000;
        var angle = (System.currentTimeMillis() % period) / (float) period * 360f;

        mat4.rotateAround(axis.rotationDegrees(angle), rel.getX() + 0.5f, 0, rel.getZ() + 0.5f);

        int light = LightTexture.pack(15, 15);
        var center = pos.relative(up, 6).relative(right, 3);
        int width = 7;
        int height = 7;

        Vector3f rightStep = new Vector3f(-right.getStepX(), -right.getStepY(), -right.getStepZ());
        rel = pos.relative(back, 1).relative(right, height).subtract(pos);
        Vector3f resetStep = new Vector3f(rel.getX(), rel.getY(), rel.getZ());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                var currentPos = center.relative(back, i).relative(right, -j);
                var state = level.getBlockState(currentPos);
                if (state.isAir()) {
                    continue;
                }
                BakedModel model = brd.getBlockModel(state);

                mat4.translate(rightStep);

                List<BakedQuad> quads = new ArrayList<>();
                quads.addAll(ModelUtil.getBakedModelQuads(model, level, currentPos, state, up, rand));
                quads.addAll(ModelUtil.getBakedModelQuads(model, level, currentPos, state, up.getOpposite(), rand));
                if (i == 0) {
                    quads.addAll(
                            ModelUtil.getBakedModelQuads(model, level, currentPos, state, back.getOpposite(), rand));
                } else if (i == height - 1) {
                    quads.addAll(ModelUtil.getBakedModelQuads(model, level, currentPos, state, back, rand));
                }
                if (j == 0) {
                    quads.addAll(ModelUtil.getBakedModelQuads(model, level, currentPos, state, right, rand));
                } else if (j == width - 1) {
                    quads.addAll(
                            ModelUtil.getBakedModelQuads(model, level, currentPos, state, right.getOpposite(), rand));
                }

                for (BakedQuad bakedQuad : quads) {
                    buffer.getBuffer(RenderType.cutout()).putBulkData(pose, bakedQuad, 1, 1, 1, light,
                            combinedOverlay);
                }
            }
            mat4.translate(resetStep);
        }

        stack.popPose();
    }

    private void drawFace(LargeCombustionSetMachine machine, Matrix4f mat4, float angle, MultiBufferSource buffer,
                          Direction up, Direction back, Direction right, float radius) {
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

        vertexDirection(vc, mat4, right, up, back, -radius, -radius, 0).color(red, green, 0, alpha).uv(0, 0).uv2(0, 0)
                .normal(1, 0, 0).endVertex();
        vertexDirection(vc, mat4, right, up, back, radius, -radius, 0).color(0, green, 0, alpha).uv(0, 1).uv2(0, 1)
                .normal(0, 1, 0).endVertex();
        vertexDirection(vc, mat4, right, up, back, radius, radius, 0).color(red, green, blue, alpha).uv(1, 1).uv2(1, 1)
                .normal(1, 1, 1).endVertex();
        vertexDirection(vc, mat4, right, up, back, radius, radius, 0).color(red, green, blue, alpha).uv(1, 1).uv2(1, 1)
                .normal(1, 1, 1).endVertex();
        vertexDirection(vc, mat4, right, up, back, -radius, radius, 0).color(0f, 0, blue, alpha).uv(1, 0).uv2(1, 0)
                .normal(0, 0, 1).endVertex();
        vertexDirection(vc, mat4, right, up, back, -radius, -radius, 0).color(red, green, 0, alpha).uv(0, 0).uv2(0, 0)
                .normal(1, 0, 0).endVertex();
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
    // @Override
    // public boolean shouldRender(BlockEntity blockEntity, Vec3 cameraPos) {
    // return true;
    // }

    public record BlockPosFace(
                               BlockPos position,
                               Direction face) {}
}
