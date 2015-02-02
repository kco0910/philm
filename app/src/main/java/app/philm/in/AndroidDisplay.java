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

package app.philm.in;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;

import app.philm.in.fragments.AboutFragment;
import app.philm.in.fragments.CancelCheckinMovieFragment;
import app.philm.in.fragments.CheckinMovieFragment;
import app.philm.in.fragments.CredentialsChangedFragment;
import app.philm.in.fragments.DiscoverTabFragment;
import app.philm.in.fragments.LibraryMoviesFragment;
import app.philm.in.fragments.LicencesFragment;
import app.philm.in.fragments.LoginFragment;
import app.philm.in.fragments.MovieCastListFragment;
import app.philm.in.fragments.MovieCrewListFragment;
import app.philm.in.fragments.MovieDetailFragment;
import app.philm.in.fragments.MovieImagesFragment;
import app.philm.in.fragments.MovieSearchListFragment;
import app.philm.in.fragments.PersonCastListFragment;
import app.philm.in.fragments.PersonCrewListFragment;
import app.philm.in.fragments.PersonDetailFragment;
import app.philm.in.fragments.PersonSearchListFragment;
import app.philm.in.fragments.RateMovieFragment;
import app.philm.in.fragments.RelatedMoviesFragment;
import app.philm.in.fragments.SearchFragment;
import app.philm.in.fragments.TrendingMoviesFragment;
import app.philm.in.fragments.WatchlistMoviesFragment;
import app.philm.in.model.ColorScheme;
import app.philm.in.util.ColorUtils;

public class AndroidDisplay implements Display {

    private static final TypedValue sTypedValue = new TypedValue();

    private final ActionBarActivity mActivity;
    private final DrawerLayout mDrawerLayout;

    private ColorScheme mColorScheme;
    private int mColorPrimaryDark;

    private Toolbar mToolbar;
    private boolean mCanChangeToolbarBackground;

