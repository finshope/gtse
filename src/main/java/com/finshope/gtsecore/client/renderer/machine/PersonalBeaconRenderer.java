package com.finshope.gtsecore.client.renderer.machine;

import com.finshope.gtsecore.common.machine.multiblock.electric.PersonalBeaconMachine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.List;

import static net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_LOCATION;

public class PersonalBeaconRenderer extends WorkableCasingMachineRenderer {

    public PersonalBeaconRenderer(ResourceLocation baseCasing, ResourceLocation workableModel) {
        super(baseCasing, workableModel);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof IMachineBlockEntity machineBlockEntity &&
                machineBlockEntity.getMetaMachine() instanceof PersonalBeaconMachine machine) {
            if (!machine.isActive()) {
                return; // Skip rendering if the machine is not active
            }
            renderMachine(machine, blockEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);
        }
    }

    private void renderMachine(PersonalBeaconMachine machine, BlockEntity blockEntity, float partialTicks,
                               PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        var pos = blockEntity.getBlockPos();
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var up = RelativeDirection.UP.getRelativeFacing(front, upwards, flipped);
        var back = RelativeDirection.BACK.getRelativeFacing(front, upwards, flipped);
        var right = RelativeDirection.RIGHT.getRelativeFacing(front, upwards, flipped);
        var beamColor = new float[]{
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
}
