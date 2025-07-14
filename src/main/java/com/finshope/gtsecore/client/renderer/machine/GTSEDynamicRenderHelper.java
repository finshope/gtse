package com.finshope.gtsecore.client.renderer.machine;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;

public class GTSEDynamicRenderHelper {

    public static DynamicRender<?, ?> createLargeCombustionRenderer() {
        return new LargeCombustionSetRenderer();
    }

    public static DynamicRender<?, ?> createPersonalBeaconRenderer() {
        return new PersonalBeaconRenderer();
    }
}
