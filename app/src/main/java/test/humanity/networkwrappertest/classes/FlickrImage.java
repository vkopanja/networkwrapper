package test.humanity.networkwrappertest.classes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import test.humanity.networkwrappertest.helpers.Constants;

/**
 * Created by vkopanja on 19/08/2015.
 */
@Table(name = "FlickrImages")
public class FlickrImage extends Model implements Serializable {

    @Column(name = "PhotoId")
    private String photoId;

    private String imageUrl;

    @Column(name = "Title")
    private String title;

    @Column(name = "Owner")
    private String owner;

    @Column(name = "Secret")
    private String secret;

    @Column(name = "Farm")
    private int farm;

    @Column(name = "Server")
    private int server;

    @Column(name = "IsPublic")
    private boolean isPublic;

    public FlickrImage() {}

    public FlickrImage(String imageUrl, String title)
    {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getPhotoId()
    {
        return photoId;
    }

    public void setPhotoId(String id)
    {
        this.photoId = id;
    }

    public String getImageUrl()
    {
        if(imageUrl == null)
        {
            imageUrl = String.format(Constants.FlickrImageUrl, farm, server, photoId, secret);
        }

        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public boolean getIsPublic()
    {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    public void setIsPublic(int isPublic)
    {
        this.isPublic = isPublic == 1;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public int getFarm()
    {
        return farm;
    }

    public void setFarm(int farm)
    {
        this.farm = farm;
    }

    public int getServer()
    {
        return server;
    }

    public void setServer(int server)
    {
        this.server = server;
    }
}