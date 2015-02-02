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

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.google.common.base.Preconditions;

import java.util.concurrent.ExecutorService;

import app.philm.in.Constants;
import app.philm.in.network.BackgroundCallRunnable;
import app.philm.in.network.NetworkCallRunnable;
import retrofit.RetrofitError;

public class PhilmBackgroundExecutor implements BackgroundExecutor {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private final ExecutorService mExecutorService;

    public PhilmBackgroundExecutor(ExecutorService executorService) {
        mExecutorService = Preconditions.checkNotNull(executorService,
                "executorService cannot be null");
    }

    @Override
    public <R> void execute(NetworkCallRunnable<R> runnable) {
        mExecutorService.execute(new TraktNetworkRunner<>(runnable));
    }

    @Override
    public <R> void execute(BackgroundCallRunnable<R> runnable) {
        mExecutorService.execute(new BackgroundCallRunner<>(runnable));
    }

    private class BackgroundCallRunner<R> implements Runnable {
        private final BackgroundCallRunnable<R> mBackgroundRunnable;

        BackgroundCallRunner(BackgroundCallRunnable<R> runnable) {
            mBackgroundRunnable = runnable;
        }

        @Override
        public final void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBackgroundRunnable.preExecute();
                }
            });

            R result = mBackgroundRunnable.runAsync();

            sHandler.post(new ResultCallback(result));
        }

        private class ResultCallback implements Runnable {
            private final R mResult;

            private ResultCallback(R result) {
                mResult = result;
            }

            @Override
            public void run() {
                mBackgroundRunnable.postExecute(mResult);
            }
        }
    }

    class TraktNetworkRunner<R> implements Runnable {

        private final NetworkCallRunnable<R> mBackgroundRunnable;

        TraktNetworkRunner(NetworkCallRunnable<R> runnable) {
            mBackgroundRunnable = runnable;
        }

        @Override
        public final void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBackgroundRunnable.onPreTraktCall();
                }
            });

            R result = null;
            RetrofitError retrofitError = null;

            try {
                result = mBackgroundRunnable.doBackgroundCall();
            } catch (RetrofitError re) {
                retrofitError = re;
                if (Constants.DEBUG) {
                    Log.d(((Object) this).getClass().getSimpleName(), "Error while completing network call", re);
                }
            }

            sHandler.post(new ResultCallback(result, retrofitError));
        }

        private class ResultCallback implements Runnable {
            private final R mResult;
            private final RetrofitError mRetrofitError;

            private ResultCallback(R result, RetrofitError retrofitError) {
                mResult = result;
                mRetrofitError = retrofitError;
            }

            @Override
            public void run() {
                if (mResult != null) {
                    mBackgroundRunnable.onSuccess(mResult);
                } else if (mRetrofitError != null) {
                    mBackgroundRunnable.onError(mRetrofitError);
                }
                mBackgroundRunnable.onFinished();
            }
        }
    }


}
