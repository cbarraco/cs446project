package ca.uwaterloo.mapapp.logic.net;


import java.util.List;

import ca.uwaterloo.mapapp.logic.net.objects.Building;
import ca.uwaterloo.mapapp.logic.net.objects.Floor;
import ca.uwaterloo.mapapp.logic.net.objects.FloorPlanDatabase;
import ca.uwaterloo.mapapp.logic.net.objects.Room;
import ca.uwaterloo.mapapp.logic.net.objects.event.Event;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by brwarner
 * 24/05/15
 */
public interface FloorplanApiRestService {
    String API_ENDPOINT = "http://104.236.77.229:8000";

    @GET("/database.json")
    List<FloorPlanDatabase> getFloorPlanDatabases();

    @GET("/png/{floorplan}.json")
    List<Room> getRooms(@Path("floorplan") String floorplan);
}