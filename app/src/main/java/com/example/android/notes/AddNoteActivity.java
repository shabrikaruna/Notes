package com.example.android.notes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.notes.database.AppDatabase;
import com.example.android.notes.database.NoteEntry;

import java.util.Date;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class AddNoteActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddNoteActivity.class.getSimpleName();

    EditText mDescription;
    Button mSave;
    private int mTaskId = DEFAULT_TASK_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if( intent != null && intent.hasExtra(EXTRA_TASK_ID) ){
            mSave.setText("UPDATE");
            if( mTaskId == DEFAULT_TASK_ID ){
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(mDb,mTaskId);
                final AddNoteViewModel viewModel = ViewModelProviders.of(this,factory).get(AddNoteViewModel.class);
                viewModel.getTask().observe(this, new Observer<NoteEntry>() {
                    @Override
                    public void onChanged(@Nullable NoteEntry noteEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(noteEntry);
                    }
                });
            }
        }
    }

    private void populateUI(NoteEntry note) {
        if (note == null) {
            return;
        }
        mDescription.setText(note.getDescription());
    }

    private void initViews() {
        mDescription = findViewById(R.id.ed_description);
        mSave = findViewById(R.id.btn_save);
        mDescription.addTextChangedListener(watch);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }


    public void onSaveButtonClicked() {
        String description = mDescription.getText().toString();
        if( description.length() > 0 ){
            String title = description;
            if(title.contains(" ")){
                title = title.substring(0, title.indexOf(" "));
            }
            Date date = new Date();
            final NoteEntry note = new NoteEntry(title,description,date);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mTaskId == DEFAULT_TASK_ID) {
                        // insert new task
                        mDb.noteDao().insertNote(note);
                    } else {
                        //update task
                        note.setId(mTaskId);
                        mDb.noteDao().updateNote(note);
                    }
                    finish();
                }
            });
        }else{
            Toast.makeText(AddNoteActivity.this,"Enter the note to save!",Toast.LENGTH_LONG).show();
        }
    }

    TextWatcher watch = new TextWatcher(){
        @Override
        public void afterTextChanged(Editable arg0) {
            if( mDescription.getText().length() > 0) {
                mSave.setEnabled(true);
                mSave.setTextColor(Color.parseColor("#FFFFFF"));
                mSave.setBackgroundColor(Color.parseColor("#795548"));
            }else{
                Toast.makeText(AddNoteActivity.this,"Text Cleared",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            mSave.setEnabled(false);
            mSave.setTextColor(Color.parseColor("#000000"));
            mSave.setBackgroundColor(Color.parseColor("#a98274"));

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            if( mDescription.getText().length() > 0) {
                mSave.setEnabled(true);
                mSave.setTextColor(Color.parseColor("#FFFFFF"));
                mSave.setBackgroundColor(Color.parseColor("#795548"));
            }else{
                mSave.setEnabled(false);
                mSave.setTextColor(Color.parseColor("#000000"));
                mSave.setBackgroundColor(Color.parseColor("#a98274"));

            }
        }};
}
