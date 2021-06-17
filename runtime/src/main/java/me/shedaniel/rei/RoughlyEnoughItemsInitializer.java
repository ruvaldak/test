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

package me.shedaniel.rei;

import me.shedaniel.architectury.annotations.ExpectPlatform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RoughlyEnoughItemsInitializer {
    public static void onInitialize() {
        checkMods();
        
        if (RoughlyEnoughItemsState.getErrors().isEmpty()) {
            initializeEntryPoint(false, "me.shedaniel.rei.RoughlyEnoughItemsCore");
        }
    }
    
    public static void onInitializeClient() {
        if (RoughlyEnoughItemsState.getErrors().isEmpty()) {
            initializeEntryPoint(true, "me.shedaniel.rei.RoughlyEnoughItemsCore");
            initializeEntryPoint(true, "me.shedaniel.rei.REIModMenuEntryPoint");
            initializeEntryPoint(true, "me.shedaniel.rei.impl.client.ClientHelperImpl");
            initializeEntryPoint(true, "me.shedaniel.rei.impl.client.REIHelperImpl");
        }
        
        initializeEntryPoint(true, "me.shedaniel.rei.impl.client.ErrorDisplayer");
    }
    
    public static void initializeEntryPoint(boolean client, String className) {
        try {
            Class<?> name = Class.forName(className);
            Object instance = name.getConstructor().newInstance();
            Method method = null;
            if (client) {
                if (isClient()) {
                    try {
                        method = name.getDeclaredMethod("onInitializeClient");
                    } catch (NoSuchMethodException ignored) {
                    }
                    if (method != null) {
                        method.invoke(instance);
                    }
                }
            } else {
                try {
                    method = name.getDeclaredMethod("onInitialize");
                } catch (NoSuchMethodException ignored) {
                }
                if (method != null) {
                    method.invoke(instance);
                }
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    @ExpectPlatform
    public static boolean isClient() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static void checkMods() {
        throw new AssertionError();
    }
}