package in.myfootprint.myfootprint.models;

/**
 * Created by Aman on 12-12-2015.
 */

public class NotificationItem {

    private String id, userName, timeStamp, image, profilePic, userID, flatOID, flatOPic, flatOName;

    public NotificationItem() {
    }

    public NotificationItem(String id, String userName, String timeStamp, String image, String userID, String flatOID, String flatOName,
                            String flatOPic, String profilePic) {
        super();
        this.id = id;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.image = image;
        this.profilePic = profilePic;
        this.userID = userID;
        this.flatOID = flatOID;
        this.flatOName = flatOName;
        this.flatOPic = flatOPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlatOID() {
        return flatOID;
    }

    public void setFlatOID(String flatOID) {
        this.flatOID = flatOID;
    }

    public String getFlatOName() {
        return flatOName;
    }

    public void setFlatOName(String flatOName) {
        this.flatOName = flatOName;
    }

    public String getFlatOPic() {
        return flatOPic;
    }

    public void setFlatOPic(String flatOPic) {
        this.flatOPic = flatOPic;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}