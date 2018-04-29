package com.farisfath25.hw2attempt1;

/*
Faris Fathurrahman - 14050141015
HW#2
CENG427 - Mobile Programming Devices

Resources:
- https://www.youtube.com/watch?v=BqMIcugsCFc
- https://stackoverflow.com/questions/24772828/how-to-parse-html-table-using-jsoup
- https://try.jsoup.org/
- https://github.com/SmtCO/AndroidWeek5
- https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
- https://stackoverflow.com/questions/29743535/android-listview-onclick-open-a-website
- https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, ConnectivityReceiver.ConnectivityReceiverListener
{
    private Button tab1;
    private Button tab2;
    private Button tab3;

    private ListView listView;

    private ArrayList<String> foodList;
    private ArrayList<contentItem> annList;
    private ArrayList<contentItem> newsList;

    private ArrayAdapter<String> foodAdapter;

    private CustomAdapter annAdapter;
    private CustomAdapter newsAdapter;

    private int mode = 0; //1=food, 2=ann, 3=news

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tab1 = findViewById(R.id.tab_1);
        tab2 = findViewById(R.id.tab_2);
        tab3 = findViewById(R.id.tab_3);

        listView = findViewById(R.id.lvContent);
        listView.setOnItemLongClickListener(this);

        foodList = new ArrayList<>();

        annList = new ArrayList<>();
        newsList = new ArrayList<>();

        // Manually checking internet connection
        if (!ConnectivityReceiver.isConnected()) {
            String message = "NO INTERNET CONNECTION!\nPlease connect and try again!";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();

            finish();
        } else {
            String message = "CONNECTED TO INTERNET! :)";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }

        //getFood operation
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final StringBuilder builder = new StringBuilder();

                try
                {
                    Document doc = Jsoup.connect("http://www.ybu.edu.tr/sks/").get();

                    Element table = doc.select("table").get(0); //select the first found table
                    Elements rows = table.select("tr");

                    for (int i = 2; i < rows.size(); i++) //first and second rows are not important
                    {
                        Element row = rows.get(i);
                        Elements cols = row.select("td");

                        builder.append(cols.get(0).text()).append("\n\n");

                        foodList.add(cols.get(0).text());
                    }

                }
                catch (IOException e)
                {
                    builder.append("Error: ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //content.setText(builder.toString());
                    }
                });
            }
        }).start();

        foodAdapter = new ArrayAdapter<>(this, R.layout.my_item_view, R.id.txtItem, foodList);
        listView.setAdapter(foodAdapter);

        //getAnnouncements operation
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final StringBuilder builder = new StringBuilder();

                try
                {
                    Document doc = Jsoup.connect("http://www.ybu.edu.tr/muhendislik/bilgisayar/").get();

                    //String title = doc.title();
                    Elements annLinks = doc.select("div.contentAnnouncements div.caContent div.cncItem a");

                    //builder.append(title).append("\n");

                    for (Element annLink : annLinks)
                    {
                        builder.append("\n").append("Text: ").append(annLink.attr("title"))
                                .append("\n").append("Link: ").append(annLink.absUrl("href"));

                        String title = annLink.attr("title");
                        String url = annLink.absUrl("href");

                        annList.add(new contentItem(title,url));
                    }
                }
                catch (IOException e)
                {
                    builder.append("Error: ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //content.setText(builder.toString());
                    }
                });
            }
        }).start();

        annAdapter= new CustomAdapter(annList,getApplicationContext());

        //getNews operation
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final StringBuilder builder = new StringBuilder();

                try
                {
                    Document doc = Jsoup.connect("http://www.ybu.edu.tr/muhendislik/bilgisayar").get();

                    //String title = doc.title();
                    Elements nwsLinks = doc.select("div.contentNews div.cnContent div.cncItem a");

                    //builder.append(title).append("\n");

                    for (Element nwsLink : nwsLinks)
                    {
                        builder.append("\n").append("Text: ").append(nwsLink.attr("title"))
                                .append("\n").append("Link: ").append(nwsLink.absUrl("href"));

                        String title = nwsLink.attr("title");
                        String url = nwsLink.absUrl("href");

                        newsList.add(new contentItem(title,url));
                    }
                }
                catch (IOException e)
                {
                    builder.append("Error: ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // content.setText(builder.toString());
                    }
                });
            }
        }).start();

        newsAdapter= new CustomAdapter(newsList,getApplicationContext());

        //tabs functions
        tab1.setOnClickListener //Food tab
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                // Manually checking internet connection
                                if (!ConnectivityReceiver.isConnected()) {
                                    String message = "NO INTERNET CONNECTION!";
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();

                                }

                                //getFood();

                                mode=1;
                                listView.setAdapter(foodAdapter);

                                if (foodList.isEmpty()) {
                                    String message = "Failed to fetch data, please open the app again!";
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();

                                    finish();
                                }

                            }
                        }
                );

        tab2.setOnClickListener //Announcements tab
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                // Manually checking internet connection
                                if (!ConnectivityReceiver.isConnected()) {
                                    String message = "NO INTERNET CONNECTION!";
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();

                                }

                                //getAnnouncements();

                                mode=2;
                                listView.setAdapter(annAdapter);

                                if (annList.isEmpty()) {
                                    String message = "Failed to fetch data, please open the app again!";
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();

                                    finish();
                                }
                            }
                        }
                );

        tab3.setOnClickListener //News tab
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                // Manually checking internet connection
                                if (!ConnectivityReceiver.isConnected()) {
                                    String message = "NO INTERNET CONNECTION!";
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();

                                }

                                //getNews();

                                mode=3;
                                listView.setAdapter(newsAdapter);

                                if (newsList.isEmpty()) {
                                    String message = "Failed to fetch data, please open the app again!";
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();

                                    finish();
                                }
                            }
                        }
                );
    }

    // Showing the status in Toast view
    private void showWarn(boolean isConnected) {
        String message;

        if (isConnected) {
            message = "CONNECTED TO INTERNET!";

        } else {
            message = "NO INTERNET CONNECTION!";

        }

        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showWarn(isConnected);
    }

    private void getFood() {


    }

    private void getAnnouncements()
    {


    }

    private void getNews()
    {


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        final int index = i;

        if (mode == 2 || mode == 3) {
            switch (mode) {
                case 2:
                    new AlertDialog.Builder(this)
                            .setMessage("Open the Announcement link on your default browser?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                String url;
                                Uri uri;
                                Intent intent;

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    url = annList.get(index).getUrl();
                                    uri = Uri.parse(url);
                                    intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    break;
                case 3:
                    new AlertDialog.Builder(this)
                            .setMessage("Open the News link on default browser?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                String url;
                                Uri uri;
                                Intent intent;

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    url = newsList.get(index).getUrl();
                                    uri = Uri.parse(url);
                                    intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    break;
                default:
                    break;
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_lock_power_off)
                .setMessage("Exit the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
