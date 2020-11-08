package com.example.necip.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppUser extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView wpListView;
    ArrayList<String> waUsers;
    ArrayAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_user);

        setTitle("What'sApp Clone");

        wpListView = findViewById(R.id.wpListView);
        waUsers = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, waUsers);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        wpListView.setOnItemClickListener(this);

        try {

            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        //FancyToast.makeText(WhatsAppUser.this, e.getMessage(), Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

                        for (ParseUser user : objects) {
                            waUsers.add(user.getUsername());
                        }

                        wpListView.setAdapter(adapter);

                    } else
                        FancyToast.makeText(WhatsAppUser.this, e.getMessage(), Toast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {

                    ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereNotContainedIn("username", waUsers);
                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() > 0 && e == null) {
                                for (ParseUser user : objects) {
                                    waUsers.add(user.getUsername());
                                }
                                adapter.notifyDataSetChanged();     //for updating data

                                if (swipeRefreshLayout.isRefreshing())      //after data update, we dont want refreshing wont be continue
                                    swipeRefreshLayout.setRefreshing(false);

                            } else {
                                if (swipeRefreshLayout.isRefreshing())      //for refreshing wont be continue
                                    swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout_item:
                FancyToast.makeText(WhatsAppUser.this, ParseUser.getCurrentUser().getUsername() + " is logged out!", Toast.LENGTH_LONG, FancyToast.DEFAULT, true).show();
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Intent intent = new Intent(WhatsAppUser.this, SignUp.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent = new Intent(this, WhatsAppChat.class);
        intent.putExtra("selectedUser", waUsers.get(position));
        startActivity(intent);

    }
}