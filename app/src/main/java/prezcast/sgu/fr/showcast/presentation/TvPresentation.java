package prezcast.sgu.fr.showcast.presentation;

import android.app.Presentation;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.util.Arrays;

import prezcast.sgu.fr.showcast.R;
import prezcast.sgu.fr.showcast.activity.MainActivity;
import prezcast.sgu.fr.showcast.activity.PresentationActivity;
import prezcast.sgu.fr.showcast.db.setting.Setting;
import prezcast.sgu.fr.showcast.presentation.PresentationContents;

/**
 * Second screen.
 */
public class TvPresentation extends Presentation implements ViewSwitcher.ViewFactory {

    /** Content to show.*/
    public PresentationContents contents;

    /** Container of the ImageView. */
    private ImageSwitcher imageSwitcher;

    /**
     * Tag for log.
     */
    private static final String TAG = "ShowCast-TvPresentation";



    /**
     * Constructor.
     * @param presentationActivity Parent activity
     * @param display Screen
     * @param contents Content to show
     */
    public TvPresentation(PresentationActivity presentationActivity, Display display, PresentationContents contents) {
        super(presentationActivity, display);
        this.contents = contents;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_tv);

        imageSwitcher = (ImageSwitcher) findViewById(R.id.presentation_tv_switch);
        imageSwitcher.setFactory(this);


        showContent(contents.index);
    }

    /**
     * Load Image if index is good.
     * @param index Index of the image
     * @return
     */
    private boolean updateContent(int index) {
        File f = new File(Setting.DEFAULT_DIRECTORY_VALUE+"/"+contents.prez+"/img/");
        File[] fileTab = f.listFiles();
        Arrays.sort(fileTab);

        if(index>=0 && index<fileTab.length){
            File image = fileTab[index];
            Uri imageUri = Uri.fromFile(image);
            imageSwitcher.setImageURI(imageUri);
            return true;
        }
        return false;
    }


    @Override
    public View makeView() {
        ImageView iView = new ImageView(getContext());
        iView.setLayoutParams(new ImageSwitcher.LayoutParams
                (ImageSwitcher.LayoutParams.MATCH_PARENT,ImageSwitcher.LayoutParams.MATCH_PARENT));
        iView.setBackgroundColor(0xFF000000);
        return iView;
    }

    /**
     * Show Image
     * @param currentIndex Index of the image
     * @return
     */
    public boolean showContent(int currentIndex) {
        if(updateContent(currentIndex)){
            contents.index = currentIndex;
            Log.d(TAG,"Change Index:"+currentIndex);
            return true;
        }
        return false;
    }
}
