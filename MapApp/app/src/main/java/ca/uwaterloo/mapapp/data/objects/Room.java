package ca.uwaterloo.mapapp.data.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by cjbarrac
 * 23/05/15
 */
@DatabaseTable(tableName = Room.TABLE)
public class Room {

    public static final String TABLE = "room";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NUMBER = "number";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private long id;

    @DatabaseField(columnName = COLUMN_NUMBER)
    private String number;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
