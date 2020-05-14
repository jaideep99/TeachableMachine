package com.example.teachablemachine;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProbAdapter extends RecyclerView.Adapter<ProbAdapter.ProbHolder>{

    List<probitem> itemList = new ArrayList<probitem>();

    String[] colors = new String[]{"#ff901e","#800080","#0077cc","#5eba7d","#d1383d"};

    ProbAdapter(List<probitem> items){
        this.itemList = items;
    }

    @NonNull
    @Override
    public ProbHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.probitem,parent,false);
        return new ProbAdapter.ProbHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProbHolder holder, int position) {

        holder.classname.setText(itemList.get(position).getClassname());
        float prob = itemList.get(position).getProb();
        holder.color.setBackgroundColor(Color.parseColor(colors[position]));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1-prob
        );
        holder.maxi.setLayoutParams(param);

        param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                prob
        );

        holder.mini.setLayoutParams(param);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ProbHolder extends RecyclerView.ViewHolder{

        TextView classname;
        LinearLayout maxi,mini;
        View color;

        public ProbHolder(@NonNull View itemView) {
            super(itemView);

            classname = (TextView) itemView.findViewById(R.id.classname);
            maxi = (LinearLayout) itemView.findViewById(R.id.max);
            mini = (LinearLayout) itemView.findViewById(R.id.mini);
            color = (View) itemView.findViewById(R.id.maxcolor);
        }
    }
}
