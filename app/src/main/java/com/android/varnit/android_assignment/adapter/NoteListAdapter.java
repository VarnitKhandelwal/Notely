package com.android.varnit.android_assignment.adapter;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.varnit.android_assignment.R;
import com.android.varnit.android_assignment.activity.EditNoteActivity;
import com.android.varnit.android_assignment.db.AppDatabase;
import com.android.varnit.android_assignment.model.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    ArrayList<Note> notesList;
    Context context;
    AppDatabase appDatabase;
    HashMap<Note, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private List<Note> itemsPendingRemoval;
    private Handler handler = new Handler(); // hanlder for running delayed runnables

    public NoteListAdapter(Context context, ArrayList<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
        itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item Layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Note note = notesList.get(position);

        if (itemsPendingRemoval.contains(note)) {
            /** {show swipe layout} and {hide regular layout} */
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undoOpt(note);
                }
            });
        } else {
            /** {show regular layout} and {hide swipe layout} */
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);

            holder.noteTitle.setText(notesList.get(position).getTitle());
            holder.noteDescription.setText(notesList.get(position).getDescription());
            holder.noteTime.setText(notesList.get(position).getDate());

            if (notesList.get(position).getIsStarred()) {
                holder.star.setImageResource(R.drawable.ic_star_select_24dp);
            } else {
                holder.star.setImageResource(R.drawable.ic_star_24dp);
            }

            if (notesList.get(position).getIsFavorite()) {
                holder.favorite.setImageResource(R.drawable.ic_favorite_select_24dp);
            } else {
                holder.favorite.setImageResource(R.drawable.ic_favorite_24dp);
            }

            holder.regularLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditNoteActivity.class);
                    intent.putExtra("note", note);
                    context.startActivity(intent);
                }
            });

            holder.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notesList.get(position).getIsStarred()) {
                        UpdateStarInNoteTask updateStarInNoteTask = new UpdateStarInNoteTask(appDatabase, notesList.get(position), false);
                        updateStarInNoteTask.execute();
                    } else {
                        UpdateStarInNoteTask updateStarInNoteTask = new UpdateStarInNoteTask(appDatabase, notesList.get(position), true);
                        updateStarInNoteTask.execute();
                    }
                }
            });

            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notesList.get(position).getIsFavorite()) {
                        UpdateFavoriteInNoteTask updateFavoriteInNoteTask = new UpdateFavoriteInNoteTask(appDatabase, notesList.get(position), false);
                        updateFavoriteInNoteTask.execute();
                    } else {
                        UpdateFavoriteInNoteTask updateFavoriteInNoteTask = new UpdateFavoriteInNoteTask(appDatabase, notesList.get(position), true);
                        updateFavoriteInNoteTask.execute();
                    }
                }
            });
        }
    }

    private void undoOpt(Note note) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(note);
        pendingRunnables.remove(note);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.remove(note);
        // this will rebind the row in "normal" state
        notifyItemChanged(notesList.indexOf(note));
    }

    public void pendingRemoval(int position) {

        final Note note = notesList.get(position);
        if (!itemsPendingRemoval.contains(note)) {
            itemsPendingRemoval.add(note);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the note
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(notesList.indexOf(note));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(note, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Note note = notesList.get(position);
        if (itemsPendingRemoval.contains(note)) {
            itemsPendingRemoval.remove(note);
        }
        if (notesList.contains(note)) {
            notesList.remove(position);
            notifyItemRemoved(position);
        }

        DeleteNoteTask deleteNoteTask = new DeleteNoteTask(appDatabase, note);
        deleteNoteTask.execute();
    }

    public boolean isPendingRemoval(int position) {
        Note note = notesList.get(position);
        return itemsPendingRemoval.contains(note);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout regularLayout;
        public LinearLayout swipeLayout;
        public TextView undo;
        TextView noteTitle, noteDescription, noteTime;
        ImageView star, favorite;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            noteDescription = (TextView) itemView.findViewById(R.id.note_description);
            noteTime = (TextView) itemView.findViewById(R.id.note_time);
            star = (ImageView) itemView.findViewById(R.id.star);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            regularLayout = (LinearLayout) itemView.findViewById(R.id.note_layout);
            swipeLayout = (LinearLayout) itemView.findViewById(R.id.swipe_layout);
            undo = (TextView) itemView.findViewById(R.id.undo);
        }
    }

    private class UpdateStarInNoteTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private Note note;
        private boolean isStar;

        UpdateStarInNoteTask(AppDatabase db, Note note, boolean isStar) {
            mDb = db;
            this.note = note;
            this.isStar = isStar;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "user-db").build();
            note.setIsStarred(isStar);
            appDatabase.noteDAO().updateNote(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
    }

    private class UpdateFavoriteInNoteTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private Note note;
        private boolean isFavorite;

        UpdateFavoriteInNoteTask(AppDatabase db, Note note, boolean isFavorite) {
            mDb = db;
            this.note = note;
            this.isFavorite = isFavorite;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "user-db").build();
            note.setIsFavorite(isFavorite);
            appDatabase.noteDAO().updateNote(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
    }

    private class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private Note note;
        private boolean isStar;

        DeleteNoteTask(AppDatabase db, Note note) {
            mDb = db;
            this.note = note;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "user-db").build();
            appDatabase.noteDAO().delete(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
    }
}