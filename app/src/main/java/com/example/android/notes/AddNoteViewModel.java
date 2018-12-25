package com.example.android.notes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.notes.database.AppDatabase;
import com.example.android.notes.database.NoteEntry;

public class AddNoteViewModel extends ViewModel {
    private LiveData<NoteEntry> note;

    public AddNoteViewModel(AppDatabase database, int taskId) {
        note = database.noteDao().loadNotesById(taskId);
    }

    public LiveData<NoteEntry> getTask() {
        return note;
    }
}
