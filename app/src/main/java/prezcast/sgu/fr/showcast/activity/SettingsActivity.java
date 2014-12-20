package prezcast.sgu.fr.showcast.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import prezcast.sgu.fr.showcast.R;
import prezcast.sgu.fr.showcast.async.SettingsAsync;
import prezcast.sgu.fr.showcast.db.setting.EnumSettingsZone;
import prezcast.sgu.fr.showcast.db.setting.Setting;
import prezcast.sgu.fr.showcast.roboguice.RoboActionBarActivity;
import prezcast.sgu.fr.showcast.view.SeparatedListAdapter;
import prezcast.sgu.fr.showcast.view.SettingDialog;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Activity for editing settings
 */
@ContentView(R.layout.activity_settings)
public class SettingsActivity extends RoboActionBarActivity{

    // List of seetings
    @InjectView(R.id.settings_list)
    private ListView listSettings;

    // Settings lists
    private List<Setting> settings;

    @Override
    protected void onResume() {
        super.onResume();

        // Load settings from db
        SettingsAsync asyncTask = new SettingsAsync(this, SettingsAsync.SettingsAsyncAction.SELECT_ALL);
        asyncTask.execute();
    }

    public void updateSettings(Map<EnumSettingsZone, List<Setting>> mapSettingsByZone){
        if(mapSettingsByZone != null) {
            SeparatedListAdapter adapter = new SeparatedListAdapter(this);
            settings = new ArrayList<>();

            for (Map.Entry<EnumSettingsZone, List<Setting>> entry : mapSettingsByZone.entrySet()) {
                List<Map<String, String>> zone = new LinkedList<>();
                settings.add(null);
                for (Setting s : entry.getValue()) {
                    String name = getString(getResources().getIdentifier(s.getName(), "string", getPackageName()));
                    String value = s.getValue();

                    Map<String, String> item = new HashMap<String, String>();
                    item.put("key", name);
                    item.put("value", value);
                    settings.add(s);
                    zone.add(item);
                }

                String zoneTitre = getString(getResources().getIdentifier(entry.getKey().name, "string", getPackageName()));
                adapter.addZone(zoneTitre, new SimpleAdapter(this, zone, R.layout.list_zone_line,
                        new String[]{"key", "value"}, new int[]{R.id.list_zone_key, R.id.list_zone_value}));

            }

            listSettings.setAdapter(adapter);
            listSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    editSettings(settings.get(i), i);
                }
            });
        }
    }

    private void editSettings(Setting setting,int position) {
        SettingDialog dialog = new SettingDialog(this,new ReadyLister(),setting,position);
        dialog.show();
    }

    public void showResult(Boolean aBoolean) {
        Toast.makeText(this,String.valueOf(aBoolean),Toast.LENGTH_SHORT).show();
    }

    public class ReadyLister implements SettingDialog.ReadyListener{

        @Override
        public void editValue(int position, String value) {
            Setting s = settings.get(position);
            s.setValue(value);
            SettingsAsync asyncTask = new SettingsAsync(SettingsActivity.this, SettingsAsync.SettingsAsyncAction.UPDATE);
            asyncTask.execute(s);
        }
    }
}
