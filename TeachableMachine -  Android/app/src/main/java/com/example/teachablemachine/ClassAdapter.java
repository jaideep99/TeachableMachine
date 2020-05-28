package com.example.teachablemachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassHolder>{

    private List<String> itemList = new ArrayList<String>();
    private ClassAdapter.OnItemClickListener mListener;


    public ClassAdapter(List<String> itemList){
        this.itemList = itemList;
    }

    public interface OnItemClickListener{

        void onItemClick(int position);
        void deleteClick(int position);
    }

    public void setOnItemClickListener(ClassAdapter.OnItemClickListener onItemClickListener)
    {
        mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image_item,parent,false);
        return new ClassHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassHolder holder, int position) {

        holder.classname.setText(itemList.get(position));
        holder.classname.setFocusableInTouchMode(true);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ClassHolder extends RecyclerView.ViewHolder{

        TextView classname;
        ImageView delete;
        TextView dataset;

        public ClassHolder(@NonNull View itemView) {
            super(itemView);

            classname = (TextView) itemView.findViewById(R.id.classname);
            delete = (ImageView) itemView.findViewById(R.id.deleteclass);
            dataset = (TextView) itemView.findViewById(R.id.databtn);

            dataset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null)
                    {
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION)
                        {
                            mListener.onItemClick(pos);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null)
                    {
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION)
                        {
                            mListener.deleteClick(pos);
                        }
                    }
                }
            });




        }
    }
}
