package prezcast.sgu.fr.showcast;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import prezcast.sgu.fr.showcast.roboguice.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Screen to manage the presentation.
 * Select display
 * Show timer
 * Show notes
 */
@ContentView(R.layout.activity_presentation_mobile)
public class PresentationActivity extends RoboActionBarActivity {

    /////    CONSTANTS

    /**
     * Key to get intent parameter.
     */
    public static final String INTENT_ARG_DIRECTORY = "directory";

    public static final String PRESENTATION_SAVE_KEY = "savedPresentation";
    /**
     * Tag for log.
     */
    private static final String TAG = "ShowCast-Presentation";


    //////   VIEW Objects

    /**
     * Start button.
     */
    @InjectView(R.id.presentation_mobile_start)
    private Button buttonStart;

    /**
     * Presentation listing.
     */
    @InjectView(R.id.presentation_mobile_list_device)
    private ListView listView;

    @InjectView(R.id.presentation_mobile_note)
    private TextView notePresentation;


    ///////  Other

    /**
     * Display manager.
     */
    private DisplayManager mDisplayManager;

    /**
     * List of presentation contents indexed by displayId.
     */
    private SparseArray<PresentationContents> mSavedPresentationContents;

    // List of all currently visible presentations indexed by display id.
    private final SparseArray<TvPresentation> mActivePresentations = new SparseArray<>();

    /**
     * Display selected.
     */
    private Display selectedDisplay;

    /**
     * Presentation directory.
     */
    private String directory;

    /**
     * Presentation notes.
     */
    @InjectView(R.id.presentation_mobile_note)
    private TextView note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore active presentation content
        if (savedInstanceState != null) {
            mSavedPresentationContents =
                    savedInstanceState.getSparseParcelableArray(PRESENTATION_SAVE_KEY);
            directory =
                    savedInstanceState.getString(INTENT_ARG_DIRECTORY);
        } else {
            mSavedPresentationContents = new SparseArray<>();
            directory = getIntent().getStringExtra(INTENT_ARG_DIRECTORY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        boolean hasActivePresentation = false;
        // Restore presentations from before the activity was paused.
        for (Display d : mDisplayManager.getDisplays()) {
            if (mActivePresentations.get(d.getDisplayId()) != null) {
                final PresentationContents contents = mSavedPresentationContents.get(d.getDisplayId());
                if (contents != null) {
                    showPresentation(d, contents);
                    hasActivePresentation = true;
                }
            }
        }
        mSavedPresentationContents.clear();

        if (hasActivePresentation) {
            updatePresentationNote();
        } else {
            initView();
        }
    }

    @Override
    protected void onPause() {
        // Be sure to call the super class.
        super.onPause();

        // Dismiss all of our presentations but remember their contents.
        Log.d(TAG, "Activity is being paused.  Dismissing all active presentation.");
        for (int i = 0; i < mActivePresentations.size(); i++) {
            TvPresentation presentation = mActivePresentations.valueAt(i);
            int displayId = mActivePresentations.keyAt(i);
            mSavedPresentationContents.put(displayId, presentation.contents);
            presentation.dismiss();
        }
        mActivePresentations.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save active active presentation content
        outState.putSparseParcelableArray(PRESENTATION_SAVE_KEY, mSavedPresentationContents);
        outState.putString(INTENT_ARG_DIRECTORY, directory);
    }

    private void initView() {
        buttonStart.setEnabled(false);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresentationContents contents = new PresentationContents(directory, 0);
                mSavedPresentationContents.put(selectedDisplay.getDisplayId(), contents);
                showPresentation(selectedDisplay, contents);
                updatePresentationNote();
            }
        });

        final List<String> deviceName = new ArrayList<>();
        for (Display d : mDisplayManager.getDisplays()) {
            deviceName.add(d.getName());
        }
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceName);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDisplay = mDisplayManager.getDisplays()[position];
                Log.d(TAG, "Display selected:" + selectedDisplay.getName());
                buttonStart.setEnabled(true);
            }
        });


    }

    private void updatePresentationNote() {
        File f = new File(MainActivity.APP_SDCARD_DIRECTORY + "/" + directory + "/note/");
        File[] fileTab = f.listFiles();
        Arrays.sort(fileTab);
        File text = fileTab[mSavedPresentationContents.get(selectedDisplay.getDisplayId()).index];

        //création de l'objet de lecture
        FileReader fr = null;
        String str = "";
        try {
            fr = new FileReader(text);
            str = "";
            int i = 0;
            //Lecture des données
            while ((i = fr.read()) != -1) {
                str += (char) i;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        notePresentation.setText(str);

    }

    /**
     * Show Presentation on tv screen.
     *
     * @param display  Tv screen
     * @param contents Content
     */
    private void showPresentation(Display display, PresentationContents contents) {
        final int displayId = display.getDisplayId();
        if (mActivePresentations.get(displayId) != null) {
            return;
        }

        Log.d(TAG, "Showing presentation on display #" + displayId + ".");

        TvPresentation presentation = new TvPresentation(this, display, contents);
        presentation.show();
        presentation.setOnDismissListener(mOnDismissListener);
        mActivePresentations.put(displayId, presentation);
    }

    private final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    TvPresentation presentation = (TvPresentation) dialog;
                    int displayId = presentation.getDisplay().getDisplayId();
                    Log.d(TAG, "Presentation on display #" + displayId + " was dismissed.");
                    mActivePresentations.delete(displayId);
                }
            };

    /*
    class DisplayAdaterListenerImpl implements DisplayAdapter.DisplayAdapterListener{

        @Override
        public void onDisplaySelected(Display d) {
            selectedDisplay = d;
            Log.d(TAG,"Display selected:"+selectedDisplay.getName());
            buttonStart.setEnabled(true);
        }
    }
    */
}