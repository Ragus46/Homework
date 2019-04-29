package com.example.efelek16.fleamarket;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    SearchView sv;
    String username, password;
    ListView lv;
    static MainActivity mainActivity;
    List<Model> lvarr = new ArrayList<>();
    ArrayAdapter<Model> adap;
    Location actlocation;
    Gson gson = new Gson();
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.listview);
        sv = findViewById(R.id.searchview);
        readsettings();
        registerSystemService();
        checkPermissionGPS();
        adap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lvarr);
        lv.setAdapter(adap);
        mainActivity = this;
        registerForContextMenu(lv);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                List<Model> filtered = new ArrayList<>();
                for (Model c : lvarr) {
                    if (c.toString().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(c);
                    }
                }
                ArrayAdapter<Model> ad = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, filtered);
                lv.setAdapter(ad);
                if (filtered.size() == 0) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Suche nicht gefunden", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void getall() {
        try {
            execute("http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php?operation=get");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_preference) {
            Intent intent = new Intent(this, prefact.class);
            startActivity(intent);
        } else if (i == R.id.menu_getall) {
            getall();

        } else if (i == R.id.menu_put) {
            int type = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;

            Context context = getApplicationContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText articlename = new EditText(context);
            articlename.setHint("ArticleName");
            layout.addView(articlename);

            EditText priceentry = new EditText(context);
            priceentry.setHint("Price");
            layout.addView(priceentry);

            EditText mailentry = new EditText(context);
            mailentry.setHint("E-Mail");
            layout.addView(mailentry);

            EditText phoneentry = new EditText(context);
            phoneentry.setHint("Phone");
            layout.addView(phoneentry);

            EditText latitude = new EditText(context);
            latitude.setHint("Latitude");
            latitude.setInputType(type);
            layout.addView(latitude);

            EditText longtitude = new EditText(context);
            longtitude.setHint("Longtitude");
            longtitude.setInputType(type);
            layout.addView(longtitude);

            Button locationfiller = new Button(context);
            locationfiller.setText("Fill with LocATM");
            locationfiller.setOnClickListener(v->{
                longtitude.setText(actlocation.getLongitude()+"");
                latitude.setText(actlocation.getLatitude()+"");
            });
            layout.addView(locationfiller);

            android.support.v7.app.AlertDialog.Builder ab = new android.support.v7.app.AlertDialog.Builder(this);
            ab.setTitle("PUT");
            ab.setView(layout);
            ab.setNegativeButton("Abrechen", (dialog, which) -> {

            });
            ab.setPositiveButton("OK", (dialog, which) -> {

                try {
                        String link = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php?operation=add&name=" + articlename.getEditableText().toString() + "&price=" + priceentry.getEditableText().toString() + "&email=" + mailentry.getEditableText().toString() +
                                "&phone=" + phoneentry.getEditableText().toString() + "&username=" + username +
                                "&password=" + password + "&latitude=" + latitude.getEditableText().toString()+ "&longitude="+longtitude.getEditableText().toString();
                    System.out.println(link);

                    execute(link);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                getall();
            });
            ab.show();
        }
        return super.onOptionsItemSelected(item);
    }
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@CONTEXT MENU @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.listview) {
            getMenuInflater().inflate(R.menu.contextmenu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_delete) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String LINK = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php?operation=delete&id=" + lvarr.get(info.position).getId() + "&username=" + username + "&password=" + password+"&latitude="+lvarr.get(info.position).getLocation().getLatitude()+"&longitude="+lvarr.get(info.position).getLocation().getLongitude();
            try {
                execute(LINK);

            } catch (Exception e) {
                e.printStackTrace();
            }
            getall();

        } else if(item.getItemId() == R.id.context_detail)
        {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            int distance = Math.round(actlocation.distanceTo(lvarr.get(info.position).getLocation()));

            Intent intent = new Intent(this, detailact.class);

            intent.putExtra("articlename",lvarr.get(info.position).getName());
            intent.putExtra("price",lvarr.get(info.position).getPrice());
            intent.putExtra("username",lvarr.get(info.position).getUsername());
            intent.putExtra("email",lvarr.get(info.position).getEmail());
            intent.putExtra("phone",lvarr.get(info.position).getPhone());
            intent.putExtra("lat",lvarr.get(info.position).getLocation().getLatitude());
            intent.putExtra("lon",lvarr.get(info.position).getLocation().getLongitude());
            intent.putExtra("distance",distance);

            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }



//+++++++++++++++++++++++++++++FILL LIST WITCH READ ENTRIES+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void filllist(JSONObject jsn) throws JSONException {
        JSONArray jsonArray = jsn.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject j = jsonArray.getJSONObject(i);
            String id = j.getString("id");
            String name = j.getString("name");
            String username = j.getString("username");
            String email = j.getString("email");
            String phone = j.getString("phone");
            String price = j.getString("price");
            double lat = Double.parseDouble(j.getString("lat"));
            double lon = Double.parseDouble(j.getString("lon"));
            Location l = new Location(LocationManager.GPS_PROVIDER);
            l.setLatitude(lat);
            l.setLongitude(lon);
            lvarr.add(new Model(id, name, price, username, email, phone,l));


        }

        adap.notifyDataSetChanged();
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@ EXECUTE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void execute(String link) throws Exception {
        Servertask servertask = new Servertask();
        servertask.execute("a", link);
    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ GET MAIN ACTIVITY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static MainActivity getInstance() {
        return mainActivity;
    }


    private void readsettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", "");
        password = preferences.getString("password", "");
    }

    //--------------------------GPS------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {
        actlocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //++++++++++++++++++++++++++++++++PERMISSION FOR GPS+++++++++++++++++++++++++++++++++++++++++++++
    private static final int RQ_ACCESS_FINE_LOCATION = 123;
    private boolean isGpsAllowed = false;

    private void registerSystemService() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void checkPermissionGPS() {
        Log.d("Permission", "checkPermissionGPS");
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    RQ_ACCESS_FINE_LOCATION);
        } else {
            gpsGranted();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }


    private void gpsGranted() {
        Log.d("gpsgranted", "gps permission granted!");
        isGpsAllowed = true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RQ_ACCESS_FINE_LOCATION) {
            //LOCATION+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode != RQ_ACCESS_FINE_LOCATION) return;
            if (grantResults.length > 0 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissiondenied", Toast.LENGTH_LONG).show();
            } else {
                gpsGranted();
            }

        } else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    //---------------------------TOAST-----------------------------
    public void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}

