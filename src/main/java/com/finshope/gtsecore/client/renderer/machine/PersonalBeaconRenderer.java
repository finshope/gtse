package com.finshope.gtsecore.client.renderer.machine;

import com.finshope.gtsecore.common.machine.multiblock.electric.PersonalBeaconMachine;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;

import java.util.List;

import static net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_LOCATION;

public class PersonalBeaconRenderer extends DynamicRender<PersonalBeaconMachine, PersonalBeaconRenderer> {

    public static final Codec<PersonalBeaconRenderer> CODEC = Codec.unit(PersonalBeaconRenderer::new);
    public static final DynamicRenderType<PersonalBeaconMachine, PersonalBeaconRenderer> TYPE = new DynamicRenderType<>(
            PersonalBeaconRenderer.CODEC);

    private void renderMachine(PersonalBeaconMachine machine, float partialTicks,
                               PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        var pos = machine.getPos();
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var right = RelativeDirection.RIGHT.getRelative(front, upwards, flipped);
        var beamColor = new float[] {
                0f, 0.5f, 1f
        };

        var level = machine.getLevel();
        if (level == null) {
            return; // Ensure the level is loaded before rendering
        }

        stack.pushPose();

        var mat4 = stack.last().pose();
        final int radius = 4;
        final int height = 5;
        final int dimension = radius * 2;
        List<BlockPos> renderPos = ImmutableList.of(
                pos.relative(up, height).relative(right, radius).subtract(pos),
                pos.relative(up, height).relative(right, radius).relative(back, dimension).subtract(pos),
                pos.relative(up, height).relative(right, -radius).subtract(pos),
                pos.relative(up, height).relative(right, -radius).relative(back, dimension).subtract(pos));
        mat4.translate(0, 0.5f, 0);
        BlockPos prevOffset = null;
        for (var beaconPos : renderPos) {
            var currOffset = prevOffset == null ? beaconPos : beaconPos.subtract(prevOffset);
            prevOffset = beaconPos;
            mat4.translate(currOffset.getX(), currOffset.getY(), currOffset.getZ());

            BeaconRenderer.renderBeaconBeam(
                    stack,
                    buffer,
                    BEAM_LOCATION,
                    partialTicks,
                    1.0F,
                    level.getGameTime(),
                    0,
                    256,
                    beamColor,
                    0.2f,
                    0.25f);
        }

        stack.popPose();
    }

    @Override
    public DynamicRenderType<PersonalBeaconMachine, PersonalBeaconRenderer> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(PersonalBeaconMachine machine, Vec3 cameraPos) {
        return machine.recipeLogic.isWorking();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(PersonalBeaconMachine personalBeaconMachine, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        renderMachine(personalBeaconMachine, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }
}
