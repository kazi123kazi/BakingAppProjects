package com.example.bakingapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}
