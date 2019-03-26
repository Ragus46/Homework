package com.example.efelek16.fleamarket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SearchView sv;
    String username, password;
    ListView lv;
    static MainActivity mainActivity;
    List<Model> lvarr = new ArrayList<>();
    ArrayAdapter<Model> adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.listview);
        sv = findViewById(R.id.searchview);
        readsettings();
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
                    Toast toast = Toast.makeText(context, "Suche nicht gefunden", Toast.LENGTH_LONG);
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
            Context context = getApplicationContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText articlename = new EditText(context);
            articlename.setHint("ArticleName");
            layout.addView(articlename);
            EditText priceentry = new EditText(context);
            priceentry.setHint("Price");
            layout.addView(priceentry);
            EditText entryusername = new EditText(context);
            entryusername.setHint("Username");
            layout.addView(entryusername);
            EditText mailentry = new EditText(context);
            mailentry.setHint("E-Mail");
            layout.addView(mailentry);
            EditText phoneentry = new EditText(context);
            phoneentry.setHint("Phone");
            layout.addView(phoneentry);


            android.support.v7.app.AlertDialog.Builder ab = new android.support.v7.app.AlertDialog.Builder(this);
            ab.setTitle("PUT");
            ab.setView(layout);
            ab.setNegativeButton("Abrechen", (dialog, which) -> {

            });
            ab.setPositiveButton("OK", (dialog, which) -> {

                try {
                    String link = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php?operation=add&name=" + articlename.getEditableText().toString() + "&price=" + priceentry.getEditableText().toString() + "&email=" + mailentry.getEditableText().toString() + "&phone=" + phoneentry.getEditableText().toString() + "&username=" + username + "&password=" + password;
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
            String LINK = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php?operation=delete&id=" + lvarr.get(info.position).getId() + "&username=" + username + "&password=" + password;
            try {
                execute(LINK);

            } catch (Exception e) {
                e.printStackTrace();
            }
            getall();

        }
        return super.onContextItemSelected(item);
    }


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

            lvarr.add(new Model(id, name, price, username, email, phone));


        }

        adap.notifyDataSetChanged();
    }

    private void execute(String link) throws Exception {
        Servertask servertask = new Servertask();
        servertask.execute("a", link);
    }

    public static MainActivity getInstance() {
        return mainActivity;
    }

    private void readsettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", "");
        password = preferences.getString("password", "");
    }


}

