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

package me.shedaniel.rei.impl.client.config.entries;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.rei.impl.client.entry.filtering.FilteringRule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class FilteringRuleOptionsScreen<T extends FilteringRule<?>> extends Screen {
    private final FilteringEntry entry;
    private RulesList rulesList;
    Screen parent;
    public T rule;
    
    public FilteringRuleOptionsScreen(FilteringEntry entry, T rule, Screen screen) {
        super(new TranslatableComponent("config.roughlyenoughitems.filteringRulesScreen"));
        this.entry = entry;
        this.rule = rule;
        this.parent = screen;
    }
    
    @Override
    public void init() {
        super.init();
        if (rulesList != null) save();
        {
            Component doneText = new TranslatableComponent("gui.done");
            int width = Minecraft.getInstance().font.width(doneText);
            addButton(new Button(this.width - 4 - width - 10, 4, width + 10, 20, doneText, button -> {
                save();
                minecraft.setScreen(parent);
            }));
        }
        rulesList = addWidget(new RulesList(minecraft, width, height, 30, height, BACKGROUND_LOCATION));
        addEntries(ruleEntry -> rulesList.addItem(ruleEntry));
    }
    
    public abstract void addEntries(Consumer<RuleEntry> entryConsumer);
    
    public abstract void save();
    
    public void addText(Consumer<RuleEntry> entryConsumer, FormattedText text) {
        for (FormattedCharSequence s : font.split(text, width - 80)) {
            entryConsumer.accept(new TextRuleEntry(rule, s));
        }
    }
    
    public void addEmpty(Consumer<RuleEntry> entryConsumer, int height) {
        entryConsumer.accept(new EmptyRuleEntry(rule, height));
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.rulesList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        this.font.drawShadow(matrices, this.title.getVisualOrderText(), this.width / 2.0F - this.font.width(this.title) / 2.0F, 12.0F, -1);
    }
    
    public static class RulesList extends DynamicElementListWidget<RuleEntry> {
        public RulesList(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
            super(client, width, height, top, bottom, backgroundLocation);
        }
        
        @Override
        protected int addItem(RuleEntry item) {
            return super.addItem(item);
        }
        
        @Override
        public int getItemWidth() {
            return width - 40;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return width - 14;
        }
    }
    
    public static abstract class RuleEntry extends DynamicElementListWidget.ElementEntry<RuleEntry> {
        private final FilteringRule<?> rule;
        
        public RuleEntry(FilteringRule<?> rule) {
            this.rule = rule;
        }
        
        public FilteringRule<?> getRule() {
            return rule;
        }
    }
    
    public static class TextRuleEntry extends RuleEntry {
        private final FormattedCharSequence text;
        
        public TextRuleEntry(FilteringRule<?> rule, FormattedCharSequence text) {
            super(rule);
            this.text = text;
        }
        
        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            Minecraft.getInstance().font.drawShadow(matrices, text, x + 5, y, -1);
        }
        
        @Override
        public int getItemHeight() {
            return 12;
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }
    
    public static class EmptyRuleEntry extends RuleEntry {
        private final int height;
        
        public EmptyRuleEntry(FilteringRule<?> rule, int height) {
            super(rule);
            this.height = height;
        }
        
        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        }
        
        @Override
        public int getItemHeight() {
            return height;
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }
    
    public static class TextFieldRuleEntry extends RuleEntry {
        private final EditBox widget;
        
        public TextFieldRuleEntry(int width, FilteringRule<?> rule, Consumer<EditBox> widgetConsumer) {
            super(rule);
            this.widget = new EditBox(Minecraft.getInstance().font, 0, 0, width, 18, Component.nullToEmpty(""));
            widgetConsumer.accept(widget);
        }
        
        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            widget.x = x + 2;
            widget.y = y + 2;
            widget.render(matrices, mouseX, mouseY, delta);
        }
        
        @Override
        public int getItemHeight() {
            return 20;
        }
        
        public EditBox getWidget() {
            return widget;
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(widget);
        }
    }
    
    public static class BooleanRuleEntry extends RuleEntry {
        private boolean b;
        private final Button widget;
        
        public BooleanRuleEntry(int width, boolean b, FilteringRule<?> rule, Function<Boolean, Component> textFunction) {
            super(rule);
            this.b = b;
            this.widget = new Button(0, 0, 100, 20, textFunction.apply(b), button -> {
                this.b = !this.b;
                button.setMessage(textFunction.apply(this.b));
            });
        }
        
        public boolean getBoolean() {
            return b;
        }
        
        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            widget.x = x + 2;
            widget.y = y;
            widget.render(matrices, mouseX, mouseY, delta);
        }
        
        @Override
        public int getItemHeight() {
            return 20;
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(widget);
        }
    }
}
