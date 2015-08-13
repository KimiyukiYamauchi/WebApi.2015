package com.example.yamauchi.imasora;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yamauchi on 2015/08/11.
 */
public class HttpAsyncLoader2 extends AsyncTaskLoader<String>
{
    private String url_str = null; // WebAPIのURL
    private final static String ENCODE = "UTF-8";
    private HttpURLConnection mHttpConnection = null;       // HTTP通信

    public HttpAsyncLoader2(Context context, String url) {
        super(context);
        url_str = url;
    }

    @Override
    public String loadInBackground() {

        URL url;    // URL指定


        try {
            url = new URL(url_str);
            // HttpURLConnectionインスタンス作成
            mHttpConnection = (HttpURLConnection)url.openConnection();
            // GET設定
            mHttpConnection.setRequestMethod("GET");
            mHttpConnection.setRequestProperty("Connection", "close");
            mHttpConnection.setFixedLengthStreamingMode(0);

            mHttpConnection.connect();

            // 応答データを取得する
            int status = mHttpConnection.getResponseCode();
            if(status >= 300){
                String message = mHttpConnection.getResponseMessage();
                return null;
            }

            InputStream in = mHttpConnection.getInputStream();
            String encoding = mHttpConnection.getContentEncoding();
            if(encoding == null){
                encoding = "UTF-8";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));

            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            Log.d(getClass().getName() + "sb = ", sb.toString());

            return sb.toString();
        }
        catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
        finally {
            // 通信終了時は、接続を閉じる （4）
            mHttpConnection.disconnect();
        }
        return null;
    }
}
