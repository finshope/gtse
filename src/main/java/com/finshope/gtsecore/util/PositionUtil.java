package com.finshope.gtsecore.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PositionUtil {

    /**
     * Suppose the offset is (dx, dy, dz) when facing south, calculate the offset when facing other directions.
     *
     * @param facing the direction to face
     * @param pos    the original position
     * @param dx
     * @param dy
     * @param dz
     * @return the new position
     */
    public static BlockPos OffsetWithDirection(Direction facing, BlockPos pos, int dx, int dy, int dz) {
        return switch (facing) {
            case NORTH -> pos.offset(-dx, dy, -dz);
            case WEST -> pos.offset(-dz, dy, dx);
            case EAST -> pos.offset(dz, dy, -dx);
            case UP -> pos.offset(dx, dz, dy);
            case DOWN -> pos.offset(dx, -dz, -dy);
            default -> pos.offset(dx, dy, dz);
        };
    }
}
