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

package me.shedaniel.rei.api.common.plugins;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.fluid.FluidSupportProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;

@ApiStatus.OverrideOnly
public interface REIPlugin<P extends REIPlugin<?>> extends Comparable<REIPlugin<P>>, REIPluginProvider<P> {
    /**
     * @return the priority of the plugin, the smaller the number, the earlier it is called.
     */
    default int getPriority() {
        return 0;
    }
    
    default String getPluginName() {
        Class<?> self = getClass();
        String simpleName = self.getSimpleName();
        return simpleName == null ? self.getName() : simpleName;
    }
    
    @Override
    default int compareTo(REIPlugin o) {
        return Double.compare(getPriority(), o.getPriority());
    }
    
    /**
     * Registers new types of entries
     */
    @ApiStatus.OverrideOnly
    default void registerEntryTypes(EntryTypeRegistry registry) {
    }
    
    /**
     * Registers item comparators for identifying variants of {@link net.minecraft.world.item.ItemStack}.
     *
     * @see ItemComparatorRegistry
     */
    @ApiStatus.OverrideOnly
    default void registerItemComparators(ItemComparatorRegistry registry) {
    }
    
    /**
     * Registers new item to fluid support providers.
     *
     * @param support the support registry
     */
    @ApiStatus.OverrideOnly
    default void registerFluidSupport(FluidSupportProvider support) {
    }
    
    /**
     * Registers serializers to displays.
     */
    @ApiStatus.OverrideOnly
    default void registerDisplaySerializer(DisplaySerializerRegistry registry) {
    }
    
    @ApiStatus.OverrideOnly
    default void preRegister() {
    }
    
    @ApiStatus.OverrideOnly
    default void postRegister() {
    }
    
    @Override
    default Collection<P> provide() {
        return Collections.singletonList((P) this);
    }
}
