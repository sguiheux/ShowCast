package prezcast.sgu.fr.showcast;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Presentation contents.
 */
public class PresentationContents implements Parcelable{

    public String prez;
    public int index;

    public static final Creator<PresentationContents> CREATOR =
            new Creator<PresentationContents>() {
                @Override
                public PresentationContents createFromParcel(Parcel in) {
                    return new PresentationContents(in);
                }

                @Override
                public PresentationContents[] newArray(int size) {
                    return new PresentationContents[size];
                }
            };

    public PresentationContents(String prez,int index) {
        this.index = index;
        this.prez = prez;
    }

    private PresentationContents(Parcel in) {
        index = in.readInt();
        prez = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(prez);
    }
}
