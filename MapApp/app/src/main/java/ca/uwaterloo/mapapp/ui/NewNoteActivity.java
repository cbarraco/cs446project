package ca.uwaterloo.mapapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.uwaterloo.mapapp.R;
import ca.uwaterloo.mapapp.data.DataManager;
import ca.uwaterloo.mapapp.data.DatabaseHelper;
import ca.uwaterloo.mapapp.data.objects.Note;

public class NewNoteActivity extends ActionBarActivity {

    @InjectView(R.id.tool_bar)
    protected Toolbar mToolbar;

    @InjectView(R.id.note_title)
    protected EditText mTitle;
    @InjectView(R.id.note_desc)
    protected EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    public void onBackPressed() {
        String message = "Cannot save empty note";

        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();

        if (!title.isEmpty()) {
            Note note = new Note();
            note.setTitle(title);
            note.setDescription(description);
            // TODO get building code from intent
            //note.setBuildingCode(buildingCode);

            // Insert note into database
            DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper(this);
            DataManager<Note, Long> dataManager = databaseHelper.getDataManager(Note.class);
            dataManager.insert(note);

            message = "Note saved";
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.slide_down);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
