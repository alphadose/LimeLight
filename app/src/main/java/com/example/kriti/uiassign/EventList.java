package com.example.kriti.uiassign;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kriti.uiassign.Retrofit.RetrofitModule;
import com.example.kriti.uiassign.beans.*;

import java.util.ArrayList;
import java.util.List;

public class EventList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new EventAdapter(Utils.events);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView tr = (TextView) view.findViewById(R.id.eventName);
                        String eventName = tr.getText().toString();
                        Log.d("Recycle", eventName);
                        Intent intent = new Intent(EventList.this, MapsActivity.class);
                        intent.putExtra("EventName", eventName);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        mAdapter.notifyDataSetChanged();
    }

    /*FloatingActionButton floatingActionButton =
            (FloatingActionButton) findViewById(R.id.fab);

    floatingActionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    });*/
    public void addEvent(View view) {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }

}