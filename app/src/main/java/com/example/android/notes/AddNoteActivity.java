package com.example.android.notes;

import android.app.ActionBar;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.notes.database.AppDatabase;
import com.example.android.notes.database.NoteEntry;

import java.util.Date;

import static android.graphics.Color.BLACK;
import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class AddNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extraTaskId";
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    private static final int DEFAULT_TASK_ID = -1;
    private static final String TAG = AddNoteActivity.class.getSimpleName();
    private EditText mDescription;
    private ImageButton mSave;

    TextWatcher watch = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable arg0) {
            if (mDescription.getText().length() > 0) {
                mSave.setEnabled(true);
                mSave.setColorFilter(Color.argb(255, 255, 255, 255));
                mSave.setBackgroundColor(Color.parseColor("#20cc85"));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            mSave.setEnabled(false);
            mSave.setColorFilter(Color.argb(0, 0, 0, 0));
            mSave.setBackgroundColor(Color.parseColor("#757575"));

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            if (mDescription.getText().length() > 0) {
                mSave.setEnabled(true);
                mSave.setColorFilter(Color.argb(255, 255, 255, 255));
                mSave.setBackgroundColor(Color.parseColor("#20cc85"));
            } else {
                mSave.setEnabled(false);
                mSave.setColorFilter(Color.argb(0, 0, 0, 0));
                mSave.setBackgroundColor(Color.parseColor("#757575"));

            }
        }
    };
    private int mTaskId = DEFAULT_TASK_ID;
    private AppDatabase mDb;
    private LinearLayout mAddNoteLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Add Note</font>"));
        getSupportActionBar().setElevation(0);
        initViews();
        mDb = AppDatabase.getInstance(getApplicationContext());
        mAddNoteLinearLayout = (LinearLayout) findViewById(R.id.add_activity_linear);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            getSupportActionBar().setTitle(R.string.edit_notes);
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Edit Note</font>"));
            getSupportActionBar().setElevation(0);

//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            hideSoftKeyboard(mAddNoteLinearLayout);
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                AddNoteViewModelFactory factory = new AddNoteViewModelFactory(mDb, mTaskId);
                final AddNoteViewModel viewModel = ViewModelProviders.of(this, factory).get(AddNoteViewModel.class);
                viewModel.getTask().observe(this, new Observer<NoteEntry>() {
                    @Override
                    public void onChanged(@Nullable NoteEntry noteEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(noteEntry);
                    }
                });
            }
        } else {
            showSoftKeyboard(mAddNoteLinearLayout);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void populateUI(NoteEntry note) {
        if (note == null) {
            return;
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        if (description.length() > 0) {
            String title = description;
            if (title.contains(".")) {
                title = title.substring(0, title.indexOf("."));
            } else {
                int length = description.length();
                title = title.substring(0, length);
            }
            Date date = new Date();
            final NoteEntry note = new NoteEntry(title, description, date);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mTaskId == DEFAULT_TASK_ID) {
                        mDb.noteDao().insertNote(note);
                    } else {
                        note.setId(mTaskId);
                        mDb.noteDao().updateNote(note);
                    }
                    finish();
                }
            });
        } else {
            Toast.makeText(AddNoteActivity.this, "Enter the note to save !", Toast.LENGTH_LONG).show();
        }
    }
}
