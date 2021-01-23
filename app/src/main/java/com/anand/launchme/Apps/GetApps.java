package com.anand.launchme.Apps;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anand.launchme.R;
import com.anand.launchme.Adadters.myListAdap;
import com.anand.launchme.AppSettings.SettingsActivity;
import com.anand.launchme.Appinfo.AppInfo;
import com.anand.launchme.Home.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetApps extends Activity {

    private Context context;
    private EditText etSearch;
    private RelativeLayout rootLayout;
    private RecyclerView recyclerView;
    private ImageView settingsBtnImg;

    private PackageManager packageManager;
    private Animation startAnimation;
    private myListAdap listAdap;


    private List<AppInfo> apps;
    private ArrayAdapter<AppInfo> adapter;

    private int spanCount = 0;
    private String gridCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_applist);

        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        recyclerView = (RecyclerView) findViewById(R.id.rec_list);
        etSearch = (EditText) findViewById(R.id.et_search);
        settingsBtnImg = (ImageView) findViewById(R.id.settings_icon_img);

        gridCount = getIntent().getStringExtra("GRID_NO");
        spanCount = Integer.parseInt(gridCount);
        Log.d("TAG_NO2", spanCount + "");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GetApps.this, RecyclerView.VERTICAL, true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(GetApps.this, spanCount, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        rootLayout.post(new Runnable() {

            @Override
            public void run() {
                rootLayout.setBackground(wallpaperDrawable);
            }
        });

        apps = null;
        adapter = null;
        loadApps();
        loadNewListView();

        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.down_to_up);

        final LayoutAnimationController controller = new LayoutAnimationController(startAnimation, 0.2f);

        rootLayout.setLayoutAnimation(controller);

        settingsBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetApps.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                GetApps.this.startActivity(intent);
            }
        });


    }


    private void loadNewListView() {

        listAdap = new myListAdap(GetApps.this, apps);

        recyclerView.setAdapter(listAdap);


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.d("TAG_APP", "**********Message : " + editable.toString());

                System.out.print("******************************************editable.toString()=" + editable.toString());

                try {
                    if (listAdap != null) {
                        listAdap.getFilter().filter(editable.toString());
                    } else {
                        Log.d("TAG_APP", "**********Message-1 : " + editable.toString());
                    }

                } catch (Exception e) {
                    Log.e("Error loadApps", e.getMessage());
                }


            }
        });
    }


    private void loadApps() {
        try {


            packageManager = getPackageManager();
            if (apps == null) {
                apps = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                Collections.sort(availableApps, new ResolveInfo.DisplayNameComparator(packageManager));
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    apps.add(appinfo);

                }
            }

        } catch (Exception ex) {
            Toast.makeText(GetApps.this, ex.getMessage().toString() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage().toString() + " loadApps");
        }

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(GetApps.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("GRID_NO", gridCount);
        GetApps.this.startActivity(i);
    }


}
