package prezcast.sgu.fr.showcast.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import prezcast.sgu.fr.showcast.db.setting.Setting;
import prezcast.sgu.fr.showcast.presentation.PresentationContents;
import prezcast.sgu.fr.showcast.R;
import prezcast.sgu.fr.showcast.presentation.TvPresentation;
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
public class PresentationActivity extends RoboActionBarActivity implements View.OnClickListener {

    /////    CONSTANTS
    // Intent parameter key
    public static final String INTENT_ARG_DIRECTORY = "directory";
    // Index to get saved currentIndex
    public static final String CURRENT_INDEX = "index";
    // Index to get saved device in presentation
    public static final String ACTIVE_DEVICE = "activeDevice";

    /**
     * Tag for log.
     */
    private static final String TAG = "ShowCast-Presentation";


    //////   VIEW Objects

    // Start presentation button
    @InjectView(R.id.presentation_mobile_start)
    private Button buttonStart;

    // Display list
    @InjectView(R.id.presentation_mobile_list_device)
    private ListView listView;

    // Note during presentation
    @InjectView(R.id.presentation_mobile_note)
    private TextView notePresentation;

    // Layout used during the presentation
    @InjectView(R.id.presentation_mobile_on )
    private RelativeLayout layoutOnPrez;

    // Layout used during device selection
    @InjectView(R.id.presentation_mobile_off )
    private RelativeLayout layoutOffPrez;

    // Bouton to show next image
    @InjectView(R.id.presentation_mobile_next)
    private Button nextButton;

    // Bouton to show prvious image
    @InjectView(R.id.presentation_mobile_previous)
    private Button previousButton;

    // Note of the presentation
    @InjectView(R.id.presentation_mobile_note)
    private TextView note;

    // Chrono prensetation time
    @InjectView(R.id.presentation_mobile_all_time)
    private Chronometer chronoAllTime;

    // Chrono slide time
    @InjectView(R.id.presentation_mobile_slide_time)
    private Chronometer chronoSlideTime;


    ///////  Other

    // Display manager
    private DisplayManager mDisplayManager;

    // List of all currently visible presentations indexed by display id.
    private final SparseArray<TvPresentation> mActivePresentations = new SparseArray<>();

    // Current index of the presentation
    private int currentIndex = 0;

    // List of saved active device
    private List<String> listActiveDevice;

    // Device selected
    private Display selectedDisplay;

    // folder of the current presentation
    private String directory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore active presentation content
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(CURRENT_INDEX);
            directory = savedInstanceState.getString(INTENT_ARG_DIRECTORY);
            listActiveDevice = savedInstanceState.getStringArrayList(ACTIVE_DEVICE);
        } else {
            directory = getIntent().getStringExtra(INTENT_ARG_DIRECTORY);
            currentIndex = 0;
            listActiveDevice = new ArrayList<>();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //chronoSlideTime.setFormat("H:MM:SS");
        //chronoAllTime.setFormat("H:MM:SS");


        mActivePresentations.clear();
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        boolean hasActivePresentation = false;
        // Restore presentations from before the activity was paused.
        if(!listActiveDevice.isEmpty()) {
            for (Display d : mDisplayManager.getDisplays()) {
                if (listActiveDevice.contains(String.valueOf(d.getDisplayId()))) {
                    final PresentationContents contents = new PresentationContents(directory, currentIndex);
                    if (contents != null) {
                        showPresentation(d, contents);
                        hasActivePresentation = true;
                    }
                }
            }
        }

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
            listActiveDevice.add(String.valueOf(displayId));
            presentation.dismiss();
        }
        mActivePresentations.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save active active presentation content
        outState.putInt(CURRENT_INDEX, currentIndex);
        outState.putString(INTENT_ARG_DIRECTORY, directory);
        outState.putStringArrayList(ACTIVE_DEVICE, (ArrayList<String>) listActiveDevice);
    }

    private void initView() {
        layoutOnPrez.setVisibility(View.GONE);
        layoutOffPrez.setVisibility(View.VISIBLE);
        buttonStart.setEnabled(false);
        buttonStart.setOnClickListener(this);

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
        layoutOnPrez.setVisibility(View.VISIBLE);
        layoutOffPrez.setVisibility(View.GONE);

        File f = new File(Setting.DEFAULT_DIRECTORY_VALUE + "/" + directory + "/note/");
        File[] fileTab = f.listFiles();
        Arrays.sort(fileTab);
        File text = fileTab[currentIndex];

        // Read note
        FileReader fr = null;
        String str = "";
        try {
            fr = new FileReader(text);
            str = "";
            int i = 0;
            while ((i = fr.read()) != -1) {
                str += (char) i;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        notePresentation.setText(str);


        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);



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

    private final DialogInterface.OnDismissListener mOnDismissListener;

    {
        mOnDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TvPresentation presentation = (TvPresentation) dialog;
                if(presentation != null){
                    int displayId = presentation.getDisplay().getDisplayId();
                    Log.d(TAG, "Presentation on display #" + displayId + " was dismissed.");
                    mActivePresentations.delete(displayId);
                }

            }


        };
    }

    @Override
    public void onClick(View v) {
            switch(v.getId()){
                case R.id.presentation_mobile_next:
                    updatePresentation(currentIndex + 1);
                    break;
                case R.id.presentation_mobile_previous:
                    updatePresentation(currentIndex-1);
                    break;
                case R.id.presentation_mobile_start:
                    PresentationContents contents = new PresentationContents(directory, 0);
                    showPresentation(selectedDisplay, contents);
                    updatePresentationNote();
                    chronoAllTime.start();
                    chronoSlideTime.start();
                    break;
            }
    }

    private void updatePresentation(int futurIndex) {
        for(int i=0;i<mActivePresentations.size();i++){
            TvPresentation tv = mActivePresentations.valueAt(i);
            if(tv.showContent(futurIndex)){
                currentIndex = futurIndex;
                chronoSlideTime.setBase(SystemClock.elapsedRealtime());
            };
        }

        updatePresentationNote();
    }
}
