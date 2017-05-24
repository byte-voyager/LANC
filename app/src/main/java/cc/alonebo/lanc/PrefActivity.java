package cc.alonebo.lanc;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import cc.alonebo.lanc.fragment.PrefFragment;


/**
 * Created by alonebo on 17-4-30.
 */

public class PrefActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        MyApplication.activityList.add(this);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //init toolbar
        Toolbar toolbar_chat_activity = (Toolbar) findViewById(R.id.pref_activity_toolbar);
        setSupportActionBar(toolbar_chat_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");

        android.app.FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fl_pref_content,new PrefFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
