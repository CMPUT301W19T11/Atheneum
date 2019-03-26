package com.example.atheneum.utils;

/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.atheneum.models.Book;
import com.example.atheneum.R;
import com.example.atheneum.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * The type Request adapter handles displaying arrays.
 */
//refer from https://www.sitepoint.com/custom-data-layouts-with-your-own-android-arrayadapter/ on Mar 2, 2019
public class requestAdapter extends ArrayAdapter {
    private int resource_id;
    private static User owner;
    private static final String TAG = "FindOwner";

    /**
     * Instantiates a new Request adapter.
     *
     * @param context  the context
     * @param resource the resource
     * @param objects  the objects
     */
    public requestAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resource_id = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Pair pair = (Pair) getItem(position);
        Book book = (Book) pair.first;
        String status = (String) pair.second;
        View view;
        class ViewHolder{
            TextView rStatus;
            TextView show_owner;
            TextView show_title;

        }
        final ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resource_id, null);
            viewHolder = new ViewHolder();
            viewHolder.rStatus = (TextView) view.findViewById(R.id.rStatus);
            viewHolder.show_owner = (TextView) view.findViewById(R.id.show_owner);
            viewHolder.show_title = (TextView) view.findViewById(R.id.show_title);

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref_owner = db.getReference("users").child(book.getOwnerID());
        Log.d(TAG, "find owner " + book.getOwnerID());
        ref_owner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                     owner = dataSnapshot.getValue(User.class);
                     Log.d(TAG, "find owner " + owner.getUserID());
                     viewHolder.show_owner.setText( owner.getUserName());
                }
//                Log.d(TAG, "find owner no " );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.rStatus.setText(status);
        if(status.equals("PENDING")){
            viewHolder.rStatus.setTextColor(Color.RED);
        }
        else if(status.equals("ACCEPTED")){
            viewHolder.rStatus.setTextColor(Color.BLUE);

        }
        else if(status.equals("DECLINED")){
            viewHolder.rStatus.setTextColor(Color.YELLOW);
        }

        viewHolder.show_title.setText(book.getTitle());


        return view;
    }
}
