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

package me.shedaniel.rei.api.common.transfer.info;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.registry.Reloadable;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Predicate;

/**
 * Registry for {@link MenuInfo}, for the default REI {@link me.shedaniel.rei.api.client.registry.transfer.TransferHandler}.
 */
public interface MenuInfoRegistry extends Reloadable<REIServerPlugin> {
    static MenuInfoRegistry getInstance() {
        return PluginManager.getServerInstance().get(MenuInfoRegistry.class);
    }
    
    <C extends AbstractContainerMenu, D extends Display> void register(CategoryIdentifier<D> category, Class<C> menuClass, MenuInfo<C, D> menuInfo);
    
    <D extends Display> void registerGeneric(Predicate<CategoryIdentifier<?>> categoryPredicate, MenuInfo<?, D> menuInfo);
    
    <C extends AbstractContainerMenu, D extends Display> MenuInfo<C, D> get(CategoryIdentifier<D> category, Class<C> menuClass);
    
    int infoSize();
}
