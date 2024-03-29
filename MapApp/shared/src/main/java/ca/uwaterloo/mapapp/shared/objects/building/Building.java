package ca.uwaterloo.mapapp.shared.objects.building;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by cjbarrac
 * 23/05/15
 */
@DatabaseTable(tableName = Building.TABLE)
public class Building implements Serializable {

    public static final String TABLE = "building";

    public static final String COLUMN_BUILDING_ID = "building_id";
    public static final String COLUMN_BUILDING_CODE = "building_code";
    public static final String COLUMN_BUILDING_NAME = "building_name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    /**
     * Not generated by database, retrieved from API
     */
    @DatabaseField(columnName = COLUMN_BUILDING_ID, id = true)
    private String buildingId;

    @DatabaseField(columnName = COLUMN_BUILDING_CODE)
    private String buildingCode;

    @DatabaseField(columnName = COLUMN_BUILDING_NAME)
    private String buildingName;

    @DatabaseField(columnName = COLUMN_LATITUDE)
    private Double latitude;

    @DatabaseField(columnName = COLUMN_LONGITUDE)
    private Double longitude;

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Building{" +
                "buildingId='" + buildingId + '\'' +
                ", buildingCode='" + buildingCode + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
