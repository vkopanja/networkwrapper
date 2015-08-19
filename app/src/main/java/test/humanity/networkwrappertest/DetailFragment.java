package test.humanity.networkwrappertest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import test.humanity.networkwrappertest.classes.FlickrImage;

/**
 * Created by vkopanja on 19/08/2015.
 */
public class DetailFragment extends Fragment {

    public DetailFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        TextView tvOwner = (TextView) v.findViewById(R.id.tvOwner);
        TextView tvPublic = (TextView) v.findViewById(R.id.tvPublic);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            FlickrImage image = (FlickrImage) bundle.getSerializable("image");
            tvTitle.setText(image.getTitle());
            tvOwner.setText(image.getOwner());
            tvPublic.setText(image.getIsPublic() ? "Public" : "Non-public");
        }

        return v;
    }
}