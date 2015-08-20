package test.humanity.networkwrappertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.activeandroid.query.Select;

import java.util.List;

import test.humanity.networkwrappertest.classes.FlickrImage;
import test.humanity.networkwrappertest.interfaces.Callbacks;
import test.humanity.networkwrappertest.interfaces.OnAsyncPostExecute;


public class MainActivity extends ActionBarActivity implements Callbacks, OnAsyncPostExecute {

    private boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar pd = (ProgressBar) findViewById(R.id.progressBar);
        List<FlickrImage> imageList = new Select().from(FlickrImage.class).execute();
        if(imageList.size() > 0)
            pd.setVisibility(View.GONE);

        isTwoPane = false;

        if(findViewById(R.id.fragmentDetail) != null)
        {
            isTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(FlickrImage image)
    {
        if(isTwoPane)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Bundle extras = new Bundle();
            extras.putSerializable("image", image);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(extras);

            ft.replace(R.id.fragmentDetail, detailFragment);
            ft.commit();
        }
        else
        {
            Intent detailActivity = new Intent(this, DetailActivity.class);
            detailActivity.putExtra("image", image);
            startActivity(detailActivity);
        }
    }

    @Override
    public void onAsyncResult(String result)
    {
        ProgressBar pd = (ProgressBar) findViewById(R.id.progressBar);
        pd.setVisibility(View.GONE);
    }
}
