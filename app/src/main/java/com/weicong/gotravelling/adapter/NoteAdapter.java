package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private Context mContext;

    private List<Note> mNotes;

    public NoteAdapter(Context context, List<Note> notes) {
        mContext = context;
        mNotes = notes;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    private OnClickListener mListener;

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.item_note, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Note note = mNotes.get(position);
        holder.content.setText(note.getContent());
        holder.date.setText(note.getDate());
        holder.address.setText(note.getAddress());
        holder.location.setImageDrawable(new IconicsDrawable(mContext,
                GoogleMaterial.Icon.gmd_location_on).colorRes(R.color.red));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void delete(int position) {
        mNotes.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        TextView date;
        TextView address;
        ImageView location;

        View layout;

        public MyViewHolder(View v) {
            super(v);

            content = (TextView) v.findViewById(R.id.tv_note_content);
            date = (TextView) v.findViewById(R.id.tv_date);
            address = (TextView) v.findViewById(R.id.tv_address);
            location = (ImageView) v.findViewById(R.id.iv_location);
            layout = v.findViewById(R.id.rl_layout);
        }
    }
}
