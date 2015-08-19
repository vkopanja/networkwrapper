package test.humanity.networkwrappertest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import test.humanity.networkwrappertest.R;
import test.humanity.networkwrappertest.classes.FlickrImage;

/**
 * Created by vkopanja on 19/08/2015.
 */
public class ImagesAdapter extends BaseAdapter {

    private Context ctx;
    private List<FlickrImage> imageUrls;

    public ImagesAdapter(Context ctx, List<FlickrImage> imageUrls)
    {
        this.ctx = ctx;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount()
    {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int i)
    {
        return imageUrls.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder holder;

        if(view == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_main, viewGroup, false);
            holder = new ViewHolder();
            holder.imgSmall = (ImageView) view.findViewById(R.id.imgSmall);
            holder.tvShortText = (TextView) view.findViewById(R.id.tvShortText);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(ctx).load(imageUrls.get(i).getImageUrl()).into(holder.imgSmall);
        holder.tvShortText.setText(imageUrls.get(i).getTitle());

        return view;
    }

    static class ViewHolder
    {
        ImageView imgSmall;
        TextView tvShortText;
    }
}
