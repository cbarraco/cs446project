package ca.uwaterloo.mapapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.uwaterloo.mapapp.R;
import ca.uwaterloo.mapapp.ui.adapters.FloorSelectorAdapter;

public class FloorPlanActivity extends ActionBarActivity {

    public static final String ARG_BUILDING_CODE = "arg_building_code";

    @InjectView(R.id.tool_bar)
    protected Toolbar mToolbar;
    @InjectView(R.id.floor_map)
    protected SubsamplingScaleImageView mFloorMap;
    @InjectView(R.id.floor_selector)
    protected ListView mFloorSelector;

    private boolean isOptionsVisible = true;
    private String mBuildingCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFloorMap.setImage(ImageSource.resource(R.drawable.test_floor_plan));
        mFloorMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOptionsVisible();
            }
        });

        loadFloorPlans(getIntent().getExtras());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void loadFloorPlans(Bundle b) {
        mBuildingCode = b.getString(ARG_BUILDING_CODE);



        mFloorSelector.setAdapter(new FloorSelectorAdapter(this, 5));
        loadFloor(1);
    }

    private void loadFloor(int floorNum) {

    }

    private void toggleOptionsVisible() {
        int visibility;
        if (isOptionsVisible)
            visibility = View.INVISIBLE;
        else
            visibility = View.VISIBLE;

        mFloorSelector.setVisibility(visibility);
        mToolbar.setVisibility(visibility);
        isOptionsVisible = !isOptionsVisible;
    }
}
