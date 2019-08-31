package com.example.topnews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.example.topnews.adapter.ChannelAdapter;
import com.example.topnews.bean.Category;
import com.example.topnews.bean.CategoryManage;
import com.example.topnews.helper.ItemDragHelperCallback;

import java.util.List;

public class ChannelActivity extends AppCompatActivity {
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        rv = findViewById(R.id.recycler);
        init();
    }

    private void init() {
        CategoryManage categoryManage = new CategoryManage();
        final List<Category> userChannel = categoryManage.getUserChannel();
        final List<Category> otherChannel = categoryManage.getOtherChannel();

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        rv.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);

        final ChannelAdapter adapter = new ChannelAdapter(this, helper, userChannel, otherChannel);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                int viewType = adapter.getItemViewType(i);
                return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
            }
        });
        rv.setAdapter(adapter);

        adapter.setOnMyChannelItemClickListener(new ChannelAdapter.OnMyChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(ChannelActivity.this, userChannel.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.saver.save();
        MainActivity.base.columnChange();
    }
}
