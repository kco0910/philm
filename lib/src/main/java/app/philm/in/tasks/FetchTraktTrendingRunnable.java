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

import com.jakewharton.trakt.entities.Movie;

import java.util.List;

import app.philm.in.network.NetworkError;
import app.philm.in.util.PhilmCollections;
import retrofit.RetrofitError;

public class FetchTraktTrendingRunnable extends BaseMovieRunnable<List<Movie>> {

    public FetchTraktTrendingRunnable(int callingId) {
        super(callingId);
    }

    @Override
    public List<Movie> doBackgroundCall() throws RetrofitError {
        return getTraktClient().moviesService().trending();
    }

    @Override
    public void onSuccess(List<Movie> result) {
        if (!PhilmCollections.isEmpty(result)) {
            mMoviesState.setTrending(getTraktMovieEntityMapper().mapAll(result));
        } else {
            mMoviesState.setTrending(null);
        }
    }

    @Override
    protected int getSource() {
        return NetworkError.SOURCE_TRAKT;
    }

}