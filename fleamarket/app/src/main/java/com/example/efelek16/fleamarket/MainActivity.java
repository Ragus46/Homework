package com.example.efelek16.fleamarket;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    List<Model> lvarr = new ArrayList<>();
    ArrayAdapter<Model> adap;
    ServerTask serverTask = new ServerTask();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.listview);
        adap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,lvarr);
        registerForContextMenu(lv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenue,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if(i ==R.id.menu_getall)
        {

        }
        else if (i==R.id.menu_getforUser)
        {
            Context context = getApplicationContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText entryusername = new EditText(context);
            entryusername.setHint("Username");
            layout.addView(entryusername);

            EditText entryop = new EditText(context);
            entryop.setHint("Operation");
            layout.addView(entryop);

            android.support.v7.app.AlertDialog.Builder ab = new android.support.v7.app.AlertDialog.Builder(this);
            ab.setTitle("Load for User");
            ab.setView(layout);
            ab.setNegativeButton("Abrechen", (dialog, which) -> {

            });
            ab.setPositiveButton("Laden", (dialog, which) -> {
                    serverTask.execute(entryop.getEditableText().toString(),entryusername.getEditableText().toString());

            });
            ab.show();
        }
        else if (i==R.id.menu_put)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if(viewId==R.id.listview){
            getMenuInflater().inflate(R.menu.contextmenu,menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }



















    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++SERVERTASK+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public class ServerTask extends AsyncTask<String, Integer, String> {
        String errormsg = "ERROR";
        Gson gson = new Gson();
        List<Model> arr = new ArrayList<>();
        @Override
        protected void onPostExecute(String s) {
            System.out.print(s);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... types) {
            String line = "";
            String operation = types[0];
            String username = types[1];
            String url = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
            if(username.length() > 0)
            {
                if(operation.toLowerCase().equals("get"))
                {
                    return getconn(operation,username);

                }
                else if(operation.toLowerCase().equals("post"))
                {
                    return postconn(operation,username);

                }
                else if(operation.toLowerCase().equals("put"))
                {
                    putconn(operation,username);
                    return line;
                }
                else if(operation.toLowerCase().equals("delete"))
                {
                    deleteconn(operation,username);
                    return line;
                }

            }
            else {

                try {
                    HttpURLConnection connection =
                            (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        try {

                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            while ((line = bufferedReader.readLine()) != null) {
                                Model json = gson.fromJson(line, Model.class);
                                arr.add(json);
                            }
                            bufferedReader.close();
                            return line;


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch(IOException i) {
                            i.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return line;
        }


        //-------------------------------GET POST PUT DELETE WITH USERNAME!------------------------


        public String getconn(String op, String username)
        {
            String line = "";
            String url = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
            String finishedurl = url+"?"+"operation="+op+"&"+"username="+username;

            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(finishedurl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String finishedline = "";
                    try {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null) {
                            finishedline +=line+"";


                        }
                        Model json = gson.fromJson(finishedline,Model.class);
                        bufferedReader.close();
                        return json.toString();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch(IOException i) {
                        i.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return errormsg;

        }

        public String postconn(String op, String username)
        {

            String line = "";
            String url = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
            String finishedurl = url+"?"+"operation="+op+"&"+"username="+username;

            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(finishedurl).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                String finishedline = "";
                    try {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null) {
                            finishedline += line+"";
                        }
                        Model json = gson.fromJson(finishedline, Model.class);
                        bufferedReader.close();
                        return json.toString();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch(IOException i) {
                        i.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return errormsg;
        }

        public void putconn(String op, String username)
        {

            String line = "";
            String url = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
            String finishedurl = url+"?"+"operation="+op+"&"+"username="+username;

            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(finishedurl).openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    try {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null) {
                            Model json = gson.fromJson(line, Model.class);

                        }
                        bufferedReader.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch(IOException i) {
                        i.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void deleteconn(String op, String username)
        {

            String line = "";
            String url = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
            String finishedurl = url+"?"+"operation="+op+"&"+"username="+username;

            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(finishedurl).openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    try {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null) {
                            Model json = gson.fromJson(line, Model.class);

                        }
                        bufferedReader.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch(IOException i) {
                        i.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}

