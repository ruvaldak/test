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

package me.shedaniel.rei.impl.client.favorites;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import me.shedaniel.rei.api.client.favorites.FavoriteEntry;
import me.shedaniel.rei.api.client.favorites.FavoriteEntryType;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class FavoriteEntryTypeRegistryImpl implements FavoriteEntryType.Registry {
    private final BiMap<ResourceLocation, FavoriteEntryType<?>> registry = HashBiMap.create();
    private final Map<Component, FavoriteEntryType.Section> sections = Maps.newLinkedHashMap();
    
    @Override
    public void acceptPlugin(REIClientPlugin plugin) {
        plugin.registerFavorites(this);
    }
    
    @Override
    public void register(ResourceLocation id, FavoriteEntryType<?> type) {
        this.registry.put(id, type);
    }
    
    @Override
    public <A extends FavoriteEntry> @Nullable FavoriteEntryType<A> get(ResourceLocation id) {
        return (FavoriteEntryType<A>) this.registry.get(id);
    }
    
    @Override
    @Nullable
    public ResourceLocation getId(FavoriteEntryType<?> type) {
        return this.registry.inverse().get(type);
    }
    
    @Override
    public FavoriteEntryType.Section getOrCrateSection(Component text) {
        return sections.computeIfAbsent(text, SectionImpl::new);
    }
    
    @Override
    public Iterable<FavoriteEntryType.Section> sections() {
        return this.sections.values();
    }
    
    @Override
    public void startReload() {
        this.registry.clear();
        this.sections.clear();
    }
    
    private static class SectionImpl implements FavoriteEntryType.Section {
        private final Component text;
        private final List<FavoriteEntry> entries = new ArrayList<>();
        
        public SectionImpl(Component text) {
            this.text = text;
        }
        
        @Override
        public void add(FavoriteEntry... entries) {
            Collections.addAll(this.entries, entries);
        }
        
        @Override
        public Component getText() {
            return text;
        }
        
        @Override
        public List<FavoriteEntry> getEntries() {
            return entries;
        }
    }
}
