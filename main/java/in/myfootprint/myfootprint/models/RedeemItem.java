package in.myfootprint.myfootprint.models;

/**
 * Created by Aman on 12-12-2015.
 */
public class RedeemItem {

    private String id, companyName, description, photo, cost, usageLeft, tandc, offerText, location, link, validity;

    public RedeemItem() {
    }

    public RedeemItem(String id, String companyName, String description, String photo,
                      String cost, String usageLeft, String tandc, String offerText, String location, String link, String validity) {
        super();
        this.id = id;
        this.companyName = companyName;
        this.description = description;
        this.photo = photo;
        this.cost = cost;
        this.usageLeft = usageLeft;
        this.tandc = tandc;
        this.offerText = offerText;
        this.location = location;
        this.link = link;
        this.validity = validity;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public String getTandc() {
        return tandc;
    }

    public void setTandc(String tandc) {
        this.tandc = tandc;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsageLeft() {
        return usageLeft;
    }

    public void setUsageLeft(String usageLeft) {
        this.usageLeft = usageLeft;
    }
}