package ca.uwaterloo.mapapp.ui;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.uwaterloo.mapapp.R;
import ca.uwaterloo.mapapp.data.DatabaseHelper;
import ca.uwaterloo.mapapp.objects.Note;
import ca.uwaterloo.mapapp.shared.data.DataManager;
import ca.uwaterloo.mapapp.shared.objects.building.Building;
import ca.uwaterloo.mapapp.shared.objects.event.Event;
import ca.uwaterloo.mapapp.ui.adapters.EventAdapter;
import ca.uwaterloo.mapapp.ui.adapters.NoteAdapter;

public class BuildingCardFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener{

    private static final int MAX_VIEW_AMOUNT = 5;

    @InjectView(R.id.icard_top)
    protected LinearLayout mCardTop;
    @InjectView(R.id.icard_buildName)
    protected TextView mBuildingName;
    @InjectView(R.id.icard_buildcode)
    protected TextView mBuildingCode;

    @InjectView(R.id.icard_content_scroll)
    protected ScrollView mScrollContent;
    @InjectView(R.id.icard_floor_plan_btn)
    protected Button mFloorPlanBtn;
    @InjectView(R.id.icard_events)
    protected ListView mEventList;
    @InjectView(R.id.icard_more_events)
    protected Button mMoreEvents;
    @InjectView(R.id.icard_notes)
    protected ListView mNoteList;
    @InjectView(R.id.icard_more_notes)
    protected Button mMoreNotes;


    private FloatingActionButton mFab;

    private Building mBuilding;

    private List<Event> mEvents;
    private EventAdapter mEventAdapter;

    private List<Note> mNotes;
    private NoteAdapter mNoteAdapter;

    private int mPriCurrentColor;
    private int mSecCurrentColor;

    private boolean isScrollEnabled = false;

    public static BuildingCardFragment newInstance() {
        return new BuildingCardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPriCurrentColor = Color.WHITE;
        mSecCurrentColor = getResources().getColor(R.color.primary);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_building_card, container, false);
        ButterKnife.inject(this, view);

        // empty states
        mEventList.setEmptyView(view.findViewById(R.id.empty_events_list_state));
        mNoteList.setEmptyView(view.findViewById(R.id.empty_note_list_state));

        mNoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewNoteActivity.class);
                intent.putExtra(ViewNoteActivity.ARG_NOTE_ID, id);
                startActivityForResult(intent, NewEditNoteActivity.REQUEST_UPDATE);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.nothing);
            }
        });

        mFloorPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FloorPlanActivity.class);
                startActivity(intent);
            }
        });

        mScrollContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isScrollEnabled;
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) return;

        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        DataManager<Note, Long> dataManager = databaseHelper.getDataManager(Note.class);
        Note note = dataManager.findById(data.getLongExtra(NewEditNoteActivity.RESULT_NOTE_ID, -1));

        if (requestCode == NewEditNoteActivity.REQUEST_UPDATE) {
            long noteId = data.getLongExtra(NewEditNoteActivity.RESULT_NOTE_ID, -1);
            for (int i = 0; i < mNotes.size(); i++) {
                if (mNotes.get(i).getId() == noteId) {
                    mNotes.remove(i);
                    break;
                }
            }
        }
        mNotes.add(0, note);
        mNoteAdapter.notifyDataSetChanged();
    }

    public void populateCard(Building building) {
        mBuilding = building;
        mBuildingName.setText(mBuilding.getBuildingName());
        mBuildingCode.setText(mBuilding.getBuildingCode());

        updateEventList();
        updateNoteList();
    }

    // TODO: implement
    public void updateEventList() {

        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        DataManager<Event, Long> dataManager = databaseHelper.getDataManager(Event.class);

        mEvents = dataManager.find(Event.COLUMN_LOCATION, mBuilding.getBuildingCode());

        if (mEvents.size() > MAX_VIEW_AMOUNT) {
            mEvents = mEvents.subList(0, MAX_VIEW_AMOUNT);
            mMoreEvents.setVisibility(View.VISIBLE);
        } else {
            mMoreEvents.setVisibility(View.INVISIBLE);
        }

        mEventAdapter = new EventAdapter(getActivity(), mEvents);
        mEventList.setAdapter(mEventAdapter);
        setListViewHeightBasedOnChildren(mEventList);
    }

    public void updateNoteList() {
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        DataManager<Note, Long> dataManager = databaseHelper.getDataManager(Note.class);
        mNotes = dataManager.find(Note.COLUMN_BUILDING_CODE, mBuilding.getBuildingCode(),
                Note.COLUMN_LAST_MODIFIED, false);

        if (mNotes.size() > MAX_VIEW_AMOUNT) {
            mNotes = mNotes.subList(0, MAX_VIEW_AMOUNT);
            mMoreNotes.setVisibility(View.VISIBLE);
        } else {
            mMoreNotes.setVisibility(View.INVISIBLE);
        }

        mNoteAdapter = new NoteAdapter(getActivity(), mNotes);
        mNoteList.setAdapter(mNoteAdapter);
        setListViewHeightBasedOnChildren(mNoteList);
    }

    public void setFab(FloatingActionButton fab) {
        this.mFab = fab;
    }

    public void changeCardTopStyling(boolean expand) {
        if (expand) {
            mBuildingName.setMaxLines(3);
            mBuildingName.setTextColor(Color.WHITE);
            mBuildingCode.setTextColor(getResources().getColor(R.color.primaryLight));
        } else {
            mBuildingName.setMaxLines(1);
            mBuildingName.setTextColor(Color.BLACK);
            mBuildingCode.setTextColor(getResources().getColor(R.color.textSecondary));
        }
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // PANEL SLIDE LISTENER METHODS
    @Override
    public void onPanelCollapsed(View view) {    }

    @Override
    public void onPanelSlide(View view, float v) {
        int priColorFrom = mPriCurrentColor;
        int priColorTo;
        boolean isExpanded = v > 0;
        if (isExpanded) {
            priColorTo = getResources().getColor(R.color.primary);
        } else {
            priColorTo = Color.WHITE;
        }

        if (priColorFrom != priColorTo) {

            // TODO: change building name and code colours

            ValueAnimator topAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), priColorFrom, priColorTo);
            topAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mCardTop.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });

            if (mFab != null) {
                ValueAnimator fabAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), priColorTo, priColorFrom);
                fabAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mFab.setColorNormal((Integer) animation.getAnimatedValue());
                        // TODO: fix pressed color as well
                    }
                });
                fabAnimation.setDuration(100);
                fabAnimation.start();
            }

            topAnimation.setDuration(100);
            topAnimation.start();
            mPriCurrentColor = priColorTo;

            changeCardTopStyling(isExpanded);
        }
    }

    @Override
    public void onPanelExpanded(View view) {
        isScrollEnabled = true;
    }

    @Override
    public void onPanelAnchored(View view) {
        isScrollEnabled = false;
    }

    @Override
    public void onPanelHidden(View view) {    }

    @Override
    public void onPanelLayout(View view, SlidingUpPanelLayout.PanelState panelState) {    }

    @Override
    public void onPanelHiddenExecuted(View view, Interpolator interpolator, int i) {    }

    @Override
    public void onPanelShownExecuted(View view, Interpolator interpolator, int i) {    }

    @Override
    public void onPanelExpandedStateY(View view, boolean b) {    }

    @Override
    public void onPanelCollapsedStateY(View view, boolean b) {    }
}
