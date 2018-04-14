package com.android.varnit.android_assignment.activity;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.varnit.android_assignment.db.AppDatabase;
import com.android.varnit.android_assignment.model.Note;
import com.android.varnit.android_assignment.R;

public class EditNoteActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    private boolean isSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Your toolbar is now an action bar and you can use it like you always do, for example:
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Note note = (Note) getIntent().getSerializableExtra("note");

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ctl.setTitle(note.getTitle());

        EditText noteDescription = (EditText) findViewById(R.id.note_description);
        noteDescription.setText(note.getDescription());
        noteDescription.setSelection(noteDescription.getText().length());

        KeyListener keyListener = noteDescription.getKeyListener();
        noteDescription.setBackground(null);
        noteDescription.setKeyListener(null);

        TextView editNote = (TextView) findViewById(R.id.edit_note);
        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editNote.getText().equals("Edit")) {
                    noteDescription.setKeyListener(keyListener);
                    editNote.setText("Save");
                    editNote.requestFocus();
                    editNote.setFocusable(true);
                    showKeyboard(v);
                    isSave = true;

                } else {
                    noteDescription.setKeyListener(null);
                    noteDescription.setFocusable(false);
                    editNote.setText("Edit");
                    hideKeyboard(v);
                    isSave = true;

                    note.setDescription(noteDescription.getText().toString());
                    EditNoteTask task = new EditNoteTask(appDatabase, note);
                    task.execute();
//                    noteDescription.setFocusableInTouchMode(false);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class EditNoteTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private Note note;

        EditNoteTask(AppDatabase db, Note note) {
            mDb = db;
            this.note = note;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "user-db").build();
            Note noteTemp = appDatabase.noteDAO().findById(note.getId());
            if(noteTemp != null) {
                noteTemp.setDescription(note.getDescription());
                appDatabase.noteDAO().updateNote(noteTemp);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Snackbar.make(findViewById(R.id.edit_note_layout), "Note Saved...", Snackbar.LENGTH_SHORT).show();
        }
    }
}
