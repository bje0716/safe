package kr.ac.dongeui.pangpang.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import kr.ac.dongeui.pangpang.R;
import kr.ac.dongeui.pangpang.adapter.HelperAdapter;
import kr.ac.dongeui.pangpang.databinding.ActivityUserBinding;
import kr.ac.dongeui.pangpang.util.Util;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        binding.appbar.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(new HelperAdapter());
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
