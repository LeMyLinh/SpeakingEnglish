package com.example.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.model.Conversation;
import com.example.admin.speakingenglishiseasy.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;


public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private  Activity context;
    private int resource;
    private  List<Conversation> objects;

    private ImageView imgPersonA, imgPersonB, imgTranslateA, imgTranslateB;
    public static TextView txtPersonA, txtPersonB;


    public ConversationAdapter(Activity context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View item = inflater.inflate(this.resource,null);


        imgPersonA = (ImageView) item.findViewById(R.id.imgPersonA);
        imgPersonB = (ImageView) item.findViewById(R.id.imgPersonB);
        imgTranslateA = (ImageView) item.findViewById(R.id.imgTranslateA);
        imgTranslateB = (ImageView) item.findViewById(R.id.imgTranslateB);
        txtPersonA = (TextView) item.findViewById(R.id.txtPersonA);
        txtPersonB = (TextView) item.findViewById(R.id.txtPersonB);


        Conversation conversation = this.objects.get(position);
        txtPersonA.setText(conversation.getPersonA());
        txtPersonB.setText(conversation.getPersonB());

        addEvent(conversation);

        return item;
    }

    private void addEvent(final Conversation con) {
        imgTranslateA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(con.getPersonA());
            }
        });

        imgTranslateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(con.getPersonB());
            }
        });
    }

    /** Translate a given text between a source and a destination language */
    public void translate(String text) {
        if (examConnectInternet()) {
            String translated = null;
            try {
                String query = URLEncoder.encode(text, "UTF-8");
                String langpair = URLEncoder.encode(Locale.ENGLISH.getLanguage()+"|"+"Vi", "UTF-8");
                String url = "http://mymemory.translated.net/api/get?q="+query+"&langpair="+langpair;
                HttpClient hc = new DefaultHttpClient();
                HttpGet hg = new HttpGet(url);

                HttpResponse hr = hc.execute(hg);
                if(hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String jsonString = String.valueOf(EntityUtils.toString(hr.getEntity()));
                    if (jsonString.charAt(jsonString.length() - 1) == ']') // fix when string lack of }
                        jsonString += '}';

                    JSONObject response = new JSONObject(jsonString);
                    translated = response.getJSONObject("responseData").getString("translatedText");
                    Toast.makeText(context, translated, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean examConnectInternet() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
