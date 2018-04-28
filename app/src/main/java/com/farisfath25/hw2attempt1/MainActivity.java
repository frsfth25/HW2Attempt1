package com.farisfath25.hw2attempt1;

/*
Resources:
- https://www.youtube.com/watch?v=BqMIcugsCFc
- https://try.jsoup.org/
- https://github.com/SmtCO/AndroidWeek5
- https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
- https://stackoverflow.com/questions/29743535/android-listview-onclick-open-a-website
 */

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener
{
    private List<contentItem> conItems = new ArrayList<contentItem>();

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

        tab1 = (Button) findViewById(R.id.tab_1);
        tab2 = (Button) findViewById(R.id.tab_2);
        tab3 = (Button) findViewById(R.id.tab_3);

        listView = (ListView) findViewById(R.id.lvContent);
        listView.setOnItemLongClickListener(this);

        foodList = new ArrayList<String>();

        annList = new ArrayList<>();
        newsList = new ArrayList<>();

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

        foodAdapter = new ArrayAdapter<String>(this, R.layout.my_item_view,R.id.txtItem ,foodList);

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
                                //getFood();

                                mode=1;
                                listView.setAdapter(foodAdapter);
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
                                //getAnnouncements();

                                mode=2;
                                listView.setAdapter(annAdapter);
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
                                //getNews();

                                mode=3;
                                listView.setAdapter(newsAdapter);
                            }
                        }
                );
    }

    private void getFood ()
    {
        //    Document doc = null;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final StringBuilder builder = new StringBuilder();

                try
                {
                    Document doc = Jsoup.connect("http://www.ybu.edu.tr/sks/").get();

                    //ArrayList<String> foodMenu = new ArrayList<>();
                    Element table = doc.select("table").get(0); //select the first found table
                    Elements rows = table.select("tr");

                    for (int i = 2; i < rows.size(); i++) //first and second rows are not important
                    {
                        Element row = rows.get(i);
                        Elements cols = row.select("td");

                        //foodMenu.add(cols.get(0).text());

                        builder.append(cols.get(0).text()).append("\n\n");

                     /*   if (cols.get(7).text().equals("down"))
                        {
                            foodMenu.add(cols.get(5).text());
                        }
                    */

                    }

                 /*)   for (int k = 0; k < foodMenu.size(); k++)
                    {
                        builder.append(foodMenu.get(k)).append("\n\n");
                    }
                    */
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
    }

    private void getAnnouncements()
    {
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
    }

    private void getNews()
    {
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
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        String url;
        Uri uri;
        Intent intent;

        if (mode==2 || mode==3) {
            switch (mode) {
                case 2:
                    url = annList.get(i).getUrl();
                    uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case 3:
                    url = newsList.get(i).getUrl();
                    uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

        return false;
    }
}
