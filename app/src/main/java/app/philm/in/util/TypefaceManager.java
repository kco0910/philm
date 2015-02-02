/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.philm.in.util;

import com.google.common.base.Preconditions;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.util.LruCache;

public class TypefaceManager {

    private static final String ROBOTO_LIGHT_FILENAME = "Roboto-Light.ttf";
    private static final String ROBOTO_CONDENSED_FILENAME = "RobotoCondensed-Regular.ttf";
    private static final String ROBOTO_CONDENSED_BOLD_FILENAME = "RobotoCondensed-Bold.ttf";
    private static final String ROBOTO_CONDENSED_LIGHT_FILENAME = "RobotoCondensed-Light.ttf";
    private static final String ROBOTO_SLAB_FILENAME = "RobotoSlab-Regular.ttf";

    private static final String ROBOTO_LIGHT_NATIVE_FONT_FAMILY = "sans-serif-light";
    private static final String ROBOTO_CONDENSED_NATIVE_FONT_FAMILY = "sans-serif-condensed";

    private final LruCache<String, Typeface> mCache;
    private final AssetManager mAssetManager;

    public TypefaceManager(AssetManager assetManager) {
        mAssetManager = Preconditions.checkNotNull(assetManager, "assetManager cannot be null");
        mCache = new LruCache<>(3);
    }

    public Typeface getRobotoLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_LIGHT_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_LIGHT_FILENAME);
    }

    public Typeface getRobotoCondensed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_CONDENSED_FILENAME);
    }

    public Typeface getRobotoCondensedBold() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.BOLD);
        }
        return getTypeface(ROBOTO_CONDENSED_BOLD_FILENAME);
    }

    public Typeface getRobotoCondensedLight() {
        return getTypeface(ROBOTO_CONDENSED_LIGHT_FILENAME);
    }

    public Typeface getRobotoSlab() {
        return getTypeface(ROBOTO_SLAB_FILENAME);
    }

    private Typeface getTypeface(final String filename) {
        Typeface typeface = mCache.get(filename);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mAssetManager, "fonts/" + filename);
            mCache.put(filename, typeface);
        }
        return typeface;
    }
}
