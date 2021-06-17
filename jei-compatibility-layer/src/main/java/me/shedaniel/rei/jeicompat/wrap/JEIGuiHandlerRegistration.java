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

package me.shedaniel.rei.jeicompat.wrap;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import mezz.jei.api.gui.handlers.*;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static me.shedaniel.rei.jeicompat.JEIPluginDetector.unwrap;
import static me.shedaniel.rei.jeicompat.JEIPluginDetector.wrap;

public enum JEIGuiHandlerRegistration implements IGuiHandlerRegistration {
    INSTANCE;
    
    @Override
    public <T extends AbstractContainerScreen<?>> void addGuiContainerHandler(@NotNull Class<? extends T> guiClass, @NotNull IGuiContainerHandler<T> guiHandler) {
        this.<T>add(guiClass, guiHandler::getGuiExtraAreas, guiHandler::getIngredientUnderMouse);
    }
    
    @Override
    public <T extends AbstractContainerScreen<?>> void addGenericGuiContainerHandler(@NotNull Class<? extends T> guiClass, @NotNull IGuiContainerHandler<?> guiHandler) {
        addGuiContainerHandler(guiClass, (IGuiContainerHandler<T>) guiHandler);
    }
    
    @Override
    public <T extends Screen> void addGuiScreenHandler(@NotNull Class<T> guiClass, @NotNull IScreenHandler<T> handler) {
        ScreenRegistry.getInstance().registerDecider(new DisplayBoundsProvider<T>() {
            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
                return guiClass.isAssignableFrom(screen);
            }
            
            @Override
            public Rectangle getScreenBounds(T screen) {
                IGuiProperties properties = handler.apply(screen);
                return new Rectangle(properties.getGuiLeft(), properties.getGuiTop(), properties.getGuiXSize(), properties.getGuiYSize());
            }
        });
    }
    
    @Override
    public void addGlobalGuiHandler(@NotNull IGlobalGuiHandler globalGuiHandler) {
        add(Screen.class, screen -> globalGuiHandler.getGuiExtraAreas(),
                (screen, mouseX, mouseY) -> globalGuiHandler.getIngredientUnderMouse(mouseX, mouseY));
    }
    
    private <T extends Screen> void add(Class<? extends T> screenClass, Function<T, Collection<Rect2i>> exclusionZones, PropertyDispatch.TriFunction<T, Double, Double, Object> focusedStack) {
        ScreenRegistry.getInstance().exclusionZones().register(screenClass, screen -> {
            return CollectionUtils.map(exclusionZones.apply(screen), rect2i -> new Rectangle(rect2i.getX(), rect2i.getY(), rect2i.getWidth(), rect2i.getHeight()));
        });
        ScreenRegistry.getInstance().registerFocusedStack((screen, mouse) -> {
            if (!screenClass.isInstance(screen)) return InteractionResultHolder.pass(EntryStack.empty());
            Object ingredient = focusedStack.apply((T) screen, (double) mouse.x, (double) mouse.y);
            if (ingredient == null) return InteractionResultHolder.pass(EntryStack.empty());
            
            return InteractionResultHolder.success(wrap(ingredient));
        });
    }
    
    @Override
    public <T extends AbstractContainerScreen<?>> void addRecipeClickArea(@NotNull Class<? extends T> guiContainerClass, int xPos, int yPos, int width, int height, @NotNull ResourceLocation @NotNull ... recipeCategoryUids) {
        ScreenRegistry.getInstance().registerContainerClickArea(new Rectangle(xPos, yPos, width, height), (Class<? extends AbstractContainerScreen<AbstractContainerMenu>>) guiContainerClass,
                Arrays.stream(recipeCategoryUids).map(CategoryIdentifier::of).toArray(CategoryIdentifier<?>[]::new));
    }
    
    @Override
    public <T extends Screen> void addGhostIngredientHandler(@NotNull Class<T> guiClass, @NotNull IGhostIngredientHandler<T> handler) {
        ScreenRegistry.getInstance().registerDraggableStackVisitor(new DraggableStackVisitor<T>() {
            @Override
            public Optional<Acceptor> visitDraggedStack(DraggingContext<T> context, DraggableStack stack) {
                List<IGhostIngredientHandler.Target<Object>> list = handler.getTargets(context.getScreen(), unwrap(stack.getStack()), true);
                for (IGhostIngredientHandler.Target<Object> target : list) {
                    if (target.getArea().contains(context.getCurrentPosition().x, context.getCurrentPosition().y)) {
                        return Optional.of(s -> {
                            target.accept(unwrap(s.getStack().copy()));
                        });
                    }
                }
                return Optional.empty();
            }
            
            @Override
            public <R extends Screen> boolean isHandingScreen(R screen) {
                return guiClass.isInstance(screen);
            }
        });
    }
}
