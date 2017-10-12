package com.example.BonusHub.recycler;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.BonusHub.db.staff.Staff;
import com.example.timur.BonusHub.R;

import java.util.ArrayList;

/**
 * Created by Timur on 22-Sep-17.
 */


public class StaffRecyclerAdapter extends RecyclerView.Adapter<StaffRecyclerAdapter.StaffHolder> {

    private static ArrayList<Staff> staffList;

    public StaffRecyclerAdapter(ArrayList<Staff> staff) {
        staffList= staff;
    }

    @Override
    public StaffRecyclerAdapter.StaffHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_staff_row, parent, false);
        return new StaffHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(StaffRecyclerAdapter.StaffHolder holder, int position) {
        Staff staffItem = staffList.get(position);
        holder.bindStaff(staffItem);
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }


    public void removeItem(int position) {
        staffList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Staff staff, int position) {
        staffList.add(position, staff);
        // notify item added by position
        notifyItemInserted(position);
    }

    public static class StaffHolder extends RecyclerView.ViewHolder{
        private TextView title;

        public RelativeLayout viewBackgroundDelete,viewForeground;
        public StaffHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.row_title);
            viewBackgroundDelete = (RelativeLayout) v.findViewById(R.id.view_background_delete);
            viewForeground = (RelativeLayout) v.findViewById(R.id.view_foreground);
        }

        public void bindStaff(Staff staff) {
            title.setText(staff.getLogin());
        }

    }
}
