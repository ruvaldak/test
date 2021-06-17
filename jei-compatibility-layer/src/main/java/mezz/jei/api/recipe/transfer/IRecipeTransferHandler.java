package mezz.jei.api.recipe.transfer;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

/**
 * A recipe transfer handler moves items into a crafting area, based on the items in a recipe.
 * <p>
 * Implementing this interface gives full control over the recipe transfer process.
 * Mods that use a regular slotted inventory can use {@link IRecipeTransferInfo} instead, which is much simpler.
 * <p>
 * Useful functions for implementing a recipe transfer handler can be found in {@link IRecipeTransferHandlerHelper}.
 * <p>
 * To register your recipe transfer handler, use {@link IRecipeTransferRegistration#addRecipeTransferHandler(IRecipeTransferHandler, ResourceLocation)}
 */
public interface IRecipeTransferHandler<C extends AbstractContainerMenu> {
    /**
     * The container that this recipe transfer handler can use.
     */
    Class<C> getContainerClass();
    
    /**
     * @param container    the container to act on
     * @param recipe       the raw recipe instance
     * @param recipeLayout the layout of the recipe, with information about the ingredients
     * @param player       the player, to do the slot manipulation
     * @param maxTransfer  if true, transfer as many items as possible. if false, transfer one set
     * @param doTransfer   if true, do the transfer. if false, check for errors but do not actually transfer the items
     * @return a recipe transfer error if the recipe can't be transferred. Return null on success.
     * @since JEI 7.1.3
     */
    @Nullable
    default IRecipeTransferError transferRecipe(C container, Object recipe, IRecipeLayout recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
        return transferRecipe(container, recipeLayout, player, maxTransfer, doTransfer);
    }
    
    /**
     * @param container    the container to act on
     * @param recipeLayout the layout of the recipe, with information about the ingredients
     * @param player       the player, to do the slot manipulation
     * @param maxTransfer  if true, transfer as many items as possible. if false, transfer one set
     * @param doTransfer   if true, do the transfer. if false, check for errors but do not actually transfer the items
     * @return a recipe transfer error if the recipe can't be transferred. Return null on success.
     * @deprecated since JEI 7.1.3. Use {@link #transferRecipe(AbstractContainerMenu, Object, IRecipeLayout, Player, boolean, boolean)}
     */
    @Deprecated
    @Nullable
    default IRecipeTransferError transferRecipe(C container, IRecipeLayout recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
        return null;
    }
}
