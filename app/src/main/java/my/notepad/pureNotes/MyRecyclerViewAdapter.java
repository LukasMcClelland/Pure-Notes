package my.notepad.pureNotes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.notepad.note20.R;

import java.util.Comparator;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.NoteViewItemViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(String item);
    }

    private final SortedList<NoteViewItem> mSortedList = new SortedList<>(NoteViewItem.class, new SortedList.Callback<NoteViewItem>() {
        @Override
        public int compare(NoteViewItem note, NoteViewItem t21) {
            return mComparator.compare(note, t21);
        }

        @Override
        public void onChanged(int i, int i1) {
            notifyItemRangeChanged(i, i1);
        }

        @Override
        public boolean areContentsTheSame(NoteViewItem note, NoteViewItem t21) {
            if (note.getDescription() != null && t21.getDescription() != null){
                return note.getDescription().equals(t21.getDescription());
            }
            else{
                return true;
            }
        }

        @Override
        public boolean areItemsTheSame(NoteViewItem note, NoteViewItem t21) {
            return note.getName().equals(t21.getName());
        }

        @Override
        public void onInserted(int i, int i1) {
            notifyItemRangeInserted(i, i1);
        }

        @Override
        public void onRemoved(int i, int i1) {
            notifyItemRangeRemoved(i, i1);
        }

        @Override
        public void onMoved(int i, int i1) {
            notifyItemMoved(i, i1);
        }
    });
    private List<NoteViewItem> mDataSet;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private Comparator<NoteViewItem> mComparator;
    private MainActivity mainActivity;


    public MyRecyclerViewAdapter(MainActivity activity, OnItemClickListener clickListener,
                                 OnItemLongClickListener longClickListener, Comparator<NoteViewItem> comparator){
        this.mainActivity = activity;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.mComparator = comparator;
    }

    public void changeComparator(Comparator<NoteViewItem> comparator) {
        this.mComparator = comparator;
    }

    @NonNull
    @Override
    public NoteViewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteViewItemViewHolder holder, int position){
        Log.i("INFO", "onBind start");
        final NoteViewItem noteViewItem = mSortedList.get(position);
        Log.i("INFO", holder.nameView.getText().toString());
        SpannableString stringBuilder = mainActivity.buildString(noteViewItem.getName());

        holder.nameView.setText(stringBuilder);
        holder.dateView.setText(noteViewItem.getDateModifiedDMY());
        holder.descView.setText(noteViewItem.getDescription());
        holder.bind(noteViewItem, clickListener, longClickListener);
    }

    @Override
    public int getItemCount(){
        return mSortedList.size();
    }

    public void add(NoteViewItem noteViewItem){
        mSortedList.add(noteViewItem);
    }

    public void remove(NoteViewItem noteViewItem){
        mSortedList.remove(noteViewItem);
    }

    public void addAll(List<NoteViewItem> noteViewItems){
        mSortedList.addAll(noteViewItems);
    }

    public void remove(List<NoteViewItem> noteViewItems){
        mSortedList.beginBatchedUpdates();
        for (NoteViewItem noteViewItem : noteViewItems){
            mSortedList.remove(noteViewItem);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<NoteViewItem> noteViewItems){
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--){
            NoteViewItem noteViewItem = mSortedList.get(i);
            if (!noteViewItems.contains(noteViewItem)){
                mSortedList.remove(noteViewItem);
            }
            else{
                mSortedList.updateItemAt(i, noteViewItem);
            }
        }
        mSortedList.replaceAll(noteViewItems);
        mSortedList.endBatchedUpdates();
    }

    public int indexOf(NoteViewItem noteViewItem){
        return mSortedList.indexOf(noteViewItem);
    }

    public NoteViewItem get(int i){
        return mSortedList.get(i);
    }



    static class NoteViewItemViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        TextView dateView;
        TextView descView;

        public NoteViewItemViewHolder(View itemView){
            super(itemView);
            nameView = itemView.findViewById(R.id.note_name);
            dateView = itemView.findViewById(R.id.note_date);
            descView = itemView.findViewById(R.id.note_description);
        }

        public void bind(final NoteViewItem noteViewItem, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(noteViewItem.getName());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    longClickListener.onItemLongClick(noteViewItem.getName());
                    return true;
                }
            });
        }
    }
}

