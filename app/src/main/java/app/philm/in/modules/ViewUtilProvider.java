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

package app.philm.in.modules;

import android.content.Context;
import android.content.res.AssetManager;

import java.text.DateFormat;

import javax.inject.Singleton;

import app.philm.in.adapters.MovieSectionedListAdapter;
import app.philm.in.adapters.PersonCreditSectionedListAdapter;
import app.philm.in.fragments.MovieDetailFragment;
import app.philm.in.fragments.PersonDetailFragment;
import app.philm.in.modules.library.ContextProvider;
import app.philm.in.modules.library.UtilProvider;
import app.philm.in.qualifiers.ApplicationContext;
import app.philm.in.util.FlagUrlProvider;
import app.philm.in.util.PhilmTypefaceSpan;
import app.philm.in.util.TypefaceManager;
import app.philm.in.view.AutofitTextView;
import app.philm.in.view.BackdropImageView;
import app.philm.in.view.ExpandingTextView;
import app.philm.in.view.FontTextView;
import app.philm.in.view.PhilmImageView;
import app.philm.in.view.RatingCircleView;
import dagger.Module;
import dagger.Provides;

@Module(
        includes =  {
                ContextProvider.class,
                UtilProvider.class
        },
        injects = {
                FontTextView.class,
                AutofitTextView.class,
                ExpandingTextView.class,
                PhilmTypefaceSpan.class,
                PhilmImageView.class,
                BackdropImageView.class,
                RatingCircleView.class,
                MovieSectionedListAdapter.class,
                PersonCreditSectionedListAdapter.class,
                MovieDetailFragment.class,
                PersonDetailFragment.class
        }
)
public class ViewUtilProvider {

    @Provides @Singleton
    public TypefaceManager provideTypefaceManager(AssetManager assetManager) {
        return new TypefaceManager(assetManager);
    }

    @Provides @Singleton
    public DateFormat provideMediumDateFormat(@ApplicationContext Context context) {
        return android.text.format.DateFormat.getMediumDateFormat(context);
    }

    @Provides @Singleton
    public FlagUrlProvider getFlagUrlProvider() {
        return new FlagUrlProvider();
    }

}
