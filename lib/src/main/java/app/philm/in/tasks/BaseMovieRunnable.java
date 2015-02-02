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

package app.philm.in.tasks;

import com.jakewharton.trakt.Trakt;
import com.squareup.otto.Bus;
import com.uwetrottmann.tmdb.Tmdb;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import app.philm.in.model.PhilmMovie;
import app.philm.in.network.NetworkCallRunnable;
import app.philm.in.network.NetworkError;
import app.philm.in.state.AsyncDatabaseHelper;
import app.philm.in.state.BaseState;
import app.philm.in.state.MoviesState;
import app.philm.in.state.mappers.TmdbCastEntityMapper;
import app.philm.in.state.mappers.TmdbCrewEntityMapper;
import app.philm.in.state.mappers.TmdbMovieEntityMapper;
import app.philm.in.state.mappers.TmdbPersonEntityMapper;
import app.philm.in.state.mappers.TraktMovieEntityMapper;
import app.philm.in.util.CountryProvider;
import app.philm.in.util.PhilmCollections;
import dagger.Lazy;
import retrofit.RetrofitError;

public abstract class BaseMovieRunnable<R> extends NetworkCallRunnable<R> {

    static final String RESULT_TRAKT_SUCCESS = "success";
    static final String RESULT_TRAKT_FAILURE = "failure";

    @Inject MoviesState mMoviesState;

    @Inject Lazy<Tmdb> mLazyTmdbClient;
    @Inject Lazy<Trakt> mLazyTraktClient;
    @Inject Lazy<AsyncDatabaseHelper> mDbHelper;
    @Inject Lazy<TraktMovieEntityMapper> mLazyTraktMovieEntityMapper;
    @Inject Lazy<TmdbMovieEntityMapper> mLazyTmdbMovieEntityMapper;
    @Inject Lazy<TmdbCastEntityMapper> mLazyTmdbCastEntityMapper;
    @Inject Lazy<TmdbCrewEntityMapper> mLazyTmdbCrewEntityMapper;
    @Inject Lazy<TmdbPersonEntityMapper> mLazyTmdbPersonEntityMapper;
    @Inject Lazy<Bus> mEventBus;
    @Inject Lazy<CountryProvider> mCountryProvider;

    private final int mCallingId;

    public BaseMovieRunnable(int callingId) {
        mCallingId = callingId;
    }

    @Override
    public void onPreTraktCall() {
        getEventBus().post(createLoadingProgressEvent(true));
    }

    @Override
    public void onError(RetrofitError re) {
        getEventBus().post(new BaseState.OnErrorEvent(getCallingId(),
                NetworkError.from(re, getSource())));
    }

    protected abstract int getSource();

    @Override
    public void onFinished() {
        getEventBus().post(createLoadingProgressEvent(false));
    }

    protected void checkPhilmState(PhilmMovie movie) {
        final List<PhilmMovie> library = mMoviesState.getLibrary();
        final List<PhilmMovie> watchlist = mMoviesState.getWatchlist();

        if (!PhilmCollections.isEmpty(library)) {
            final boolean shouldBeInLibrary = movie.isWatched() || movie.inCollection();

            if (shouldBeInLibrary != library.contains(movie)) {
                if (shouldBeInLibrary) {
                    library.add(movie);
                    Collections.sort(library, PhilmMovie.COMPARATOR_SORT_TITLE);
                } else {
                    library.remove(movie);
                }
            }
        }

        if (!PhilmCollections.isEmpty(watchlist)) {
            final boolean shouldBeInWatchlist = movie.inWatchlist();
            if (shouldBeInWatchlist != watchlist.contains(movie)) {
                if (shouldBeInWatchlist) {
                    watchlist.add(movie);
                    Collections.sort(watchlist, PhilmMovie.COMPARATOR_SORT_TITLE);
                } else {
                    watchlist.remove(movie);
                }
            }
        }
    }

    protected Tmdb getTmdbClient() {
        return mLazyTmdbClient.get();
    }

    protected Trakt getTraktClient() {
        return mLazyTraktClient.get();
    }

    protected AsyncDatabaseHelper getDbHelper() {
        return mDbHelper.get();
    }

    protected TraktMovieEntityMapper getTraktMovieEntityMapper() {
        return mLazyTraktMovieEntityMapper.get();
    }

    protected TmdbMovieEntityMapper getTmdbMovieEntityMapper() {
        return mLazyTmdbMovieEntityMapper.get();
    }

    protected TmdbCastEntityMapper getTmdbCastEntityMapper() {
        return mLazyTmdbCastEntityMapper.get();
    }

    protected TmdbCrewEntityMapper getTmdbCrewEntityMapper() {
        return mLazyTmdbCrewEntityMapper.get();
    }

    protected TmdbPersonEntityMapper getTmdbPersonEntityMapper() {
        return mLazyTmdbPersonEntityMapper.get();
    }

    protected Bus getEventBus() {
        return mEventBus.get();
    }

    protected CountryProvider getCountryProvider() {
        return mCountryProvider.get();
    }

    protected int getCallingId() {
        return mCallingId;
    }

    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowLoadingProgressEvent(getCallingId(), show);
    }
}