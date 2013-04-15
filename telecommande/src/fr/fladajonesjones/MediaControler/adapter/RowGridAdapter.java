package fr.fladajonesjones.MediaControler.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.DialogRendererSelector;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.events.UpnpServerLoadingPisteOkEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.model.Row;
import fr.fladajonesjones.MediaControler.model.Row.RowArtiste;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Album;
import fr.flagadajones.media.util.BusManager;

public class RowGridAdapter extends BaseAdapter implements SectionIndexer {

    Activity activity;
    List<Row> myElements = new ArrayList<Row>();

    HashMap<String, Integer> alphaIndexer;

    String[] sections;

   static Album album;
    static UpnpRendererDevice selectedDevice;
    
    static Object eventSub = new Object(){
    @Subscribe public void onUpnpServerLoadingPisteOk(UpnpServerLoadingPisteOkEvent event){
            BusManager.getInstance().unregister(this);
            
            selectedDevice.playMusique(album);
        }
    };
    
    

    
    private View.OnClickListener myOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View layout) {
            AlbumHolder holder = (AlbumHolder) layout.getTag();
            album = holder.album;
            // dans le cas d'un clique sur une radio, on affiche la fenetre de selection du renderer si on a plus
            // d'un renderer de disponible
            //createDialogSelectionDevice(radio, (ImageView) v.findViewById(R.id.radioIcone));
            if (UpnpDeviceManager.getInstance().rendererDevice.size() == 0) {
                return;
            } else if (UpnpDeviceManager.getInstance().getDefaultRenderer() != null) {
            	selectedDevice=UpnpDeviceManager.getInstance().getDefaultRenderer();
            	playMusique(selectedDevice,album);
            	
            } else if (UpnpDeviceManager.getInstance().rendererDevice.size() == 1) {
            	selectedDevice = UpnpDeviceManager.getInstance().rendererDevice.get(0);
                playMusique(selectedDevice,album);
            } else {
                DialogRendererSelector.createDialogSelectionDevice(activity,album, holder.albumName.getCompoundDrawables()[1]);
            }

        }
    };
    
    private static void playMusique(UpnpRendererDevice selectedDevice,Album album){
        
        if (album.isPisteLoaded()) {
            selectedDevice.playMusique(album);
        } else {
            BusManager.getInstance().register(eventSub);

           UpnpDeviceManager.getInstance().libraryDevice.loadPiste(album.upnpId);
        }
        
    }
    
  
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation performed on the array should be
     * synchronized on this lock. This lock is also used by the filter (see {@link #getFilter()} to make a synchronized
     * copy of the original array of data.
     */
    private final Object mLock = new Object();

    // Constructor
    public RowGridAdapter(Activity context) {
        super();
        this.activity = context;
    

    }

    void initSection() {
        // here is the tricky stuff
        alphaIndexer = new HashMap<String, Integer>();
        // in this hashmap we will store here the positions for
        // the sections

        int size = myElements.size();
        // Collections.sort(myElements);
        for (int i = size - 1; i >= 0; i--) {
            Row element = myElements.get(i);
            for (RowArtiste artiste : element.lstArtiste) {
                alphaIndexer.put(artiste.artiste.getNom().substring(0, 1), i);
            }
        }

        Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
        // cannot be sorted...

        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>(); // list can be
        // sorted

        while (it.hasNext()) {
            String key = it.next();
            keyList.add(key);
        }

        Collections.sort(keyList);

        sections = new String[keyList.size()]; // simple conversion to an
        // array of object
        keyList.toArray(sections);
    }

    public void clear() {
        synchronized (mLock) {
            myElements.clear();
        }
    }

    public void addAll(List<Row> items) {
        synchronized (mLock) {
            myElements.addAll(items);
        }
        initSection();

    }

    public void add(Row items) {
        synchronized (mLock) {
            myElements.add(items);
        }
        initSection();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = (View) convertView;
        final RowHolder holder;
        if (layout == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(R.layout.music_row_artiste_grid_view, parent, false);
            holder = new RowHolder();
            holder.artisteNames = new TextView[5];
            holder.artisteNames[0] = (TextView) layout.findViewById(R.id.artisteName1);
            holder.artisteNames[1] = (TextView) layout.findViewById(R.id.artisteName2);
            holder.artisteNames[2] = (TextView) layout.findViewById(R.id.artisteName3);
            holder.artisteNames[3] = (TextView) layout.findViewById(R.id.artisteName4);
            holder.artisteNames[4] = (TextView) layout.findViewById(R.id.artisteName5);

            holder.albums = new AlbumHolder[5];
            for (int i = 0; i < 5; i++) {
                holder.albums[i] = new AlbumHolder();
            }
            holder.albums[0].albumName = (TextView) layout.findViewById(R.id.albumName1);
            holder.albums[1].albumName = (TextView) layout.findViewById(R.id.albumName2);
            holder.albums[2].albumName = (TextView) layout.findViewById(R.id.albumName3);
            holder.albums[3].albumName = (TextView) layout.findViewById(R.id.albumName4);
            holder.albums[4].albumName = (TextView) layout.findViewById(R.id.albumName5);

            for (int i = 0; i < 5; i++) {
                holder.artisteNames[i].setSelected(true);

                AlbumHolder holder2 = holder.albums[i];
                holder2.albumName.setTag(holder2);
                //holder2.albumIcone = (ImageView) holder2.layout.findViewById(R.id.albumIcone);
                //holder2.albumName = (TextView) holder2.layout.findViewById(R.id.albumName);
                holder2.albumName.setSelected(true);

                holder2.albumName.setOnClickListener(myOnClickListener);
            }
            layout.setTag(holder);
        } else {
            holder = (RowHolder) layout.getTag();
        }

        // holder.albumGrid.setAdapter(new AlbumGridAdapter(activity));
        final Row item = myElements.get(position);

        int num = 0;
        for (RowArtiste iterable_element : item.lstArtiste) {
            TextView t = holder.artisteNames[num];
            t.setText(iterable_element.artiste.getNom());
            t.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, iterable_element.nbAlbum));
            t.setBackgroundColor(iterable_element.color);
            t.setVisibility(View.VISIBLE);
            num++;
        }
        for (int i = num; i < 5; i++) {
            TextView t = holder.artisteNames[i];
            t.setVisibility(View.GONE);
        }

        int numAlbum = 0;
        for (Album iterable_element : item.lstAlbum) {
            AlbumHolder holderAlbum = holder.albums[numAlbum];
            holderAlbum.album = iterable_element;
            holderAlbum.albumName.setText(iterable_element.titre);
            holderAlbum.albumName.setVisibility(View.VISIBLE);
//            ImageTag tag = Application.imageTagFactory.build(iterable_element.icone);
//            holderAlbum.albumIcone.setTag(tag);
//            Application.thumbnailImageLoader.getLoader().load(holderAlbum.albumIcone);
           
            //Application.imageLoader.DisplayImage(iterable_element.icone,  holderAlbum.albumIcone);
            Application.imageLoader.DisplayImage(iterable_element.albumArt,  holderAlbum.albumName);
            
            numAlbum++;
        }
        for (int i = numAlbum; i < 5; i++) {
        	AlbumHolder holderAlbum = holder.albums[i];
        	TextView t = holderAlbum.albumName;
            t.setVisibility(View.INVISIBLE);
        }
        if (numAlbum<5){
        	TextView t = holder.artisteNames[num];
            t.setText("");
            t.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 5-num));
            t.setBackgroundColor(0);
            t.setVisibility(View.VISIBLE);
        }
        
        // albumGrid.setOnItemClickListener(new OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> parent, View v,
        // int position, long id) {
        // // dans le cas d'un clique sur une radio, on affiche la fenetre
        // // de selection du renderer si on a plus
        // // d'un renderer de disponible
        // final Album album = (Album) gridAda.getItem(position);
        // // createDialogSelectionDevice(radio, (ImageView)
        // // v.findViewById(R.id.radioIcone));
        // if (Application.rendererDevice.size() == 0) {
        // return;
        // } else if (Application.getDefaultRenderer() != null) {
        // Application.getDefaultRenderer().playMusique(album);
        // } else if (Application.rendererDevice.size() == 1) {
        // UpnpRendererDevice renderer = Application.rendererDevice
        // .get(0);
        // renderer.playMusique(album);
        // } else {
        // Application.createDialogSelectionDevice(activity,album, (ImageView)
        // v.findViewById(R.id.albumIcone));
        // }
        // }
        // });

        return layout;
    }

    @Override
    public int getPositionForSection(int section) {
        // Log.v("getPositionForSection", ""+section);
        String letter = sections[section];

        return alphaIndexer.get(letter);
    }

    @Override
    public int getSectionForPosition(int position) {

        // you will notice it will be never called (right?)
        // Log.v("getSectionForPosition", "called");
        return 0;
    }

    @Override
    public Object[] getSections() {

        return sections; // to string will be called each object, to display
        // the letter
    }

    static class RowHolder {

        // TextView[] albumNames;
        // ImageView[] albumIcones;
        TextView[] artisteNames;
        AlbumHolder[] albums;

        // TextView albumArtisteName;
    }

    static class AlbumHolder {
        //LinearLayout layout;
        Album album;
        TextView albumName;
        //ImageView albumIcone;
        // TextView albumArtisteName;
    }

    @Override
    public int getCount() {
        return myElements.size();
    }

    @Override
    public Row getItem(int position) {

        return myElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
