package com.example.collection_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.collection_app.MainActivity.EXTRA_MESSAGE;

public class MyCustomAdapter extends BaseAdapter{
    private ArrayList<String> list;
    private Context context;

    public MyCustomAdapter(Context context, ArrayList<String> list) {
//        super(context,pick,list);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.button_list_view, null);
        }

        //Handle TextView and display string from your list
        TextView tvContact= (TextView)view.findViewById(R.id.list_item_string);
        final String the_text = list.get(position);
        tvContact.setText(the_text);

        //Handle buttons and add onClickListeners
        Button callbtn= (Button)view.findViewById(R.id.choose_btn);

        final DisplayMessageActivity tempcontext = (DisplayMessageActivity)this.context;
        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tempcontext, FeedbackActivity.class);
//        System.out.println("haha");
                intent.putExtra(tempcontext.EXTRA_MESSAGE, tempcontext.user + "_" + the_text);
                tempcontext.startActivity(intent);

            }
        });

        return view;
    }

}
