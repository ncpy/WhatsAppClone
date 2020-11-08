package com.example.necip.whatsappclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("GeylDYlpjMmCGYMgOOTL7DBxcF4P0mBMmRyB3dgl")
                // if defined
                .clientKey("lWf9iqmDVqmulvvisqzGQgrQjleAPrDQ7uOF27VF")
                .server("https://parseapi.back4app.com/")
                .build()
        );

    }
}
