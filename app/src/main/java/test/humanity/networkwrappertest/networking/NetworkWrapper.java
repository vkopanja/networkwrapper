package test.humanity.networkwrappertest.networking;

import android.app.Activity;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vkopanja on 18/08/2015.
 * <p>A wrapper class for network operations</p>
 * <p>Uses either {@link OkHttpClient} or {@link HttpURLConnection}</p>
 */
public class NetworkWrapper {

    private static Activity act;
    private static Exception ex;
    private Type type;
    private String url;
    private Method method;
    private Pair<String, String>[] headerProperties;
    private JSONObject postData;

    // HttpUrlConnection fields
    private HttpURLConnection httpURLConnection;
    private HttpsURLConnection httpsURLConnection;
    private InputStream inputStream;

    // OkHttp fields
    private OkHttpClient okHttpClient;
    private Response response;
    private Request request;

    /**
     * <p>A wrapper class for network operations</p>
     * <p>Uses either {@link OkHttpClient} or {@link HttpURLConnection}</p>
     * @param builder
     */
    public NetworkWrapper(Builder builder) {
        this.act = builder.act;
        this.ex = builder.ex;
        this.url = builder.url;
        this.type = builder.type;
        this.postData = builder.postData;
        this.httpURLConnection = builder.httpURLConnection;
        this.httpsURLConnection = builder.httpsURLConnection;
        this.inputStream = builder.inputStream;
        this.method = builder.method;
        this.headerProperties = builder.headerProperties;
        this.okHttpClient = builder.okHttpClient;
        this.response = builder.response;
        this.request = builder.request;
    }

    public static class Builder {

        private static Activity act;
        private static Exception ex;
        private Type type;
        private String url;
        private JSONObject postData;

        // HttpUrlConnection fields
        private HttpURLConnection httpURLConnection;
        private HttpsURLConnection httpsURLConnection;
        private InputStream inputStream;
        private Method method;
        private Pair<String, String>[] headerProperties;

        // OkHttp fields
        private OkHttpClient okHttpClient;
        private static Response response;
        private static Request request;

        /**
         * Initalize a Builder with a {@link test.humanity.networkwrappertest.networking.NetworkWrapper.Type}
         * @param activity Used for handling errors with a toast
         * @param type {@link test.humanity.networkwrappertest.networking.NetworkWrapper.Type}
         */
        public Builder(Activity activity, Type type)
        {
            this.act = activity;
            this.type = type;
        }

        /**
         * Initalize a connection with an http or https URL
         * @param httpUrl eg. http://example.com
         * @return
         */
        public Builder connection(String httpUrl)
        {
            try
            {
                if(!httpUrl.contains("http") || !httpUrl.contains("https"))
                    throw new MalformedURLException("URL must start with http or https.");

                URL url = new URL(httpUrl);
                this.url = httpUrl;

                switch(type)
                {
                    case HttpUrlConnection:
                        if(httpUrl.contains("https"))
                            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        else
                            this.httpURLConnection = (HttpURLConnection) url.openConnection();
                        break;
                    case OkHttp:
                        this.okHttpClient = new OkHttpClient();
                        this.request = new Request.Builder().url(httpUrl).build();
                        break;
                }
            }
            catch(MalformedURLException e)
            {
                showToast(e.getMessage(), e);
                ex = e;
            }
            catch(IOException e)
            {
                showToast(e.getMessage(), e);
                ex = e;
            }
            catch(Exception e)
            {
                showToast(e.getMessage(), e);
                ex = e;
            }

            return this;
        }

        /**
         * Set a method for the request
         * @param method {@link test.humanity.networkwrappertest.networking.NetworkWrapper.Method}
         * @return
         */
        public Builder method(Method method)
        {
            this.method = method;
            try
            {
                switch(type)
                {
                    case HttpUrlConnection:
                        if(httpURLConnection != null)
                            httpURLConnection.setRequestMethod(method.type);
                        if(httpsURLConnection != null)
                            httpsURLConnection.setRequestMethod(method.type);
                        break;
                    case OkHttp:
                        if(okHttpClient != null)
                        {
                            if(request != null)
                            {
                                // no need to set method for OkHttp here
                            }
                        }
                        break;
                }
            }
            catch(ProtocolException e)
            {
                showToast(e.getMessage(), e);
                ex = e;
            }
            catch (Exception e)
            {
                showToast(e.getMessage(), e);
                ex = e;
            }

            return this;
        }

