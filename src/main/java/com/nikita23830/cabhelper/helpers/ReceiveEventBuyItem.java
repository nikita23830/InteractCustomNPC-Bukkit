package com.nikita23830.cabhelper.helpers;

import net.minecraft.item.ItemStack;

public class ReceiveEventBuyItem {
    public final boolean cancelled;
    public final ItemStack receive;
    public ReceiveEventBuyItem(boolean cancelled, ItemStack receive) {
        this.cancelled = cancelled;
        this.receive = receive;
    }
}
