package net.azisaba.petinventoryoffline.mypet;

import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetType;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import net.azisaba.petinventoryoffline.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MyPetClass {
    private static final Class<?> CLASS = Util.tryGet(() -> Class.forName("de.Keyle.MyPet.entity.MyPetClass"));
    private final Object o;

    public MyPetClass(@NotNull Object o) {
        this.o = o;
    }

    @NotNull
    public MyPet getNewMyPetInstance(@NotNull MyPetPlayer player) {
        try {
            return (MyPet) CLASS.getMethod("getNewMyPetInstance", MyPetPlayer.class).invoke(o, player);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("_ -> new")
    public static @NotNull MyPetClass getByMyPetType(@NotNull MyPetType type) {
        try {
            return new MyPetClass(CLASS.getMethod("getByMyPetType", MyPetType.class).invoke(null, type));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
