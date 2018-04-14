package com.android.varnit.android_assignment.activity;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.varnit.android_assignment.db.AppDatabase;
import com.android.varnit.android_assignment.utils.EndDrawerToggle;
import com.android.varnit.android_assignment.model.Note;
import com.android.varnit.android_assignment.adapter.NoteListAdapter;
import com.android.varnit.android_assignment.R;
import com.android.varnit.android_assignment.utils.SwipeItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener {

    ArrayList<Note> notesList = new ArrayList<Note>();
    NoteListAdapter noteListAdapter;
    AppDatabase appDatabase;
    private RecyclerView userShareList;
    private boolean heartedSelect, favoriteSelect, poemSelect, storySelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeUI();
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        EndDrawerToggle toggle = new EndDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.hearted_layout).setOnClickListener(this);
        findViewById(R.id.favorite_layout).setOnClickListener(this);
        findViewById(R.id.poem_layout).setOnClickListener(this);
        findViewById(R.id.story_layout).setOnClickListener(this);
        findViewById(R.id.filter_image).setOnClickListener(this);
        findViewById(R.id.apply_layout).setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        userShareList = (RecyclerView) findViewById(R.id.note_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        userShareList.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        userShareList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        setSwipeForRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String query = "SELECT * FROM note";
        GetNotesFromDbTask task = new GetNotesFromDbTask(appDatabase, query);
        task.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void setSwipeForRecyclerView() {

        SwipeItem swipeHelper = new SwipeItem(0, ItemTouchHelper.LEFT, HomeActivity.this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                NoteListAdapter adapter = (NoteListAdapter) userShareList.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                NoteListAdapter adapter = (NoteListAdapter) userShareList.getAdapter();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(userShareList);

        //set swipe label
        swipeHelper.setLeftSwipeLable("Delete");
        //set swipe background-Color
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(HomeActivity.this, R.color.swipebg));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.hearted_layout:
                if (heartedSelect) {
                    ((TextView) findViewById(R.id.hearted_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.hearted_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((TextView) findViewById(R.id.hearted_text)).setTextColor(getResources().getColor(R.color.selectColor));
                    ((ImageView) findViewById(R.id.hearted_image)).setColorFilter(ContextCompat.getColor(this, R.color.selectColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                heartedSelect = !heartedSelect;
                break;
            case R.id.favorite_layout:
                if (favoriteSelect) {
                    ((TextView) findViewById(R.id.favorite_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.favorite_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((TextView) findViewById(R.id.favorite_text)).setTextColor(getResources().getColor(R.color.selectColor));
                    ((ImageView) findViewById(R.id.favorite_image)).setColorFilter(ContextCompat.getColor(this, R.color.selectColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                favoriteSelect = !favoriteSelect;
                break;
            case R.id.poem_layout:
                if (poemSelect) {
                    ((TextView) findViewById(R.id.poem_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.poem_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((TextView) findViewById(R.id.poem_text)).setTextColor(getResources().getColor(R.color.selectColor));
                    ((ImageView) findViewById(R.id.poem_image)).setColorFilter(ContextCompat.getColor(this, R.color.selectColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                poemSelect = !poemSelect;
                break;
            case R.id.story_layout:
                if (storySelect) {
                    ((TextView) findViewById(R.id.story_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.story_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    ((TextView) findViewById(R.id.story_text)).setTextColor(getResources().getColor(R.color.selectColor));
                    ((ImageView) findViewById(R.id.story_image)).setColorFilter(ContextCompat.getColor(this, R.color.selectColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                storySelect = !storySelect;
                break;
            case R.id.filter_image:
                if (heartedSelect || favoriteSelect || poemSelect || storySelect) {
                    heartedSelect = false;
                    favoriteSelect = false;
                    poemSelect = false;
                    storySelect = false;
                    ((TextView) findViewById(R.id.hearted_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.hearted_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    ((TextView) findViewById(R.id.favorite_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.favorite_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    ((TextView) findViewById(R.id.poem_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.poem_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    ((TextView) findViewById(R.id.story_text)).setTextColor(getResources().getColor(R.color.white));
                    ((ImageView) findViewById(R.id.story_image)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case R.id.apply_layout:
                String query;
                if (heartedSelect || favoriteSelect || poemSelect || storySelect) {
                    query = "SELECT * FROM note WHERE ";
                    if (heartedSelect) {
                        query += "isFavorite = 1";
                    }
                    if (favoriteSelect) {
                        if (query.contains("isFavorite"))
                            query += " OR ";
                        query += "isStarred = 1";
                    }
                    if (poemSelect) {
                        if (query.contains("isFavorite") || query.contains("isStarred"))
                            query += " OR ";
                        query += "isPoemCategory = 1";
                    }
                    if (storySelect) {
                        if (query.contains("isFavorite") || query.contains("isStarred") || query.contains("isPoemCategory"))
                            query += " OR ";
                        query += "isStoryCategory = 1";
                    }
                } else {
                    query = "SELECT * FROM note";
                }
                GetNotesFromDbTask task = new GetNotesFromDbTask(appDatabase, query);
                task.execute();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                break;
        }
    }

    private class GetNotesFromDbTask extends AsyncTask<Void, ArrayList<Note>, ArrayList<Note>> {

        private final AppDatabase mDb;
        private String query;

        GetNotesFromDbTask(AppDatabase db, String query) {
            mDb = db;
            this.query = query;
        }

        @Override
        protected ArrayList<Note> doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "user-db").build();

            notesList = (ArrayList) appDatabase.noteDAO().getNotes(query);
            return notesList;
        }

        @Override
        protected void onPostExecute(ArrayList<Note> usersList) {
            super.onPostExecute(usersList);
            noteListAdapter = new NoteListAdapter(HomeActivity.this, usersList);
            userShareList.setAdapter(noteListAdapter);
        }
    }
}
