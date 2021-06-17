package mezz.jei.api.ingredients.subtypes;

import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Gets subtype information from ingredients that have subtype interpreters.
 * <p>
 * Add subtypes for your ingredients with {@link ISubtypeRegistration#registerSubtypeInterpreter(Item, ISubtypeInterpreter)}.
 */
public interface ISubtypeManager {
    /**
     * Get the data from an itemStack that is relevant to comparing and telling subtypes apart.
     * Returns null if the itemStack has no information used for subtypes.
     *
     * @deprecated since JEI 7.3.0. Use {@link #getSubtypeInfo(ItemStack, UidContext)}
     */
    @Nullable
    @Deprecated
    String getSubtypeInfo(ItemStack itemStack);
    
    /**
     * Get the data from an itemStack that is relevant to comparing and telling subtypes apart.
     * Returns null if the itemStack has no information used for subtypes.
     *
     * @since JEI 7.3.0
     */
    @Nullable
    String getSubtypeInfo(ItemStack itemStack, UidContext context);
}
