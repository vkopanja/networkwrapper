package test.humanity.networkwrappertest.networking;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.http.OkHeaders;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import test.humanity.networkwrappertest.interfaces.OnAsyncPostExecute;

/**
 * Created by vkopanja on 18/08/2015.
 */
public class NetworkWrapper {

    private static OnAsyncPostExecute mAsyncPostExecute;

    private Context ctx;
    private Type type;
    private String url;

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

    public NetworkWrapper(Builder builder) {
        this.ctx = builder.ctx;
        this.url = builder.url;
        this.type = builder.type;
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

        private Context ctx;
        private Type type;
        private String url;

        // HttpUrlConnection fields
        private HttpURLConnection httpURLConnection;
        private HttpsURLConnection httpsURLConnection;
        private InputStream inputStream;
        private Method method;
        private Pair<String, String>[] headerProperties;

        // OkHttp fields
        private OkHttpClient okHttpClient;
        private OkHeaders okHeaders;
        private static Response response;
        private static Request request;

        public Builder context(Context ctx)
        {
            this.ctx = ctx;
            return this;
        }
        public Builder(Type type)
        {
            this.type = type;
        }

        /***
         * Initalize a connection with an http or https URL
         * @param httpUrl
         * @return
         */
        public Builder connection(String httpUrl)
        {
            try
            {
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
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return this;
        }

        public Builder method(Method method)
        {
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
                                // TODO: eventually add method type for OkHttp
                            }
                        }
                        break;
                }
            }
            catch(ProtocolException e)
            {
                e.printStackTrace();
            }

            return this;
        }

        /***
         * Must be called when using {@link #type OkHttp}
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
                            throw new IllegalArgumentException("Request must be initalized.");

                        try
                        {
                            AsyncOkHttpNetwork network = new AsyncOkHttpNetwork();
                            this.response = network.execute(okHttpClient.newCall(this.request)).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            return this;
        }

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

        public NetworkWrapper build()
        {
            return new NetworkWrapper(this);
        }
    }

    public String getStringResponse()
    {
        StringBuilder result = new StringBuilder();

        try
        {
            if(httpURLConnection != null || httpsURLConnection != null)
            {
                AsyncHttpNetwork network = new AsyncHttpNetwork();
                inputStream = network.execute(httpURLConnection != null ? httpURLConnection : httpsURLConnection).get();
            }
            else if(okHttpClient != null)
            {
                if(response != null)
                {
                    AsyncResponseBodyNetwork network = new AsyncResponseBodyNetwork();
//                    network.execute(response.body());
                    result = new StringBuilder(network.execute(response.body()).get());
//                    AsyncOkHttpNetwork okHttpNetwork = new AsyncOkHttpNetwork();
//                    response = okHttpNetwork.execute(okHttpClient.newCall(request)).get();
                }
            }

            if(inputStream != null)
            {
                AsyncGzipNetwork gzip = new AsyncGzipNetwork();
                result = gzip.execute(inputStream).get();
            }
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }

        return result.toString();
    }

    // region Async tasks

    private class AsyncGzipNetwork extends AsyncTask<InputStream, Void, StringBuilder>
    {
        @Override
        protected StringBuilder doInBackground(InputStream... inputStreams)
        {
            StringBuilder result = new StringBuilder();
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreams[0]));

                String line;

                while((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(StringBuilder stringBuilder)
        {
            super.onPostExecute(stringBuilder);
//            mAsyncPostExecute.onPostExecute();
        }
    }

    private class AsyncHttpNetwork extends AsyncTask<HttpURLConnection, Void, InputStream>
    {
        @Override
        protected InputStream doInBackground(HttpURLConnection... urls)
        {
            InputStream is = null;
            try
            {
                is = urls[0].getInputStream();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                byte[] data = e.getMessage().getBytes(Charset.forName("UTF-8"));
                is = new ByteArrayInputStream(data);
            }

            return is;
        }

        @Override
        protected void onPostExecute(InputStream inputStream)
        {
            super.onPostExecute(inputStream);
//            mAsyncPostExecute.onPostExecute();
        }
    }

    private static class AsyncOkHttpNetwork extends AsyncTask<Call, Void, Response>
    {
        @Override
        protected Response doInBackground(Call... calls)
        {
            Response response = null;

            try
            {
                response = calls[0].execute();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(Response response1)
        {
            super.onPostExecute(response);
            response = response1;
//            mAsyncPostExecute.onPostExecute();
        }
    }

    private class AsyncResponseBodyNetwork extends AsyncTask<ResponseBody, Void, String>
    {
        @Override
        protected String doInBackground(ResponseBody... responseBodies)
        {
            String result = null;
            try
            {
                result = responseBodies[0].string();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            mAsyncPostExecute.onPostExecute(s);
        }
    }

    // endregion

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

    public void setAsyncPostExecute(OnAsyncPostExecute mAsyncPostExecute)
    {
        this.mAsyncPostExecute = mAsyncPostExecute;
    }
}