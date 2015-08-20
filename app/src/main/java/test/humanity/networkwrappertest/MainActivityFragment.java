package test.humanity.networkwrappertest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import test.humanity.networkwrappertest.adapters.ImagesAdapter;
import test.humanity.networkwrappertest.classes.FlickrImage;
import test.humanity.networkwrappertest.helpers.Constants;
import test.humanity.networkwrappertest.interfaces.Callbacks;
import test.humanity.networkwrappertest.interfaces.OnAsyncPostExecute;
import test.humanity.networkwrappertest.networking.NetworkWrapper;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnAsyncPostExecute {

    private NetworkWrapper wrapper;
    private ListView listView;
    private String response;
    private Callbacks mCallbacks = sCallbacks;
    private ImagesAdapter adapter;
    private List<FlickrImage> photoUrls;
    private JSONObject result;

    public MainActivityFragment()
    {
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(!(activity instanceof Callbacks))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = sCallbacks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        photoUrls = new Select().from(FlickrImage.class).execute();

        if(photoUrls.size() == 0)
        {
            try
            {
                if(savedInstanceState == null || savedInstanceState.getString("response") == null) {
                    AsyncTest test = new AsyncTest();
                    test.execute(String.format(Constants.FlickrPhotoSearch, Constants.FlickrApiKey, Constants.PerPage));
                }
                else
                {
                    result = new JSONObject(savedInstanceState.getString("response"));
                    savedInstanceState.putString("response", result.toString());
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void getData() throws JSONException {
        response = result.toString();

        if(result.get("stat") != null && result.getString("stat").equals("ok"))
        {
            JSONObject photoData = result.getJSONObject("photos");
            JSONArray photosArray = photoData.getJSONArray("photo");

            photoUrls = new ArrayList<>();

            for(int i = 0; i < photosArray.length(); i++)
            {
                JSONObject jo = photosArray.getJSONObject(i);

                String photoSearch = String.format(Constants.FlickrImageUrl, jo.getInt("farm"), jo.getInt("server"), jo.getString("id"), jo.getString("secret"));
                FlickrImage img = new FlickrImage(photoSearch, jo.getString("title"));
                img.setPhotoId(jo.getString("id"));
                img.setImageUrl(photoSearch);
                img.setTitle(jo.getString("title"));
                img.setIsPublic(jo.getInt("ispublic"));
                img.setOwner(jo.getString("owner"));
                img.setSecret(jo.getString("secret"));
                img.setServer(jo.getInt("server"));
                img.setFarm(jo.getInt("farm"));
                img.save();

                photoUrls.add(img);
            }

            adapter = new ImagesAdapter(getActivity(), photoUrls);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) v.findViewById(R.id.listViewMain);

        adapter = new ImagesAdapter(getActivity(), photoUrls);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                mCallbacks.onItemSelected(photoUrls.get(i));
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("response", response);

    }

    @Override
    public void onAsyncResult(String result) {
        ((MainActivity)getActivity()).onAsyncResult(result);
    }

    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(FlickrImage image)
        {
        }
    };

    /***
     * Used for testing of the network wrapper
     */
    private class AsyncTest extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            try {
                wrapper = new NetworkWrapper.Builder(NetworkWrapper.Type.OkHttp)
                        .connection(String.format(Constants.FlickrPhotoSearch, Constants.FlickrApiKey, Constants.PerPage))
                        .response()
                        .build();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
//            wrapper.setAsyncPostExecute((MainActivity) getActivity());
            return wrapper.getStringResponse();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                result = new JSONObject(s);
                getData();
                onAsyncResult(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
