package in.myfootprint.myfootprint.models;

/**
 * Created by Aman on 12-12-2015.
 */
public class CouponItem {

    private String validity, companyName, couponCode, location, description, offerText, tandc, redeemedOn, link, createdOn, couponId;
    private String photo;
    private Boolean isGifted;
    private int cost;

    public CouponItem() {
    }

    public CouponItem(String validity, String companyName, String location, String description, String offerText, String tandc,
                      String couponCode, String redeemedOn, String link, String createdOn, String couponId, String photo,
                      Boolean isGifted) {
        super();
        this.validity = validity;
        this.companyName = companyName;
        this.couponCode = couponCode;
        this.isGifted = isGifted;
        this.location = location;
        this.description = description;
        this.offerText = offerText;
        this.tandc = tandc;
        this.redeemedOn = redeemedOn;
        this.link = link;
        this.createdOn = createdOn;
        this.couponId = couponId;
        this.photo = photo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRedeemedOn() {
        return redeemedOn;
    }

    public void setRedeemedOn(String redeemedOn) {
        this.redeemedOn = redeemedOn;
    }

    public String getTandc() {
        return tandc;
    }

    public void setTandc(String tandc) {
        this.tandc = tandc;
    }

    public Boolean getIsGifted() {
        return isGifted;
    }

    public void setIsGifted(Boolean isGifted) {
        this.isGifted = isGifted;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}