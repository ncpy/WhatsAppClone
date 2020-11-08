package com.example.necip.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppChat extends AppCompatActivity implements View.OnClickListener{

    private String selectedUser;
    //private EditText edtChat;
    private ListView chatListView;
    private ArrayList<String> chatsList;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_chat);

        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this, "Chat with " + selectedUser + " Now!!", Toast.LENGTH_LONG, FancyToast.INFO, true).show();

        findViewById(R.id.btnSend).setOnClickListener(this);        //enteresan bir kullanım

        chatListView = findViewById(R.id.chatListView);
        chatsList = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatsList);
        chatListView.setAdapter(adapter);

        try {
            ParseQuery<ParseObject> firstparseQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondparseQuery = ParseQuery.getQuery("Chat");

            firstparseQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstparseQuery.whereEqualTo("waTarget", selectedUser);

            secondparseQuery.whereEqualTo("waSender", selectedUser);
            secondparseQuery.whereEqualTo("waTarget", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstparseQuery);
            allQueries.add(secondparseQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject chatObject : objects) {
                            String waMessage = chatObject.get("waMessage") + "";

                            if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername()))
                                waMessage = ParseUser.getCurrentUser().getUsername() + ": " + waMessage;

                            if (chatObject.get("waSender").equals(selectedUser))
                                waMessage = selectedUser + ": " + waMessage;

                            chatsList.add(waMessage);
                        }
                        adapter.notifyDataSetChanged();

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onClick(View view) {

        final EditText edtChat = findViewById(R.id.edtChat);   // for less memory usage, here is the best place for this value. this runs only button clicks. After that it dies.

        ParseObject parseObject = new ParseObject("Chat");
        parseObject.put("waSender", ParseUser.getCurrentUser().getUsername());
        parseObject.put("waTarget", selectedUser);
        parseObject.put("waMessage", edtChat.getText().toString());
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(WhatsAppChat.this, "Message from " + ParseUser.getCurrentUser().getUsername() + " sent to " + selectedUser, Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

                    chatsList.add(ParseUser.getCurrentUser().getUsername() + ": " + edtChat.getText().toString());
                    adapter.notifyDataSetChanged();     //for updating
                    edtChat.setText("");
                }
            }
        });

    }
}

// profile photo
// sending files
// ..