        /**
         * Must be called when using {@link #type OkHttp} <br/>
         * Builds the {@link #response Response}
         * @return
         */
        public Builder response()
        {
            switch(type)
            {
                case HttpUrlConnection:
                    break;
                case OkHttp:
                    if(okHttpClient != null)
                    {
                        if(this.request == null)
                            throw new IllegalStateException("Request must be initalized.");

                        try
                        {
                            this.response = okHttpClient.newCall(this.request).execute();
                        } catch (IOException e) {
                            showToast(e.getMessage(), e);
                            ex = e;
                        } catch (Exception e) {
                            showToast(e.getMessage(), e);
                            ex = e;
                        }
                    }
                    break;
            }

            return this;
        }

        /**
         * Sets the headers using key value {@link Pair}
         * @param headers
         * @return
         */
        public Builder headers(Pair<String, String>... headers)
        {
            this.headerProperties = headers;

            switch(type)
            {
                case HttpUrlConnection:
                    if(httpURLConnection != null)
                    {
                        for(Pair<String, String> item : headers)
                        {
                            httpURLConnection.setRequestProperty(item.first, item.second);
                        }
                    }
                    else if(httpsURLConnection != null)
                    {
                        for(Pair<String, String> item : headers)
                        {
                            httpsURLConnection.setRequestProperty(item.first, item.second);
                        }
                    }
                    break;
                case OkHttp:
                    if(okHttpClient != null)
                    {
                        Request.Builder requestBuilder = new Request.Builder().url(url);
                        for(Pair<String, String> item : headers)
                        {
                            requestBuilder.header(item.first, item.second);
                        }

                        this.request = requestBuilder.build();
                    }
                    break;
            }

            return this;
        }

        /**
         * Set the post data using a key value {@link JSONObject}
         * @param jo {@link JSONObject}
         * @return
         * @throws IllegalStateException When trying to set {@link #postData(JSONObject)} and we are using {@link #method GET}
         */
        public Builder postData(JSONObject jo) throws IllegalStateException
        {
            this.postData = jo;

            if(method != Method.POST)
                throw new IllegalStateException("Cannot send post data if method is GET.");

            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = postData.keys();

            try {

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = postData.get(key);

                    sb.append(key + "=" + value.toString() + "&");
                }

                sb.setLength(sb.toString().length() - 1);

                if (httpURLConnection != null) {
                    OutputStream os = httpURLConnection.getOutputStream();
                    os.write(sb.toString().getBytes());
                    os.close();
                } else if (httpsURLConnection != null) {
                    OutputStream os = httpsURLConnection.getOutputStream();
                    os.write(sb.toString().getBytes());
                    os.close();
                } else if (okHttpClient != null) {
                    RequestBody rb = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), sb.toString());
                    this.request = new Request.Builder().url(url).post(rb).build();
                }
            } catch (JSONException e) {
                showToast(e.getMessage(), e);
                ex = e;
            } catch (IOException e) {
                showToast(e.getMessage(), e);
                ex = e;
            }

            return this;
        }

        /**
         * Builds the NetworkWrapper class and returns an instance
         * @return
         */
        public NetworkWrapper build()
        {
            if(ex != null)
            {
                showToast(ex.getMessage(), ex);
                return new NetworkWrapper(this);
            }

            if(type == Type.OkHttp)
            {
                if(response == null)
                    throw new IllegalStateException("You must call .response() before calling build when using OkHttp Type.");
            }

            return new NetworkWrapper(this);
        }
    }

    /**
     * Returns the strings response of our request
     * @return
     */
    public String getStringResponse()
    {
        StringBuilder result = new StringBuilder();

        try
        {
            if(ex != null)
                throw ex;

            if(httpURLConnection != null || httpsURLConnection != null)
            {
                inputStream = httpURLConnection != null ? httpURLConnection.getInputStream() : httpsURLConnection.getInputStream();
            }
            else if(okHttpClient != null)
            {
                if(response != null)
                {
                    result = new StringBuilder(response.body().string());
                }
                else {
                    throw new IOException("There was a connection problem.");
                }
            }

            if(inputStream != null)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
            }
        } catch (IOException e) {
            showToast(e.getMessage(), e);
            ex = e;
        } catch (Exception e) {
            showToast(e.getMessage(), e);
            ex = e;
        }

        return result.toString();
    }

    public enum Type
    {
        OkHttp, HttpUrlConnection
    }

    public enum Method
    {
        POST("POST"), GET("GET");

        private String type;
        Method(String type)
        {
            this.type = type;
        }
    }

    private static void showToast(String message, Throwable t) {
        if(act != null) {
            final String msg = message;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
                }
            });
        }

        Log.e(NetworkWrapper.class.toString(), message, t);
    }
}