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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.R;
import com.example.atheneum.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


//refer from https://www.sitepoint.com/custom-data-layouts-with-your-own-android-arrayadapter/ on Mar 2, 2019
public class requestAdapter extends ArrayAdapter {
    private int resourse_id;
    private static User owner;
    private static final String TAG = "FindOwner";
    public requestAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourse_id = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Book book = (Book) getItem(position);
        View view;
        class ViewHolder{
            TextView show_description;
            TextView show_owner;
            TextView show_title;

        }
        final ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourse_id, null);
            viewHolder = new ViewHolder();
            viewHolder.show_description = (TextView) view.findViewById(R.id.show_description);
            viewHolder.show_owner = (TextView) view.findViewById(R.id.show_owner);
            viewHolder.show_title = (TextView) view.findViewById(R.id.show_title);

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
//        viewHolder.show_date.setText(dateFormat.format(measurement.getDate()));
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

        viewHolder.show_description.setText(book.getDescription());

        viewHolder.show_title.setText(book.getTitle());


        return view;
    }
}
