package com.example.teachablemachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectHolder> {

    private List<String> itemList = new ArrayList<String>();

    private OnItemClickListener mListener;

    public ProjectAdapter(List<String> itemList){
        this.itemList = itemList;
    }

    public interface OnItemClickListener{

        void onItemClick(int position);
        void deleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.project_item,parent,false);
        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder holder, int position) {

        holder.foldername.setText(itemList.get(position));



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ProjectHolder extends RecyclerView.ViewHolder{

        TextView foldername;
        LinearLayout folder;
        ImageView delete;


        public ProjectHolder(@NonNull View itemView) {
            super(itemView);

            foldername = (TextView) itemView.findViewById(R.id.name);
            folder = (LinearLayout) itemView.findViewById(R.id.project);
            delete = (ImageView) itemView.findViewById(R.id.delete);

            foldername.setOnClickListener(new View.OnClickListener() {
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
                public void onClick(View view) {
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