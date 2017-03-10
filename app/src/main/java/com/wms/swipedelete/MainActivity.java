package com.wms.swipedelete;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.swipedelete.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.idRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        final List<Item> lists = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Item item = new Item("Item" + i);
            lists.add(item);
        }

        mainBinding.idRecyclerview.setAdapter(new CommonAdapter<Item>(this, lists, R.layout.item_menu) {
            @Override
            public void convert(final CommonViewHolder holder, final Item item) {
                holder.setText(R.id.item_content, item.value);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), item.value, Toast.LENGTH_SHORT).show();
                    }
                });

                holder.getView(R.id.negative).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "删除", Toast.LENGTH_SHORT).show();
                        lists.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
                holder.getView(R.id.positive).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "取消", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private class Item {
        public String value;

        public Item(String value) {
            this.value = value;
        }
    }
}
