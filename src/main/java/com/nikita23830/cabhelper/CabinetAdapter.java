package com.nikita23830.cabhelper;

import com.gamerforea.eventhelper.util.ConvertUtils;
import com.nikita23830.cabhelper.helpers.ReceiveEventBuyItem;
import com.nikita23830.customnpcs.CustomNPCEvents;
import com.nikita23830.customnpcs.events.BuyItem;
import com.nikita23830.customnpcs.events.Interact;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.roles.RoleTrader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CabinetAdapter {
    public static CabinetAdapter instance = new CabinetAdapter();
    public static IInjection injection;
    private static boolean loading = false;
    private static boolean inject = false;

    public static void init() throws IllegalAccessException, InstantiationException {
        Class<?> clazz = InjectionUtils.injectClass("WorldGuard", CabinetAdapter.class);
        injection = (IInjection) clazz.newInstance();
    }

    public static boolean autoInject() throws InstantiationException, IllegalAccessException {
        if (inject)
            return true;

        if (injection == null && !loading) {
            loading = true;
            CabinetAdapter.init();
            inject = true;
            return true;
        } else if (injection == null) {
            return true;
        }
        System.out.println("Can't connect to plugin");
        loading = false;
        return false;
    }

    public static boolean checkInject() throws InstantiationException, IllegalAccessException {
        autoInject();
        return inject;
    }

    public static boolean sendInteract(EntityPlayer player, EntityNPCInterface npc) {
        try {
            checkInject();
            return injection.sendInteract(player, npc);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ReceiveEventBuyItem sendBuyItem(EntityPlayer player, EntityNPCInterface npc, int id) {
        try {
            checkInject();
            return injection.sendBuyItem(player, npc, id);
        } catch (Exception e) {
            return null;
        }
    }

    public static net.minecraft.item.ItemStack clone(ItemStack stack) throws Exception {
        Class clzStack = ReflectionHelper.getClass(MinecraftServer.class.getClassLoader(), "org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack");
        return (net.minecraft.item.ItemStack) clzStack.getMethod("asNMSCopy", ItemStack.class).invoke(clzStack, stack);
    }

    public static final class Inj implements IInjection {

        @Override
        public boolean sendInteract(EntityPlayer player, EntityNPCInterface npc) throws Exception {
            Player p = ConvertUtils.toBukkitEntity(player);
            Location l = new Location(ConvertUtils.toBukkitWorld(npc.worldObj), npc.posX, npc.posY, npc.posZ);
            Interact event = new Interact(npc.display.name, npc.display.title, l, p);
            Bukkit.getPluginManager().callEvent(event);
            return !event.isCancelled();
        }

        @Override
        public ReceiveEventBuyItem sendBuyItem(EntityPlayer player, EntityNPCInterface npc, int id) throws Exception {
            Player p = ConvertUtils.toBukkitEntity(player);
            Location l = new Location(ConvertUtils.toBukkitWorld(npc.worldObj), npc.posX, npc.posY, npc.posZ);
            RoleInterface role = npc.roleInterface;
            if (!(role instanceof RoleTrader))
                return null;
            RoleTrader trader = (RoleTrader) role;
            ItemStack[] sold = new ItemStack[trader.inventorySold.getSizeInventory()];
            for (int i = 0; i < sold.length; ++i) {
                sold[i] = ConvertUtils.toBukkitItemStackMirror(trader.inventorySold.getStackInSlot(i));
            }
            org.bukkit.inventory.Inventory soldInv = CustomNPCEvents.createInventory(sold);

            ItemStack[] purchases = new ItemStack[trader.inventoryCurrency.getSizeInventory()];
            for (int i = 0; i < purchases.length; ++i) {
                purchases[i] = ConvertUtils.toBukkitItemStackMirror(trader.inventoryCurrency.getStackInSlot(i));
            }
            org.bukkit.inventory.Inventory purchasesInv = CustomNPCEvents.createInventory(purchases);
            BuyItem event = new BuyItem(npc.display.name, npc.display.title, l, p, id, soldInv, purchasesInv);
            Bukkit.getPluginManager().callEvent(event);

            net.minecraft.item.ItemStack receive = event.getReceivePlayer() == null || event.getReceivePlayer().getType() == Material.AIR ? null : CabinetAdapter.clone(event.getReceivePlayer());
            return new ReceiveEventBuyItem(event.isCancelled(), receive);
        }
    }
}
