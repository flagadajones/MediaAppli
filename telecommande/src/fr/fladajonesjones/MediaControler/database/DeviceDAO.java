package fr.fladajonesjones.MediaControler.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.fladajonesjones.MediaControler.upnp.UpnpDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class DeviceDAO {
    private SQLiteDatabase maBaseDonnees;
    static public int TYPE_SERVER = 1;
    static public int TYPE_RENDERER = 2;

    public DeviceDAO() {
        maBaseDonnees = MySQLOpenHelper.instance.getBaseDonnees();
    }

    public ArrayList<UpnpDevice> getAllDevice() {
        Cursor c = maBaseDonnees
                .query(MySQLOpenHelper.TABLE_DEVICES, new String[] { MySQLOpenHelper.COLONNE_DEVICE_UDN,
                        MySQLOpenHelper.COLONNE_DEVICE_TYPE }, null, null, null, null, null);
        //
        if (c.getCount() == 0) {

            c.close();
            return new ArrayList<UpnpDevice>(0);
        }
        ArrayList<UpnpDevice> retDevices = new ArrayList<UpnpDevice>(c.getCount());
        c.moveToFirst();
        do {

            UpnpDevice device = null;
            if (c.getInt(1) == TYPE_SERVER)
                device = new UpnpServerDevice();
            else if (c.getInt(1) == TYPE_RENDERER)
                device = new UpnpRendererDevice();

            device.setUdn(c.getString(0));
            retDevices.add(device);
        } while (c.moveToNext());
        // Ferme le curseur pour liberer les ressources.
        c.close();

        return retDevices;
    }

    public long insertDevice(UpnpDevice device) {

        ContentValues valeurs = new ContentValues();
        valeurs.put(MySQLOpenHelper.COLONNE_DEVICE_UDN, device.getUdn());
        int type = 0;
        if (device instanceof UpnpServerDevice)
            type = TYPE_SERVER;
        else if (device instanceof UpnpRendererDevice)
            type = TYPE_RENDERER;
        valeurs.put(MySQLOpenHelper.COLONNE_DEVICE_TYPE, type);
        return maBaseDonnees.insert(MySQLOpenHelper.TABLE_DEVICES, null, valeurs);

    }

    public int removeDevice(String udn) {

        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_DEVICES, MySQLOpenHelper.COLONNE_DEVICE_UDN + " = \"" + udn
                + "\"", null);
    }

}
