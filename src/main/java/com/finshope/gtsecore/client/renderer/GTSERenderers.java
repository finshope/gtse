package com.finshope.gtsecore.client.renderer;

import com.finshope.gtsecore.client.renderer.machine.LargeCombustionSetRenderer;
import com.finshope.gtsecore.client.renderer.machine.PersonalBeaconRenderer;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;

public class GTSERenderers {

    public static void init() {
        DynamicRenderManager.register(GTCEu.id("large_combustion_set_renderer"), LargeCombustionSetRenderer.TYPE);
        DynamicRenderManager.register(GTCEu.id("personal_beacon_renderer"), PersonalBeaconRenderer.TYPE);
    }
}
