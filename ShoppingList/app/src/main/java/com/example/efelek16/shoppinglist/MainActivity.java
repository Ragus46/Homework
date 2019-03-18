package com.example.efelek16.shoppinglist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Spinner sp;
    List<geschaeft> spinnerarr = new ArrayList<>();
    List<Model> lvarr = new ArrayList<>();
    ArrayAdapter<Model> lvadap;
    ArrayAdapter<geschaeft> spadap;
    ArrayAdapter<Model> adap;
    List<Model> filtered = new ArrayList<>();
    Button saving;
    String filename = "shoppinglist.json";
    File writefile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv);
        sp = findViewById(R.id.spinner);
        saving = findViewById(R.id.save);
        spinnerarr.add(new geschaeft("---"));
        registerForContextMenu(listView);
        spadap = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerarr);
        lvadap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lvarr);
        adap = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, filtered);
        sp.setAdapter(spadap);
        listView.setAdapter(lvadap);
        if (writefile.exists()) {
            if (!checkPermission()) {
                reader();
            } else {
                if (checkPermission()) {
                    requestPermissionAndContinue();
                } else {
                    reader();
                }
            }
        }

        //--------------------------------Sorting for the Selected item in the Spinner-----------------------------------
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp.getSelectedItem().toString().equals("---")) {
                    listView.setAdapter(lvadap);
                    lvadap.notifyDataSetChanged();
                } else {
                    Set<Model> s = new HashSet<>();
                    for (int j = 0; j < lvarr.size(); j++) {
                        if (lvarr.get(j).getGeschaeft().equals(sp.getSelectedItem().toString())) {
                            s.add(new Model(lvarr.get(j).getGeschaeft(), lvarr.get(j).getEintrag(), lvarr.get(j).getStueck()));
                        }
                    }
                    filtered.clear();
                    filtered.addAll(s);
                    listView.setAdapter(adap);
                    adap.notifyDataSetChanged();

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                listView.setAdapter(lvadap);
            }
        });
        //------------------------------SAVING-------------------------------
        saving.setOnClickListener(v -> {
            if (!checkPermission()) {
                write();
            } else {
                if (checkPermission()) {
                    requestPermissionAndContinue();
                } else {
                    write();
                }
            }

        });

    }

    //---------------------------CHECK IF EXTERNAL STORAGE IS READ - & WRITEABLE--------------------------------------------
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    //--------------------------------OptionsMenu-------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //-----------------------------------When options item selected---------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        //add new Item to Spinner-------------------------------------------------------------------
        if (i == R.id.geschaeft) {
            Context context = getApplicationContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText geschaeft = new EditText(context);
            geschaeft.setHint("Neuer Artikel");
            layout.addView(geschaeft); // Another add method

            android.support.v7.app.AlertDialog.Builder ab = new android.support.v7.app.AlertDialog.Builder(this);
            ab.setTitle("Neues Geschäft Hinzufügen");
            ab.setView(layout);
            ab.setNegativeButton("Abrechen", (dialog, which) -> {

            });
            ab.setPositiveButton("Speichern", (dialog, which) -> {
                try {
                    if (geschaeft.getEditableText().toString().length() > 0) {
                        spinnerarr.add(new geschaeft(geschaeft.getEditableText().toString()));

                        Set<geschaeft> s = new HashSet<>();
                        s.addAll(spinnerarr);
                        spinnerarr.clear();
                        spinnerarr.addAll(s);
                        spadap.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                }
            });
            ab.show();
        }
        //add new item to listview acording to spinner item------------------------------------------------------
        else if (i == R.id.artikel) {

            Context context = getApplicationContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText artikel = new EditText(context);
            artikel.setHint("Neuer Artikel");
            layout.addView(artikel); // Another add method

            EditText count = new EditText(context);
            count.setInputType(InputType.TYPE_CLASS_NUMBER);
            count.setHint("Wie viel?");
            layout.addView(count);

            android.support.v7.app.AlertDialog.Builder ab = new android.support.v7.app.AlertDialog.Builder(this);
            ab.setTitle("Neuen Artikel Hinzufügen");
            ab.setView(layout);
            ab.setNegativeButton("Abrechen", (dialog, which) -> {

            });
            ab.setPositiveButton("Speichern", (dialog, which) -> {
                try {
                    if (artikel.getEditableText().toString().length() > 0 && count.getEditableText().toString().length() > 0) {

                        lvarr.add(new Model(sp.getSelectedItem().toString(), artikel.getEditableText().toString(), count.getEditableText().toString()));
                        lvadap.notifyDataSetChanged();
                        adap.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                }
            });
            ab.show();


        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------untested Contextmenu------------------------------------------------------
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.lv) {
            getMenuInflater().inflate(R.menu.contextmenu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //-----------------------------if context menue selected------------------------------------------
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //remove item from listview-------------------------------------
        if (item.getItemId() == R.id.context_delete) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            lvarr.remove(info.position);
            lvadap.notifyDataSetChanged();
            adap.notifyDataSetChanged();


            return true;
        } else if (item.getItemId() == R.id.context_change) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            Context context = getApplicationContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText shop = new EditText(context);
            shop.setText(lvarr.get(info.position).getGeschaeft());
            layout.addView(shop);

            EditText artikel = new EditText(context);
            artikel.setText(lvarr.get(info.position).eintrag);
            layout.addView(artikel); // Another add method

            EditText count = new EditText(context);
            count.setInputType(InputType.TYPE_CLASS_NUMBER);
            count.setText(lvarr.get(info.position).getStueck());
            layout.addView(count);

            android.support.v7.app.AlertDialog.Builder ab = new android.support.v7.app.AlertDialog.Builder(this);
            ab.setTitle("Bearbiten");
            ab.setView(layout);
            ab.setNegativeButton("CANCEL", (dialog, which) -> {

            });
            ab.setPositiveButton("Change", (dialog, which) -> {
                try {
                    boolean contains = false;
                    lvarr.set(info.position, new Model(shop.getEditableText().toString(), artikel.getEditableText().toString(), count.getEditableText().toString()));
                    for (int i = 0; i < spinnerarr.size(); i++) {
                        if (spinnerarr.get(i).toString().equals(shop.getEditableText().toString())) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        spinnerarr.add(new geschaeft(shop.getEditableText().toString()));
                        spadap.notifyDataSetChanged();
                    }

                    adap.notifyDataSetChanged();
                    lvadap.notifyDataSetChanged();
                } catch (Exception e) {
                }
            });
            ab.show();
        }
        return super.onContextItemSelected(item);
    }


    //-----------------------PERMISSION for OS MARSHMALLOW OR ABOVE NEEDED-.--------------------------------------------
    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            writeorread();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {

                    writeorread();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Writer & reader---------------------------------------------
    public void write() {
        if (lvarr.size() > 0 && isExternalStorageWritable()) {

            try {
                Writer writer = new BufferedWriter(new FileWriter(writefile));
                Gson gson = new Gson();
                for (int i = 0; i< lvarr.size(); i++)
                {
                    String json = gson.toJson(lvarr.get(i));
                    writer.write(json);
                    writer.flush();
                }
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void writeorread(){
        if (lvarr.size()>0)
        {
            write();
        }
        else {
            reader();
        }
    }

    public void reader() {
        try {
           FileInputStream fis = new FileInputStream(writefile);
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                Model json = gson.fromJson(line, Model.class);
                spinnerarr.add(new geschaeft(json.getGeschaeft()));
                lvarr.add(json);
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }
}
