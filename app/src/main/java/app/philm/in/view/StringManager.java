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

package app.philm.in.view;

import android.content.Context;

import app.philm.in.BuildConfig;
import app.philm.in.R;
import app.philm.in.controllers.AboutController;
import app.philm.in.controllers.MainController;
import app.philm.in.controllers.MovieController;
import app.philm.in.network.NetworkError;
import app.philm.in.util.AppUtils;

public class StringManager {

    public static int getStringResId(MovieController.DiscoverTab tab) {
        switch (tab) {
            case POPULAR:
                return R.string.popular_title;
            case IN_THEATRES:
                return R.string.in_theatres_title;
            case UPCOMING:
                return R.string.upcoming_title;
            case RECOMMENDED:
                return R.string.recommended_title;
        }
        return 0;
    }

    public static int getStringResId(MainController.SideMenuItem item) {
        switch (item) {
            case DISCOVER:
                return R.string.discover_title;
            case TRENDING:
                return R.string.trending_title;
            case LIBRARY:
                return R.string.library_title;
            case WATCHLIST:
                return R.string.watchlist_title;
            case SEARCH:
                return R.string.search_title;
        }
        return R.string.app_name;
    }

    public static int getStringResId(NetworkError error) {
        switch (error) {
            case UNAUTHORIZED_TRAKT:
                return R.string.error_unauthorized;
            case NETWORK_ERROR:
                return R.string.error_network;
            case NOT_FOUND_TRAKT:
                return R.string.error_movie_not_found_trakt;
            case NOT_FOUND_TMDB:
                return R.string.error_movie_not_found_tmdb;
            case UNKNOWN:
            default:
                return R.string.error_unknown;
        }
    }

    public static int getTitleResId(AboutController.AboutItem item) {
        switch (item) {
            case BUILD_VERSION:
                return R.string.about_build_version_title;
            case BUILD_TIME:
                return R.string.about_build_time_title;
            case OPEN_SOURCE:
                return R.string.about_open_source_title;
            case POWERED_BY_TMDB:
                return R.string.about_powered_tmdb_title;
            case POWERED_BY_TRAKT:
                return R.string.about_powered_trakt_title;
        }
        return 0;
    }

    public static String getSubtitle(Context context, AboutController.AboutItem item) {
        switch (item) {
            case BUILD_VERSION:
                return AppUtils.getVersionName();
            case BUILD_TIME:
                return BuildConfig.BUILD_TIME;
            case OPEN_SOURCE:
                return context.getString(R.string.about_open_source_content);
            case POWERED_BY_TMDB:
                return context.getString(R.string.about_powered_tmdb_content);
            case POWERED_BY_TRAKT:
                return context.getString(R.string.about_powered_trakt_content);
        }
        return null;
    }

}
