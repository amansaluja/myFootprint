package in.myfootprint.myfootprint.models;

/**
 * Created by Aman on 12-12-2015.
 */
public class FeedItem {
    private String id;
    private String name, status, image, profilePic, timeStamp, url, flatOwner;

    public FeedItem() {
    }

    public FeedItem(String id, String name, String image, String status,
                    String profilePic, String timeStamp, String url, String flatOwner) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.flatOwner = flatOwner;
    }

    public String getFlatOwner() {
        return flatOwner;
    }

    public void setFlatOwner(String flatOwner) {
        this.flatOwner = flatOwner;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}