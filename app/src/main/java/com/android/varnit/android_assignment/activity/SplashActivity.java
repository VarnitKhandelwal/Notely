package com.android.varnit.android_assignment.activity;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.varnit.android_assignment.db.AppDatabase;
import com.android.varnit.android_assignment.model.Note;
import com.android.varnit.android_assignment.R;
import com.android.varnit.android_assignment.utils.SharedPreference;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    AppDatabase appDatabase;
    private SharedPreference sharedPreferenceObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferenceObj = new SharedPreference(SplashActivity.this);

        if (sharedPreferenceObj.getApp_runFirst().equals("FIRST")) {
            // That's mean First Time Launch
            // After your Work , SET Status NO
            sharedPreferenceObj.setApp_runFirst("NO");
            AddDummyNoteInDbTask task = new AddDummyNoteInDbTask(appDatabase);
            task.execute();
        } else {
            // App is not First Time Launch
            openHomeActivity();
        }
    }

    private void openHomeActivity() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    private class AddDummyNoteInDbTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        AddDummyNoteInDbTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "user-db").build();
            for (int i = 0; i < 10; i++) {
                Note note = new Note();
                note.setTitle("And then there were none");
                note.setDescription("Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. " +
                        "Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine." +
                        "Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine." +
                        " Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine." +
                        "Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine." +
                        "Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine. Ten little Soldier Boys went out to dine.");
                note.setDate("Today at 06:30 PM");
                if(i % 2 == 0)
                    note.setIsPoemCategory(true);
                else
                    note.setIsStoryCategory(true);
                appDatabase.noteDAO().insertAll(note);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            openHomeActivity();
        }
    }
}
