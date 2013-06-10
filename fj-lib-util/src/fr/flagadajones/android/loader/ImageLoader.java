package fr.flagadajones.android.loader;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import fr.flagadajones.media.util.StreamUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private Map<TextView, String> textViews = Collections.synchronizedMap(new WeakHashMap<TextView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread

    public ImageLoader(Context context, int stub_id) {
        fileCache = new FileCache(context);
        this.stub_id = stub_id;
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id;

    /*	    public void DisplayImage(String url, ImageView imageView)
            {
                imageViews.put(imageView, url);
                Bitmap bitmap=memoryCache.get(url);
                if(bitmap!=null)
                    imageView.setImageBitmap(bitmap);
                else
                {
                    queuePhoto(url, imageView);
                    imageView.setImageResource(stub_id);
                }
            }
            public void DisplayImage(String url, TextView textView)
            {
                textViews.put(textView, url);
                Bitmap bitmap=memoryCache.get(url);
                if(bitmap!=null){
                    Drawable draw=new BitmapDrawable(textView.getResources(),bitmap);
                            draw.setBounds(0, 0, 80, 80);
                    textView.setCompoundDrawables(null, draw, null, null);
                }else
                {
                    queuePhoto(url, textView);
                    Drawable draw=textView.getResources().getDrawable(stub_id );
                    draw.setBounds(0, 0, 80, 80);
                    textView.setCompoundDrawables(null,draw,null,null);
                }
            }
         */
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private void queuePhoto(String url, TextView textView) {
        PhotoToLoadTextView p = new PhotoToLoadTextView(url, textView);
        executorService.submit(new PhotosLoaderTextView(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        //from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            StreamUtils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    //Task for the queue
    private class PhotoToLoadTextView {
        public String url;
        public TextView textView;

        public PhotoToLoadTextView(String u, TextView i) {
            url = u;
            textView = i;
        }
    }

    class PhotosLoaderTextView implements Runnable {
        PhotoToLoadTextView photoToLoad;

        PhotosLoaderTextView(PhotoToLoadTextView photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayerTextView bd = new BitmapDisplayerTextView(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoadTextView photoToLoadTextView) {
        String tag = textViews.get(photoToLoadTextView.textView);
        if (tag == null || !tag.equals(photoToLoadTextView.url))
            return true;
        return false;
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayerTextView implements Runnable {
        Bitmap bitmap;
        PhotoToLoadTextView photoToLoad;

        public BitmapDisplayerTextView(Bitmap b, PhotoToLoadTextView p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.textView.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(photoToLoad.textView.getResources(), bitmap), null, null);
            else
                photoToLoad.textView.setCompoundDrawablesWithIntrinsicBounds(null, photoToLoad.textView.getResources().getDrawable(stub_id), null, null);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}

