package prezcast.sgu.fr.showcast.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Liste adapter for object Display.
 */
public class DisplayAdapter extends ArrayAdapter {

    /** List of displays. */
    public List<Display> listDisplay;

    /** Screen context. */
    public Context ctx;

    /**
     * Constructor
     * @param ctx Screen context
     * @param displays List of available displays
     */
    public DisplayAdapter(Context ctx,int ressourceId,List<Display> displays){
        super(ctx, ressourceId, displays);
        this.listDisplay = displays;
        this.ctx = ctx;

    }
}
