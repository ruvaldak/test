package mezz.jei.api.recipe.transfer;

import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.recipe.transfer.IRecipeTransferError.Type;
import mezz.jei.api.registration.IRecipeTransferRegistration;

import java.util.Collection;

/**
 * Helper functions for implementing an {@link IRecipeTransferHandler}.
 * Get an instance from {@link IRecipeTransferRegistration#getTransferHelper()}.
 */
public interface IRecipeTransferHandlerHelper {
    /**
     * Create an error with {@link Type#INTERNAL}.
     * It is recommended that you also log a message to the console.
     */
    IRecipeTransferError createInternalError();
    
    /**
     * Create an error with type {@link Type#USER_FACING} that shows a tooltip.
     *
     * @param tooltipMessage the message to show on the tooltip for the recipe transfer button.
     */
    IRecipeTransferError createUserErrorWithTooltip(String tooltipMessage);
    
    /**
     * Create an error with type {@link Type#USER_FACING} that shows a tooltip and highlights missing item slots.
     *
     * @param tooltipMessage   the message to show on the tooltip for the recipe transfer button.
     * @param missingItemSlots the slot indexes for items that are missing. Must not be empty.
     *                         Slots are indexed according to {@link IGuiItemStackGroup#getGuiIngredients()}.
     */
    IRecipeTransferError createUserErrorForSlots(String tooltipMessage, Collection<Integer> missingItemSlots);
}
