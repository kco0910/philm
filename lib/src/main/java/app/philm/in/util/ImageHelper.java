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
import com.google.common.base.Verify;

import java.net.URLEncoder;

import app.philm.in.model.PhilmMovie;
import app.philm.in.model.PhilmMovieVideo;
import app.philm.in.model.PhilmPerson;
import app.philm.in.model.PhilmPersonCredit;

public class ImageHelper {

    private static final boolean RESIZE_ALL = false;

    private static final int[] TRAKT_POSTER_SIZES = { 138, 300 };
    private static final int[] TRAKT_BACKDROP_SIZES = { 218, 940 };

    private static final String YOUTUBE_URL_BASE = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_MEDIUM_Q_FILENAME = "mqdefault.jpg";
    private static final int YOUTUBE_MEDIUM_Q_WIDTH = 180;
    private static final String YOUTUBE_HIGH_Q_FILENAME = "hqdefault.jpg";
    private static final int YOUTUBE_HIGH_Q_WIDTH = 480;

    private String mTmdbBaseUrl;
    private int[] mTmdbPosterSizes;
    private int[] mTmdbBackdropSizes;
    private int[] mTmdbProfileSizes;

    public void setTmdbBackdropSizes(int[] tmdbBackdropSizes) {
        mTmdbBackdropSizes = tmdbBackdropSizes;
    }

    public void setTmdbPosterSizes(int[] tmdbPosterSizes) {
        mTmdbPosterSizes = tmdbPosterSizes;
    }

    public void setTmdbBaseUrl(String baseUrl) {
        mTmdbBaseUrl = baseUrl;
    }

    public void setTmdbProfileSizes(int[] tmdbProfileSizes) {
        mTmdbProfileSizes = tmdbProfileSizes;
    }

    public String getPosterUrl(final PhilmPersonCredit credit, final int width, final int height) {
        final String imageUrl = credit.getPosterPath();
        Preconditions.checkNotNull(imageUrl, "movie must have poster url");
        String url = buildTmdbPosterUrl(imageUrl, width, RESIZE_ALL);
        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public String getPosterUrl(final PhilmMovie movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getTmdbPosterUrl())) {
            url = buildTmdbPosterUrl(movie.getTmdbPosterUrl(),
                    width, RESIZE_ALL);
        } else if (!TextUtils.isEmpty(movie.getTraktPosterUrl())) {
            url = buildTraktUrl(movie.getTraktPosterUrl(),
                    selectSize(width, TRAKT_POSTER_SIZES, RESIZE_ALL));
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public String getFanartUrl(final PhilmMovie movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getTmdbBackdropUrl())) {
            url = buildTmdbBackdropUrl(movie.getTmdbBackdropUrl(), width, RESIZE_ALL);
        } else if (!TextUtils.isEmpty(movie.getTraktBackdropUrl())) {
            url = buildTraktUrl(movie.getTraktBackdropUrl(),
                    selectSize(width, TRAKT_BACKDROP_SIZES, RESIZE_ALL));
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public String getFanartUrl(final PhilmMovie.BackdropImage image,
            final int width, final int height) {

        final String imageUrl = image.url;
        Preconditions.checkNotNull(imageUrl, "image must have backdrop url");

        String url = null;
        switch (image.sourceType) {
            case PhilmMovie.TYPE_TMDB:
                url = buildTmdbBackdropUrl(imageUrl, width, RESIZE_ALL);
                break;
            case PhilmMovie.TYPE_TRAKT:
                url = buildTraktUrl(imageUrl, selectSize(width, TRAKT_BACKDROP_SIZES, RESIZE_ALL));
                break;
        }

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public String getProfileUrl(final PhilmPerson person, final int width, final int height) {
        final String imageUrl = person.getPictureUrl();
        Preconditions.checkNotNull(imageUrl, "movie must have picture url");

        String url = null;
        switch (person.getPictureType()) {
            case PhilmMovie.TYPE_TMDB:
                url = buildTmdbBackdropUrl(imageUrl, width, RESIZE_ALL);
                break;
            case PhilmMovie.TYPE_TRAKT:
                url = buildTraktUrl(imageUrl, selectSize(width, TRAKT_BACKDROP_SIZES, RESIZE_ALL));
                break;
        }

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getResizedUrl(String url, int width, int height) {
        StringBuffer sb = new StringBuffer("https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy");
        sb.append("?container=focus");
        sb.append("&resize_w=").append(width);
        sb.append("&resize_h=").append(height);
        sb.append("&url=").append(URLEncoder.encode(url));
        sb.append("&refresh=31536000");
        return sb.toString();
    }

    private String buildTmdbPosterUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbPosterSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbPosterSizes, forceLarger));
        } else {
            return null;
        }
    }

    private String buildTmdbBackdropUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbBackdropSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbBackdropSizes, forceLarger));
        } else {
            return null;
        }
    }

    private String buildTmdbProfileUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbProfileSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbProfileSizes, forceLarger));
        } else {
            return null;
        }
    }

    private static int selectSize(final int width, final int[] widths, final boolean forceLarger) {
        int previousBucketWidth = 0;

        for (int i = 0; i < widths.length; i++) {
            final int currentBucketWidth = widths[i];

            if (width < currentBucketWidth) {
                if (forceLarger || previousBucketWidth != 0) {
                    // We're in between this and the previous bucket
                    final int bucketDiff = currentBucketWidth - previousBucketWidth;
                    if (width < previousBucketWidth + (bucketDiff / 2)) {
                        return previousBucketWidth;
                    } else {
                        return currentBucketWidth;
                    }
                } else {
                    return currentBucketWidth;
                }
            } else if (i == widths.length - 1) {
                // If we get here then we're larger than a bucket
                if (width < currentBucketWidth * 2) {
                    return currentBucketWidth;
                }
            }

            previousBucketWidth = currentBucketWidth;
        }
        return Integer.MAX_VALUE;
    }

    private static String buildTraktUrl(final String originalUrl, final int width) {
        if (!TextUtils.isEmpty(originalUrl) && width != Integer.MAX_VALUE) {
            final int lastDot = originalUrl.lastIndexOf('.');
            if (lastDot != 0) {
                StringBuilder url = new StringBuilder(originalUrl.substring(0, lastDot));
                url.append('-').append(width);
                url.append(originalUrl.substring(lastDot));
                return url.toString();
            }
        }
        return originalUrl;
    }

    private static String buildTmdbUrl(String baseUrl, String imagePath, int width) {
        StringBuilder url = new StringBuilder(baseUrl);
        if (width == Integer.MAX_VALUE) {
            url.append("original");
        } else {
            url.append('w').append(width);
        }
        url.append(imagePath);
        return url.toString();
    }

    public String getVideoSnapshotUrl(PhilmMovieVideo video, final int width, final int height) {
        switch (video.getSource()) {
            case YOUTUBE:
                StringBuilder url = new StringBuilder(YOUTUBE_URL_BASE);
                url.append(video.getId()).append("/");

                final int size = selectSize(width,
                        new int[] { YOUTUBE_MEDIUM_Q_WIDTH, YOUTUBE_HIGH_Q_WIDTH }, false);
                switch (size) {
                    case YOUTUBE_MEDIUM_Q_WIDTH:
                        url.append(YOUTUBE_MEDIUM_Q_FILENAME);
                        break;
                    case YOUTUBE_HIGH_Q_WIDTH:
                    case Integer.MAX_VALUE:
                        url.append(YOUTUBE_HIGH_Q_FILENAME);
                        break;
                }

                return url.toString();
        }
        return null;
    }

}
