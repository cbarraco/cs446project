package ca.uwaterloo.mapapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.uwaterloo.mapapp.R;
import ca.uwaterloo.mapapp.data.DatabaseHelper;
import ca.uwaterloo.mapapp.logic.net.ServerRestApi;
import ca.uwaterloo.mapapp.shared.ICallback;
import ca.uwaterloo.mapapp.shared.data.DataManager;
import ca.uwaterloo.mapapp.shared.objects.event.Event;
import ca.uwaterloo.mapapp.shared.objects.event.EventImage;
import ca.uwaterloo.mapapp.shared.objects.event.EventNote;
import ca.uwaterloo.mapapp.shared.objects.event.EventRanking;
import ca.uwaterloo.mapapp.shared.objects.event.EventTimes;
import ca.uwaterloo.mapapp.ui.adapters.EventNoteAdapter;
import ca.uwaterloo.mapapp.util.ListViewUtil;

public class ViewEventActivity extends ActionBarActivity {
    public static final String ARG_EVENT_ID = "ARG_EVENT_ID";

    @InjectView(R.id.tool_bar)
    protected Toolbar mToolbar;
    @InjectView(R.id.event_title)
    protected TextView title;
    @InjectView(R.id.event_location_txt)
    protected TextView location;
    @InjectView(R.id.event_link_btn)
    protected Button link;
    @InjectView(R.id.event_notes_list)
    protected ListView mNoteList;
    @InjectView(R.id.event_more_notes)
    protected Button moreNotes;
    @InjectView(R.id.galleryButton)
    protected Button galleryButton;

    private Event mEvent;

    private List<EventNote> mEventNotes;
    private EventNoteAdapter mAdapter;
    private List<EventTimes> mEventTimes;
    private List<EventRanking> mEventRankings;
    private List<EventImage> mEventImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
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

        Bundle b = getIntent().getExtras();
        long eventId = b.getLong(ARG_EVENT_ID, -1);
        setEvent(eventId);

        mNoteList.setEmptyView(findViewById(R.id.empty_list_state));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.slide_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void setEvent(long eventId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        DataManager<Event, Long> dataManager = databaseHelper.getDataManager(Event.class);

        mEvent = dataManager.findFirst(Event.COLUMN_ID, eventId);
        title.setText(Html.fromHtml(mEvent.getTitle()));

        if (mEvent.getLocation() != null && !mEvent.getLocation().isEmpty()) {
            location.setText(mEvent.getLocation());

        } else {
            location.setText("University of Waterloo");
        }

        if (mEvent.getLink() != null && !mEvent.getLink().isEmpty()) {
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = mEvent.getLink();
                    if (!link.startsWith("https://") && !link.startsWith("http://")) {
                        link = "http://" + link;
                    }

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            });

        } else {
            link.setVisibility(View.INVISIBLE);
        }

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEventActivity.this, EventGalleryActivity.class);
                intent.putExtra(EventGalleryActivity.ARG_FILTER_VALUE_INTEGER, mEvent.getId());
                startActivity(intent);
            }
        });

        loadEventNotes();
        loadEventTimes();
        loadEventRankings();
        loadEventImages();
    }

    private void loadEventNotes() {
        final Context context = this;
        ICallback callback = new ICallback() {
            @Override
            public void call(Object param) {
                mEventNotes = (List<EventNote>) param;
                mAdapter = new EventNoteAdapter(context, mEventNotes);
                mNoteList.setAdapter(mAdapter);
                ListViewUtil.setListViewHeightBasedOnChildren(mNoteList);

                int visibility = mEventNotes.isEmpty() ? View.INVISIBLE : View.VISIBLE;
                moreNotes.setVisibility(visibility);
            }
        };
        ServerRestApi.requestEventNotes(callback, mEvent.getId());
    }

    private void loadEventTimes() {
        final Context context = this;
        ICallback callback = new ICallback() {
            @Override
            public void call(Object param) {
                mEventTimes = (List<EventTimes>) param;
                // process/display event times here
            }
        };
        ServerRestApi.requestEventTimes(callback, mEvent.getId());
    }

    private void loadEventRankings() {
        final Context context = this;
        ICallback callback = new ICallback() {
            @Override
            public void call(Object param) {
                mEventRankings = (List<EventRanking>) param;
                // process/display event rankings here
            }
        };
        ServerRestApi.requestEventRanking(callback, mEvent.getId());
    }

    private void loadEventImages() {
        final Context context = this;
        ICallback callback = new ICallback() {
            @Override
            public void call(Object param) {
                mEventImages = (List<EventImage>) param;
                // process/display event rankings here
            }
        };
        ServerRestApi.requestEventImages(callback, mEvent.getId());
    }
}
