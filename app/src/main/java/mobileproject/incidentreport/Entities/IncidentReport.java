package mobileproject.incidentreport.Entities;

/**
 * Created by zthomas on 11/6/15.
 */
public class IncidentReport {
    public float longitude;
    public float latitude;
    public String description;
    public int incidentId;
    public String category;
    public IncidentReport()
    {

    }
    public void setLongitude(float lng)
    {
        this.longitude = lng;
    }
    public float getLongitude()
    {
        return longitude;
    }
    public  void setLatitude(float lat)
    {
        this.latitude = lat;

    }

    public float getLatitude()
    {
        return latitude;
    }
    public void setDescription(String describe)
    {
        this.description = describe;
    }

    public String getDescription()
    {
        return description;
    }

    public void setIncidentId(int id)
    {
        this.incidentId = id;
    }

    public int getIncidentId()
    {
        return incidentId;
    }
    public void setCategory(String cat)
    {
        this.category = cat;
    }

    public String getCategory()
    {
        return  category;
    }
}