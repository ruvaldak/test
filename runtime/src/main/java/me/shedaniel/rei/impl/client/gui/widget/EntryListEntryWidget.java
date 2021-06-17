/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.impl.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.ClientHelper;
import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class EntryListEntryWidget extends EntryWidget {
    public int backupY;
    
    protected EntryListEntryWidget(Point point, int entrySize) {
        super(point);
        this.backupY = point.y;
        getBounds().width = getBounds().height = entrySize;
    }
    
    @Override
    protected void drawHighlighted(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (!getCurrentEntry().isEmpty())
            super.drawHighlighted(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public void queueTooltip(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (ClientHelper.getInstance().isCheating() && !minecraft.player.inventory.getCarried().isEmpty()) {
            return;
        }
        super.queueTooltip(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!interactable)
            return super.mouseReleased(mouseX, mouseY, button);
        if (containsMouse(mouseX, mouseY)) {
            if (wasClicked()) {
                if (doAction(mouseX, mouseY, button)) {
                    return true;
                } else {
                    wasClicked = true;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    protected boolean doAction(double mouseX, double mouseY, int button) {
        if (!ClientHelper.getInstance().isCheating()) return false;
        EntryStack<?> entry = getCurrentEntry().copy();
        if (!entry.isEmpty()) {
            if (entry.getValueType() == FluidStack.class) {
                FluidStack value = (FluidStack) entry.getValue();
                Item bucketItem = value.getFluid().getBucket();
                if (bucketItem != null) {
                    entry = EntryStacks.of(bucketItem);
                }
            }
            if (entry.getValueType() == ItemStack.class)
                entry.<ItemStack>cast().getValue().setCount(button != 1 && !Screen.hasShiftDown() ? 1 : ((ItemStack) entry.getValue()).getMaxStackSize());
            return ClientHelper.getInstance().tryCheatingEntry(entry);
        }
        
        return false;
    }
    
    @Override
    protected boolean cancelDeleteItems(EntryStack<?> stack) {
        if (!interactable || !ConfigObject.getInstance().isGrabbingItems())
            return super.cancelDeleteItems(stack);
        if (ClientHelper.getInstance().isCheating()) {
            EntryStack<?> entry = getCurrentEntry().copy();
            if (!entry.isEmpty()) {
                if (entry.getValueType() == FluidStack.class) {
                    FluidStack value = (FluidStack) entry.getValue();
                    Item bucketItem = value.getFluid().getBucket();
                    if (bucketItem != null) {
                        entry = EntryStacks.of(bucketItem);
                    }
                }
                return EntryStacks.equalsExact(entry, stack);
            }
        }
        return super.cancelDeleteItems(stack);
    }
}
