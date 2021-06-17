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

package me.shedaniel.rei.plugin.client.entry;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.AbstractEntryRenderer;
import me.shedaniel.rei.api.client.entry.renderer.BatchEntryRenderer;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.ImmutableTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ItemEntryDefinition implements EntryDefinition<ItemStack>, EntrySerializer<ItemStack> {
    private final EntryRenderer<ItemStack> renderer = new ItemEntryRenderer();
    
    @Override
    public Class<ItemStack> getValueType() {
        return ItemStack.class;
    }
    
    @Override
    public EntryType<ItemStack> getType() {
        return VanillaEntryTypes.ITEM;
    }
    
    @Override
    public EntryRenderer<ItemStack> getRenderer() {
        return renderer;
    }
    
    @Override
    @Nullable
    public ResourceLocation getIdentifier(EntryStack<ItemStack> entry, ItemStack value) {
        return Registry.ITEM.getKey(value.getItem());
    }
    
    @Override
    public boolean isEmpty(EntryStack<ItemStack> entry, ItemStack value) {
        return value.isEmpty();
    }
    
    @Override
    public ItemStack copy(EntryStack<ItemStack> entry, ItemStack value) {
        return value.copy();
    }
    
    @Override
    public ItemStack normalize(EntryStack<ItemStack> entry, ItemStack value) {
        ItemStack copy = value.copy();
        copy.setCount(1);
        return copy;
    }
    
    @Override
    public long hash(EntryStack<ItemStack> entry, ItemStack value, ComparisonContext context) {
        int code = 1;
        code = 31 * code + System.identityHashCode(value.getItem());
        code = 31 * code + Long.hashCode(ItemComparatorRegistry.getInstance().hashOf(context, value));
        return code;
    }
    
    @Override
    public boolean equals(ItemStack o1, ItemStack o2, ComparisonContext context) {
        if (o1.getItem() != o2.getItem())
            return false;
        return ItemComparatorRegistry.getInstance().hashOf(context, o1) == ItemComparatorRegistry.getInstance().hashOf(context, o2);
    }
    
    @Override
    @Nullable
    public EntrySerializer<ItemStack> getSerializer() {
        return this;
    }
    
    @Override
    public boolean supportSaving() {
        return true;
    }
    
    @Override
    public boolean supportReading() {
        return true;
    }
    
    @Override
    public CompoundTag save(EntryStack<ItemStack> entry, ItemStack value) {
        return value.save(new CompoundTag());
    }
    
    @Override
    public ItemStack read(CompoundTag tag) {
        return ItemStack.of(tag);
    }
    
    private static final ReferenceSet<Item> SEARCH_BLACKLISTED = new ReferenceOpenHashSet<>();
    
    @Override
    public Component asFormattedText(EntryStack<ItemStack> entry, ItemStack value) {
        if (!SEARCH_BLACKLISTED.contains(value.getItem()))
            try {
                return value.getHoverName();
            } catch (Throwable e) {
                e.printStackTrace();
                SEARCH_BLACKLISTED.add(value.getItem());
            }
        try {
            return new ImmutableTextComponent(I18n.get("item." + Registry.ITEM.getKey(value.getItem()).toString().replace(":", ".")));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return new ImmutableTextComponent("ERROR");
    }
    
    @Override
    public Collection<ResourceLocation> getTagsFor(TagContainer tagContainer, EntryStack<ItemStack> entry, ItemStack value) {
        TagCollection<Item> collection = tagContainer.getItems();
        return collection == null ? Collections.emptyList() : collection.getMatchingTags(value.getItem());
    }
    
    private List<Component> tryGetItemStackToolTip(EntryStack<ItemStack> entry, ItemStack value, boolean careAboutAdvanced) {
        if (!SEARCH_BLACKLISTED.contains(value.getItem()))
            try {
                return value.getTooltipLines(Minecraft.getInstance().player, careAboutAdvanced && Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            } catch (Throwable e) {
                e.printStackTrace();
                SEARCH_BLACKLISTED.add(value.getItem());
            }
        return Lists.newArrayList(asFormattedText(entry, value));
    }
    
    @SuppressWarnings("deprecation")
    public class ItemEntryRenderer extends AbstractEntryRenderer<ItemStack> implements BatchEntryRenderer<ItemStack> {
        @Override
        public int getBatchId(EntryStack<ItemStack> entry) {
            return 1738923 + (getModelFromStack(entry.getValue()).usesBlockLight() ? 1 : 0);
        }
        
        private BakedModel getModelFromStack(ItemStack stack) {
            return Minecraft.getInstance().getItemRenderer().getModel(stack, null, null);
        }
        
        @Override
        public void startBatch(EntryStack<ItemStack> entry, PoseStack matrices, float delta) {
            Minecraft.getInstance().getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
            Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
            RenderSystem.pushMatrix();
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean sideLit = getModelFromStack(entry.getValue()).usesBlockLight();
            if (!sideLit)
                Lighting.setupForFlatItems();
        }
        
        @Override
        public void renderBase(EntryStack<ItemStack> entry, PoseStack matrices, MultiBufferSource.BufferSource immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            if (!entry.isEmpty()) {
                ItemStack stack = entry.getValue();
                matrices.pushPose();
                matrices.translate(bounds.getCenterX(), bounds.getCenterY(), 100.0F + entry.getZ());
                matrices.scale(bounds.getWidth(), (bounds.getWidth() + bounds.getHeight()) / -2f, bounds.getHeight());
                Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, matrices, immediate, 15728880, OverlayTexture.NO_OVERLAY, getModelFromStack(stack));
                matrices.popPose();
            }
        }
        
        @Override
        public void renderOverlay(EntryStack<ItemStack> entry, PoseStack matrices, MultiBufferSource.BufferSource immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            if (!entry.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().blitOffset = entry.getZ();
                Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, entry.getValue(), bounds.x, bounds.y, null);
                Minecraft.getInstance().getItemRenderer().blitOffset = 0.0F;
            }
        }
        
        @Override
        public void endBatch(EntryStack<ItemStack> entry, PoseStack matrices, float delta) {
            RenderSystem.enableDepthTest();
            //RenderSystem.disableAlphaTest();
            RenderSystem.disableRescaleNormal();
            boolean sideLit = getModelFromStack(entry.getValue()).usesBlockLight();
            if (!sideLit)
                Lighting.setupFor3DItems();
            RenderSystem.popMatrix();
        }
        
        @Override
        @Nullable
        public Tooltip getTooltip(EntryStack<ItemStack> entry, Point mouse) {
            if (entry.isEmpty())
                return null;
            return Tooltip.create(tryGetItemStackToolTip(entry, entry.getValue(), true));
        }
    }
}
