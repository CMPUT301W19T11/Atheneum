package com.example.atheneum.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AvailableBookAdapter extends ArrayAdapter {
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
    public AvailableBookAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resource_id = resource;
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
            TextView show_status;

        }
        final ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resource_id, null);
            viewHolder = new ViewHolder();
            viewHolder.show_description = (TextView) view.findViewById(R.id.show_description);
            viewHolder.show_owner = (TextView) view.findViewById(R.id.show_owner);
            viewHolder.show_title = (TextView) view.findViewById(R.id.show_title);
            viewHolder.show_status = (TextView) view.findViewById(R.id.show_status);


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

        viewHolder.show_description.setText(book.getDescription());

        viewHolder.show_title.setText(book.getTitle());

        viewHolder.show_status.setText(book.getStatus().toString());
        if (book.getStatus() == Book.Status.ACCEPTED) {
            viewHolder.show_status.setTextColor(Color.parseColor("#ce6412"));
        } else if (book.getStatus() == Book.Status.AVAILABLE) {
            viewHolder.show_status.setTextColor(Color.parseColor("#008577"));
        } else if (book.getStatus() == Book.Status.REQUESTED) {
            viewHolder.show_status.setTextColor(Color.parseColor("#f4c842"));
        } else if (book.getStatus() == Book.Status.BORROWED) {
            viewHolder.show_status.setTextColor(Color.parseColor("#af270f"));
        }


        return view;
    }
}
