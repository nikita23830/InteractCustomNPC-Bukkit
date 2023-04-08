package com.nikita23830.cabhelper;

import com.nikita23830.cabhelper.helpers.ReceiveEventBuyItem;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public interface IInjection {

    boolean sendInteract(EntityPlayer player, EntityNPCInterface npc) throws Exception;
    ReceiveEventBuyItem sendBuyItem(EntityPlayer player, EntityNPCInterface npc, int id) throws Exception;
}
