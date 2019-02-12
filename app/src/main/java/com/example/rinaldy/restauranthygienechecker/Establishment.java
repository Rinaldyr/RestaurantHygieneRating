package com.example.rinaldy.restauranthygienechecker;

import java.text.DateFormat;
import java.util.Date;

public class Establishment {

    private Integer FHRSID;
    private String BusinessName;
    private String BusinessType;
    private Integer BusinessTypeID;
    private String AddressLine1, AddressLine2, AddressLine3, AddressLine4;
    private String PostCode;
    private String Phone;
    private String RatingKey;
    private String RatingValue;
    private Date RatingDate;
    private String LocalAuthorityName;
    private String LocalAuthorityWebsite;
    private String LocalAuthorityEmailAddress;
    private Geocode geocode;
    private Double Distance;

    public Integer getFHRSID() {
        return FHRSID;
    }

    public String getBusinessName() {
        return BusinessName;
    }

    public String getBusinessType() {
        return BusinessType;
    }

    public Integer getBusinessTypeID() {
        return BusinessTypeID;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public String getAddressLine3() {
        return AddressLine3;
    }

    public String getAddressLine4() {
        return AddressLine4;
    }

    public String getPostCode() {
        return PostCode;
    }

    public String getPhone() {
        return Phone;
    }

    public String getRatingKey() {
        return RatingKey;
    }

    public String getRatingValue() {
        return RatingValue;
    }

    public Date getRatingDate() {
        return RatingDate;
    }

    public String getLocalAuthorityName() {
        return LocalAuthorityName;
    }

    public String getLocalAuthorityWebsite() {
        return LocalAuthorityWebsite;
    }

    public String getLocalAuthorityEmailAddress() {
        return LocalAuthorityEmailAddress;
    }

    public Geocode getGeocode() {
        return geocode;
    }

    public Double getDistance() {
        return Distance;
    }

    public String getHumanlyDate() { return DateFormat.getDateInstance(DateFormat.LONG).format(RatingDate); }

    public String getFullAddress(String separator) {
        return AddressLine1
                + (AddressLine2.isEmpty() ? "" : separator + AddressLine2)
                + (AddressLine3.isEmpty() ? "" : separator + AddressLine3)
                + (AddressLine4.isEmpty() ? "" : separator + AddressLine4);
    }

    @Override
    public String toString() {
        return BusinessName;
    }

    public class Geocode {
        Double longitude;
        Double latitude;

        public Geocode(Double longitude, Double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
    }
}
