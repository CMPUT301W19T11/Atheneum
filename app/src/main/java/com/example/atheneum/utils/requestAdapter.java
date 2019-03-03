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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.atheneum.models.Request;
import com.example.atheneum.R;

import java.util.List;


//refer from https://www.sitepoint.com/custom-data-layouts-with-your-own-android-arrayadapter/ on Mar 2, 2019
public class requestAdapter extends ArrayAdapter {
    private int resourse_id;
    public requestAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourse_id = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Request request = (Request) getItem(position);
        View view;
        class ViewHolder{
            TextView show_bookID;
            TextView show_requester;
            TextView show_status;

        }
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourse_id, null);
            viewHolder = new ViewHolder();
            viewHolder.show_bookID = (TextView) view.findViewById(R.id.show_bookID);
            viewHolder.show_requester = (TextView) view.findViewById(R.id.show_requester);
            viewHolder.show_status = (TextView) view.findViewById(R.id.show_status);

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
//        viewHolder.show_date.setText(dateFormat.format(measurement.getDate()));
        viewHolder.show_bookID.setText(request.getBookID().toString());
        viewHolder.show_requester.setText(request.getRequester().getUserName());
        viewHolder.show_status.setText(request.getrStatus().toString());


        return view;
    }
}
