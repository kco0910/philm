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

import com.google.common.base.Preconditions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import app.philm.in.util.AndroidPhilmAlarmManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Inject AndroidPhilmAlarmManager mPhilmAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        PhilmApplication.from(context).inject(this);
        Preconditions.checkNotNull(mPhilmAlarmManager, "mPhilmAlarmManager cannot be null");

        mPhilmAlarmManager.onAlarmTriggered(intent);
    }
}
