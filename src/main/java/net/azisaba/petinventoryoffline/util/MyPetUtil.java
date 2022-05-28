package net.azisaba.petinventoryoffline.util;

import de.Keyle.MyPet.api.entity.StoredMyPet;
import de.Keyle.MyPet.api.util.NBTStorage;
import org.jetbrains.annotations.NotNull;

public class MyPetUtil {
    public static Object getSkillInfo(@NotNull Object storedMyPet) {
        try {
            return StoredMyPet.class.getMethod("getSkillInfo").invoke(storedMyPet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    public static void NBTStorage_load(@NotNull Object nbtStorage, @NotNull Object nbtTagCompound) {
        try {
            NBTStorage.class.getMethod("load", nbtTagCompound.getClass()).invoke(nbtStorage, nbtTagCompound);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
