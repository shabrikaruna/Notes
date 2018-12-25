package com.example.android.notes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.notes.database.AppDatabase;
import com.example.android.notes.database.NoteEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<NoteEntry>> notes;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());

        notes = database.noteDao().getAllNotes();
    }
    public LiveData<List<NoteEntry>> getTasks() {
        return notes;
    }
}
