package prabhalab.client.location;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIResponse
{
    private String location_id;

    public String getLocation_id ()
    {
        return location_id;
    }

    public void setLocation_id (String location_id)
    {
        this.location_id = location_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [location_id = "+location_id+"]";
    }
}
