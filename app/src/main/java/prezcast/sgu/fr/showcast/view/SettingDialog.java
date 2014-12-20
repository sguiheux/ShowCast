package prezcast.sgu.fr.showcast.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import prezcast.sgu.fr.showcast.R;
import prezcast.sgu.fr.showcast.db.setting.Setting;

/**
 * Dialog to edit a setting
 */
public class SettingDialog extends Dialog{

    /**
     * COnstructor
     * @param ctx Screen context
     * @param lst Lisener for action
     * @param s setting to edit
      */
    public SettingDialog(Context ctx, final ReadyListener lst, Setting s,final int position) {
        super(ctx);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_setting);

        LinearLayout ll = (LinearLayout) findViewById(R.id.dialog_layout_setting);

        // Setting name
        String settingsName = ctx.getString(ctx.getResources().getIdentifier(s.getName(), "string", ctx.getPackageName()));
        ((TextView) findViewById(R.id.dialog_setting_key)).setText(settingsName);

        // Setting value
        View v = null;
        switch (s.getType()) {
            case STRING:
                v = new EditText(ctx);
                ((EditText) v).setText(s.getValue());
                ((EditText) v).setSingleLine();
                break;
            case INT:
                v = new NumberPicker(ctx);
                ((NumberPicker) v).setValue(Integer.valueOf(s.getValue()));
                break;
            case BOOLEAN:
                v = new CheckBox(ctx);
                ((CheckBox) v).setText(s.getValue());
                ((CheckBox) v).setChecked(s.getValue().equals("true"));
                break;
        }
        ll.addView(v);

        final View finalV = v;
        findViewById(R.id.dialog_setting_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valeur = "";
                if (finalV instanceof EditText) {
                    valeur = ((EditText) finalV).getText().toString();
                }
                if (finalV instanceof NumberPicker) {
                    valeur = String.valueOf(((NumberPicker) finalV).getValue());
                }
                if (finalV instanceof CheckBox) {
                    valeur = String.valueOf(((CheckBox)finalV).isChecked());
                }
                lst.editValue(position, valeur);
                dismiss();
            }
        });

        findViewById(R.id.dialog_setting_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    /**
     * Interface for sending reult
     */
    public interface ReadyListener {

        /**
         * Edit value modification
         * @param position index of the setting
         * @param value New Value
         */
        void editValue(int position, String value);
    }
}
