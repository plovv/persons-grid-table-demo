package com.plovv.demopersonstable.application;

import android.app.Application;

public class DemoPersonsTableApp extends Application {

    // single instance
    private static DemoPersonsTableApp instance;

    public static DemoPersonsTableApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

}
