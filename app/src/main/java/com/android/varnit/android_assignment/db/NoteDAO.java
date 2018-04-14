package com.android.varnit.android_assignment.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.android.varnit.android_assignment.model.Note;

import java.util.List;

@Dao
public interface NoteDAO {

    @Query("SELECT * FROM note")
    List<Note> getAllNotes();

    @Query("SELECT * FROM note where id LIKE  :id")
    Note findById(int id);

    @RawQuery(observedEntities = Note.class)
    List<Note> getNotes(String query);

    @Query("SELECT COUNT(*) from note")
    int countNotes();

    @Insert
    void insertAll(Note... notes);

    @Delete
    void delete(Note note);

    @Update
    void updateNote(Note note);
}