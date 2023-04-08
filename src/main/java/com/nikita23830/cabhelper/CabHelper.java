package com.nikita23830.cabhelper;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

@SideOnly(Side.SERVER)
@Mod(
        modid = CabHelper.MODID,
        name = "Cabinet Helper",
        version = "r1.0.1",
        acceptableRemoteVersions = "*",
        dependencies = "after:IC2;after:Forestry;after:ProjRed|Exploration;after:ThermalFoundation;after:TConstruct;after:GalacticraftCore;after:galaxyspace"
)
public class CabHelper {
    public static final String MODID = "CabHelper";

    public static HashMap<String, NBTTagCompound> chunks = new HashMap<String, NBTTagCompound>();
    public static boolean allRestore = false;
    public static int countAll = 0;

    public static Thread threadRestore = null;

    public CabHelper() {

    }
}
