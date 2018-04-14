package com.android.varnit.android_assignment.activity;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.varnit.android_assignment.db.AppDatabase;
import com.android.varnit.android_assignment.model.Note;
import com.android.varnit.android_assignment.R;

import java.text.DateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    private TextInputLayout noteNameLayout, noteDescriptionLayout;
    private EditText noteName, noteDescription;
    private RadioGroup categoryRadioGroup;
    private RadioButton categoryRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initialize();
    }

    private void initialize() {
        noteNameLayout = (TextInputLayout) findViewById(R.id.note_name_layout);
        noteDescriptionLayout = (TextInputLayout) findViewById(R.id.note_description_layout);
        noteName = (EditText) findViewById(R.id.note_name);
        noteDescription = (EditText) findViewById(R.id.note_description);
        categoryRadioGroup = (RadioGroup) findViewById(R.id.category_radio_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);

        //Your toolbar is now an action bar and you can use it like you always do, for example:
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateNoteName() {
        if (noteName.getText().toString().trim().isEmpty()) {
            noteNameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(noteName);
            return false;
        } else {
            noteNameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNoteDescription() {
        if (noteDescription.getText().toString().trim().isEmpty()) {
            noteDescriptionLayout.setError(getString(R.string.err_msg_name));
            requestFocus(noteDescription);
            return false;
        } else {
            noteDescriptionLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void addNote() {
        if (!validateNoteName()) {
            return;
        }

        if (!validateNoteDescription()) {
            return;
        }

        Note note = new Note();
        note.setTitle(noteName.getText().toString());
        note.setDescription(noteDescription.getText().toString());
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String currentDateTimeString = dateFormat.format(new Date());
        note.setDate(currentDateTimeString);

        // get selected radio button from radioGroup
        int selectedId = categoryRadioGroup.getCheckedRadioButtonId();

        switch (selectedId) {
            case R.id.poem_category:
                note.setIsPoemCategory(true);
                break;
            case R.id.story_category:
                note.setIsStoryCategory(true);
                break;
        }

        AddNoteTask task = new AddNoteTask(appDatabase, note);
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_add_note:
                addNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AddNoteTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private Note note;

        AddNoteTask(AppDatabase db, Note note) {
            mDb = db;
            this.note = note;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "user-db").build();
            appDatabase.noteDAO().insertAll(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
