package com.example.efelek16.shoppinglist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Spinner sp;
    List<geschaeft> spinnerarr = new ArrayList<>();
    List<Model> lvarr = new ArrayList<>();
    ArrayAdapter<Model> lvadap;
    ArrayAdapter<geschaeft> spadap;
    ArrayAdapter<Model> adap;
    List<Model> filtered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv);
        sp = findViewById(R.id.spinner);
        spinnerarr.add(new geschaeft("---"));
        registerForContextMenu(listView);
        spadap = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,spinnerarr);
        lvadap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,lvarr);
        adap = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_list_item_1,filtered);
        sp.setAdapter(spadap);
        listView.setAdapter(lvadap);
        //--------------------------------Sorting for the Selected item in the Spinner-----------------------------------
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(sp.getSelectedItem().toString().equals("---")){
                    listView.setAdapter(lvadap);
                    lvadap.notifyDataSetChanged();
                }
                else{
                    Set<Model> s = new HashSet<>();
                    for (int j = 0; j< lvarr.size();j++)
                    {
                        if(lvarr.get(j).getGeschaeft().equals(sp.getSelectedItem().toString()))
                        {
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



    }

    //--------------------------------OptionsMenu-------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //-----------------------------------When options item selected---------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        //add new Item to Spinner-------------------------------------------------------------------
        if(i == R.id.geschaeft)
        {
            Context context =  getApplicationContext();
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
                try{
                    if(geschaeft.getEditableText().toString().length()>0)
                    {
                        spinnerarr.add(new geschaeft(geschaeft.getEditableText().toString()));

                        Set<geschaeft> s = new HashSet<>();
                        s.addAll(spinnerarr);
                        spinnerarr.clear();
                        spinnerarr.addAll(s);
                        spadap.notifyDataSetChanged();
                    }
                }catch (Exception e) {}
            });
            ab.show();
        }
        //add new item to listview acording to spinner item------------------------------------------------------
        else if(i == R.id.artikel)
        {

            Context context =  getApplicationContext();
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
                try{
                    if(artikel.getEditableText().toString().length()>0&&count.getEditableText().toString().length()>0)
                    {

                        lvarr.add(new Model(sp.getSelectedItem().toString(),artikel.getEditableText().toString(),count.getEditableText().toString()));
                        lvadap.notifyDataSetChanged();
                        adap.notifyDataSetChanged();

                    }
                }catch (Exception e) {}
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
        }
        else if (item.getItemId() == R.id.context_change)
        {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            Context context =  getApplicationContext();
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
                    lvarr.set(info.position, new Model(shop.getEditableText().toString(),artikel.getEditableText().toString(),count.getEditableText().toString()));
                    for (int i = 0; i < spinnerarr.size();i++)
                    {
                        if(spinnerarr.get(i).toString().equals(shop.getEditableText().toString()))
                        {
                            contains = true;
                        }
                    }
                    if(!contains)
                    {
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
}
