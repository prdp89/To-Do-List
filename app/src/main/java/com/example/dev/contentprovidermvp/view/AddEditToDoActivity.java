package com.example.dev.contentprovidermvp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.m1040033.contentprovidermvp.R;
import com.example.dev.contentprovidermvp.utils.ActivityUtils;

public class AddEditToDoActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 100;
    public static final int REQUEST_EDIT_TASK = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_to_do);

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddEditToDoFragment addEditToDoFragment =
                (AddEditToDoFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (addEditToDoFragment == null) {
            // Create the fragment
            addEditToDoFragment = AddEditToDoFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), addEditToDoFragment, R.id.contentFrame);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED,getIntent());
        finish();
    }
}
