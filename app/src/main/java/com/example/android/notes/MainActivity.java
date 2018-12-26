package com.example.android.notes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.notes.database.AppDatabase;
import com.example.android.notes.database.NoteEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements NoteAdapter.ItemClickListener {

    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0);
        mNoteRecyclerView = findViewById(R.id.recyclerViewNotes);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteAdapter = new NoteAdapter(this, this);
        mNoteRecyclerView.setAdapter(mNoteAdapter);
        FloatingActionButton fabButton = findViewById(R.id.fab);
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>NOTES</font>"));
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(addTaskIntent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<NoteEntry> tasks = mNoteAdapter.getTasks();
                        mDb.noteDao().deleteNote(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mNoteRecyclerView);
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<NoteEntry>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntry> noteEntries) {
                mNoteAdapter.setTasks(noteEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
        intent.putExtra(AddNoteActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}
