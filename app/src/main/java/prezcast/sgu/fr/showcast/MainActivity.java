package prezcast.sgu.fr.showcast;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import prezcast.sgu.fr.showcast.roboguice.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 1st Actity.
 * Choose the presentation to show
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    /**
     * Application directory.
     */
    public static final String APP_SDCARD_DIRECTORY = "/sdcard/showcast";

    private static final String TAG = "ShowCast-Main";

    /**
     * List presentation directory.
     */
    private List<String> listDirectory = new ArrayList<>();

    /**
     * Presentation listing.
     */
    @InjectView(R.id.mainListDirectory)
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create application folder
        initFirstTime();

    }

    @Override
    protected void onResume() {
        super.onResume();

        listDirectory.clear();
        for (File f : new File(APP_SDCARD_DIRECTORY).listFiles()) {
            if (f.isDirectory()) {
                listDirectory.add(f.getName());
            }
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDirectory);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentNext = new Intent(MainActivity.this, PresentationActivity.class);
                intentNext.putExtra(PresentationActivity.INTENT_ARG_DIRECTORY, listDirectory.get(position));
                startActivity(intentNext);
            }
        });

    }

    /**
     * Create the application main directory for presentation
     */
    private void initFirstTime() {
        File f = new File(APP_SDCARD_DIRECTORY);
        if (!f.exists()) {
            f.mkdir();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