    public AndroidDisplay(ActionBarActivity activity,
            DrawerLayout drawerLayout) {
        mActivity = Preconditions.checkNotNull(activity, "activity cannot be null");
        mDrawerLayout = drawerLayout;

        mActivity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, sTypedValue, true);
        mColorPrimaryDark = sTypedValue.data;

        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(mColorPrimaryDark);
        }
    }

    @Override
    public void showLibrary() {
        showFragmentFromDrawer(new LibraryMoviesFragment());
    }

    @Override
    public void showTrending() {
        showFragmentFromDrawer(new TrendingMoviesFragment());
    }

    @Override
    public void showDiscover() {
        showFragmentFromDrawer(new DiscoverTabFragment());
    }

    @Override
    public void showWatchlist() {
        showFragmentFromDrawer(new WatchlistMoviesFragment());
    }

    @Override
    public void showLogin() {
        LoginFragment fragment = LoginFragment.create();

        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }

    @Override
    public void startMovieDetailActivity(String movieId, Bundle bundle) {
        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        startActivity(intent, bundle);
    }

    @Override
    public void showMovieDetailFragment(String movieId) {
        showFragmentFromDrawer(MovieDetailFragment.create(movieId));
    }

    @Override
    public void startMovieImagesActivity(String movieId) {
        Intent intent = new Intent(mActivity, MovieImagesActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        mActivity.startActivity(intent);
    }

    @Override
    public void showMovieImagesFragment(String movieId) {
        showFragmentFromDrawer(MovieImagesFragment.create(movieId));
    }

    @Override
    public void showSearchFragment() {
        showFragmentFromDrawer(new SearchFragment());
    }

    @Override
    public void showSearchMoviesFragment() {
        showFragment(new MovieSearchListFragment());
    }

    @Override
    public void showSearchPeopleFragment() {
        showFragment(new PersonSearchListFragment());
    }

    @Override
    public void showAboutFragment() {
        AboutFragment fragment = new AboutFragment();
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }

    @Override
    public void showLicencesFragment() {
        showFragment(new LicencesFragment());
    }

    @Override
    public void showRateMovieFragment(String movieId) {
        RateMovieFragment fragment = RateMovieFragment.create(movieId);
        fragment.show(mActivity.getSupportFragmentManager(), FRAGMENT_TAG_RATE_MOVIE);
    }

    @Override
    public void closeDrawerLayout() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public boolean toggleDrawer() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasMainFragment() {
        return mActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_main) != null;
    }

    @Override
    public void startAddAccountActivity() {
        Intent intent = new Intent(mActivity, AccountActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void startAboutActivity() {
        Intent intent = new Intent(mActivity, AboutActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void showUpNavigation(boolean show) {
        final ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setHomeAsUpIndicator(show ? R.drawable.ic_back : R.drawable.ic_menu);
        }
    }

    @Override
    public void setActionBarTitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    @Override
    public void setActionBarSubtitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(title);
        }
    }

    @Override
    public boolean popEntireFragmentBackStack() {
        final FragmentManager fm = mActivity.getSupportFragmentManager();
        final int backStackCount = fm.getBackStackEntryCount();
        // Clear Back Stack
        for (int i = 0; i < backStackCount; i++) {
            fm.popBackStack();
        }
        return backStackCount > 0;
    }

    @Override
    public void finishActivity() {
        mActivity.finish();
    }

    @Override
    public void showSettings() {
        mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
    }

    @Override
    public void showRelatedMovies(String movieId) {
        showFragment(RelatedMoviesFragment.create(movieId));
    }

    @Override
    public void showCastList(String movieId) {
        showFragment(MovieCastListFragment.create(movieId));
    }

    @Override
    public void showCrewList(String movieId) {
        showFragment(MovieCrewListFragment.create(movieId));
    }

    @Override
    public void showCheckin(String movieId) {
        CheckinMovieFragment fragment = CheckinMovieFragment.create(movieId);
        fragment.show(mActivity.getSupportFragmentManager(), FRAGMENT_TAG_CHECKIN_MOVIE);
    }

    @Override
    public void showCancelCheckin() {
        CancelCheckinMovieFragment fragment = CancelCheckinMovieFragment.create();
        fragment.show(mActivity.getSupportFragmentManager(), FRAGMENT_TAG_CHECKIN_MOVIE);
    }

    @Override
    public void showCredentialsChanged() {
        new CredentialsChangedFragment().show(mActivity.getSupportFragmentManager(),
                FRAGMENT_TAG_TRAKT_CREDENTIALS_WRONG);
    }

    @Override
    public void startPersonDetailActivity(String id, Bundle bundle) {
        Intent intent = new Intent(mActivity, PersonActivity.class);
        intent.putExtra(PARAM_ID, id);
        startActivity(intent, bundle);
    }

    @Override
    public void showPersonDetail(String id) {
        showFragmentFromDrawer(PersonDetailFragment.create(id));
    }

    @Override
    public void showPersonCastCredits(String id) {
        showFragment(PersonCastListFragment.create(id));
    }

    @Override
    public void showPersonCrewCredits(String id) {
        showFragment(PersonCrewListFragment.create(id));
    }

    @Override
    public void playYoutubeVideo(String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + id));

        mActivity.startActivity(intent);
    }

    @Override
    public void setColorScheme(ColorScheme colorScheme) {
        if (Objects.equal(mColorScheme, colorScheme)) {
            // ColorScheme hasn't changed, ignore
            return;
        }

        mColorScheme = colorScheme;

        setToolbarBackground(mColorScheme.primaryAccent);
        mColorPrimaryDark = mColorScheme.secondaryAccent;
    }

    private void showFragmentFromDrawer(Fragment fragment) {
        popEntireFragmentBackStack();

        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }

    private void showFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void startActivity(Intent intent, Bundle options) {
        ActivityCompat.startActivity(mActivity, intent, options);
    }

    @Override
    public void setStatusBarColor(float scroll) {
        final int statusBarColor = ColorUtils.blendColors(mColorPrimaryDark, 0, scroll);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(statusBarColor);
        } else if (Build.VERSION.SDK_INT >= 21) {
            mActivity.getWindow().setStatusBarColor(statusBarColor);
        }
    }

    @Override
    public void setSupportActionBar(Object toolbar, boolean handleBackground) {
        mToolbar = (Toolbar) toolbar;
        mCanChangeToolbarBackground = handleBackground;

        if (mColorScheme != null) {
            setToolbarBackground(mColorScheme.primaryAccent);
        }

        if (mDrawerLayout != null && mToolbar != null) {
            final ActionBar ab = mActivity.getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeButtonEnabled(true);
            }
        }
    }

    private void setToolbarBackground(int color) {
        if (mCanChangeToolbarBackground && mToolbar != null) {
            mToolbar.setBackgroundColor(color);
        }
    }
}
