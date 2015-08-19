package test.humanity.networkwrappertest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import test.humanity.networkwrappertest.classes.FlickrImage;

/**
 * Created by vkopanja on 19/08/2015.
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = new Bundle();
        extras.putSerializable("image", (FlickrImage) getIntent().getExtras().get("image"));

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(extras);

        ft.add(R.id.mainHolder, detailFragment);
        ft.commit();
    }
}