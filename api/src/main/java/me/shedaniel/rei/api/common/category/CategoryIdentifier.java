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

package me.shedaniel.rei.api.common.category;

import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.Identifiable;
import me.shedaniel.rei.impl.Internals;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface CategoryIdentifier<D extends Display> extends Identifiable {
    static <D extends Display> CategoryIdentifier<D> of(String str) {
        return of(new ResourceLocation(str));
    }
    
    static <D extends Display> CategoryIdentifier<D> of(String namespace, String path) {
        return of(new ResourceLocation(namespace, path));
    }
    
    static <D extends Display> CategoryIdentifier<D> of(ResourceLocation identifier) {
        return Internals.getCategoryIdentifier(identifier);
    }
    
    default String getNamespace() {
        return getIdentifier().getNamespace();
    }
    
    default String getPath() {
        return getIdentifier().getPath();
    }
}
