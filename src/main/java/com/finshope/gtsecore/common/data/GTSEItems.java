package com.finshope.gtsecore.common.data;

import com.finshope.gtsecore.common.items.CompleteCardItemBehaviour;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.network.chat.Component;

import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;

public class GTSEItems {
    public static final ItemEntry<ComponentItem> COMPLETE_CARD = REGISTRATE.item("complete_card", ComponentItem::create)
            .lang("Complete card")
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.addAll(LangHandler.getMultiLang("item.gtse.complete_card.tooltip"));
            })))
            .onRegister(attach(new CompleteCardItemBehaviour()))
            .defaultModel()
            .register();

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }

    public static void init() {

    }
}
