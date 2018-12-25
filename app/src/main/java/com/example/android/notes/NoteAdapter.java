package com.example.android.notes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.notes.database.NoteEntry;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy";
    private List<NoteEntry> mNoteEntries;
    private Context mContext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    final private ItemClickListener mItemClickListener;

    public NoteAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_layout, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {
        NoteEntry noteEntry = mNoteEntries.get(i);
        String description = noteEntry.getDescription();

        String firstWord;
        if(description.contains(" ")){
            firstWord = description.substring(0, description.indexOf(" "));
        }else{
            firstWord = description.substring(0,10);
        }

        String updatedAt = dateFormat.format(noteEntry.getUpdatedAt());
        noteViewHolder.mNoteTitle.setText(firstWord);
        noteViewHolder.mNOteDescription.setText(description);
        noteViewHolder.mNoteDate.setText(updatedAt);
    }

    @Override
    public int getItemCount() {
        return mNoteEntries == null ? 0 : mNoteEntries.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public List<NoteEntry> getTasks() {
        return mNoteEntries;
    }

    public void setTasks(List<NoteEntry> noteEntries) {
        mNoteEntries = noteEntries;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        TextView mNoteTitle;
        TextView mNOteDescription;
        TextView mNoteDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mNoteTitle = itemView.findViewById(R.id.noteTitle);
            mNOteDescription = itemView.findViewById(R.id.noteDescription);
            mNoteDate = itemView.findViewById(R.id.noteDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mNoteEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
