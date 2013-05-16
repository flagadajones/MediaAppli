package fr.flagadajones.media.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils {


    public static Drawable resize(Drawable image) {

        Bitmap d = ((BitmapDrawable) image).getBitmap();
        
        Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, 32, 32, false);
        return new BitmapDrawable(bitmapOrig);
    }
}
