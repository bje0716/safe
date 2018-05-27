package kr.ac.dongeui.pangpang.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import kr.ac.dongeui.pangpang.R;
import kr.ac.dongeui.pangpang.adapter.NotificationAdapter;
import kr.ac.dongeui.pangpang.databinding.ActivityNotificationBinding;
import kr.ac.dongeui.pangpang.util.Util;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(Util.setBackArrowColor(this));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = new NotificationAdapter();
        if (adapter.getItemCount() == 0) {
            binding.noItem.setVisibility(View.VISIBLE);
        }

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
