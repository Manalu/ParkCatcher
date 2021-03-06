/*
    Park Catcher Montréal
    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    Copyright (C) 2012 Mudar Noufal <mn@mudar.ca>

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.parkcatcher.ui.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.base.ToolbarActivity;
import ca.mudar.parkcatcher.ui.fragments.DetailsFragment;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;


public class DetailsActivity extends ToolbarActivity {
    private static final String TAG = "DetailsActivity";

    public static Intent newIntent(Context context, int idPost) {
        final Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(Const.BundleExtras.ID_POST, idPost);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_details);
        setContentView(R.layout.activity_toolbar_header);
        getActionBarToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            final DetailsFragment fragment = DetailsFragment.newInstance(getIdPostFromIntent());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                startActivity(MainActivity.newIntent(this));
                finish();
//                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getIdPostFromIntent() {
        try {
            if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
                return getIdFromUri(getIntent().getData());
            } else {
                final Bundle extras = getIntent().getExtras();
                return extras.getInt(Const.BundleExtras.ID_POST, Const.UNKNOWN);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Const.UNKNOWN;
        }
    }

    private int getIdFromUri(Uri uri) {
        final List<String> pathSegments = uri.getPathSegments();

        if ((pathSegments.size() == 5)
                && (pathSegments.get(0).equals(Const.BundleExtras.URL_PATH_POST_ID))) {

            try {
                final int postId = Integer.parseInt(pathSegments.get(1));
                final int dayOfWeekIso = Integer.valueOf(pathSegments.get(2));
                final double clockTime = Double.valueOf(pathSegments.get(3));
                final int duration = Integer.valueOf(pathSegments.get(4));

                final int hourOfDay = ParkingTimeHelper.getHoursFromClockTime(clockTime);
                final int minute = ParkingTimeHelper.getMintuesFromClockTime(clockTime);

                GregorianCalendar calendar = new GregorianCalendar();

                calendar.set(Calendar.DAY_OF_WEEK, ParkingTimeHelper.getDayOfWeek(dayOfWeekIso));
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                final ParkingApp parkingApp = (ParkingApp) getApplicationContext();
                parkingApp.setParkingCalendar(calendar);
                parkingApp.setParkingDuration(duration);

                return postId;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return Const.UNKNOWN;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void toggleToobarColor(boolean isForbidden) {
        if (Const.SUPPORTS_LOLLIPOP) {
            final int colorDark = isForbidden ? getResources().getColor(R.color.theme_alert_primary_dark) : getResources().getColor(R.color.theme_primary_dark);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(colorDark);
        }
        final int color = isForbidden ? getResources().getColor(R.color.theme_alert_primary) : getResources().getColor(R.color.theme_primary);
        getActionBarToolbar().setBackgroundColor(color);

        final ContextThemeWrapper w = new ContextThemeWrapper(this, isForbidden ? R.style.AppTheme_Parking_Alert : R.style.AppTheme_Parking);
        getTheme().setTo(w.getTheme());
    }
}
