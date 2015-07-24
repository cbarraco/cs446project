package ca.uwaterloo.mapapp.logic.net;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.uwaterloo.mapapp.logic.net.serialization.ServerApiJsonDeserializer;
import ca.uwaterloo.mapapp.logic.net.services.IServerRestService;
import ca.uwaterloo.mapapp.shared.ICallback;
import ca.uwaterloo.mapapp.shared.IRequestor;
import ca.uwaterloo.mapapp.shared.objects.event.EventNote;
import ca.uwaterloo.mapapp.shared.objects.event.EventRanking;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class ServerRestApi {
    private static HashMap<String, Object> localCacheMap = new HashMap<>();

    private static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(List.class, new ServerApiJsonDeserializer<List>())
            .create();
    
    private static RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(IServerRestService.API_ENDPOINT)
            .setConverter(new GsonConverter(gson))
            .build();

    private static IServerRestService service = restAdapter.create(IServerRestService.class);

    public static void requestEvents(final ICallback callback) {
        requestData("Events", callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEvents();
            }
        });
    }
    public static void requestEventImages(final ICallback callback) {
        requestData("EventImages", callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventImages();
            }
        });
    }
    public static void requestEventLocations(final ICallback callback) {
        requestData("EventLocations", callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventLocations();
            }
        });
    }
    public static void requestEventTimes(final ICallback callback) {
        requestData("EventTimes", callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventTimes();
            }
        });
    }
    public static void requestEventNotes(final ICallback callback, final Long eventId) {
        requestData("EventNotes", callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventNotes(eventId);
            }
        });
    }
    public static void requestEventRanking(final ICallback callback, final Long eventId) {
        requestData("EventRanking", callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventRanking(eventId);
            }
        });
    }
    public static void addOrSetEventNote(final ICallback callback, final EventNote note) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Response result =service.setEventNote(note);
                return result.getStatus() == 200;
            }
        });
    }
    public static void addOrSetEventRanking(final ICallback callback, final EventRanking ranking) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                System.out.println(gson.toJson(ranking, EventRanking.class));
                Response result = service.setEventRanking(ranking);
                return result.getStatus() == 200;
            }
        });
    }
    public static void deleteEventNote(final ICallback callback, final EventNote note) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Response result = service.deleteEventNote(note.getId());
                return result.getStatus() == 200;
            }
        });
    }
    public static void deleteEventRanking(final ICallback callback, final EventRanking ranking) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Response result = service.deleteEventRanking(ranking.getId());
                return result.getStatus() == 200;
            }
        });
    }

    private static void requestData(final String cacheKey, final ICallback callback, final IRequestor requestor)
    {
        if(localCacheMap.containsKey(cacheKey)) {
            callback.call(localCacheMap.get(cacheKey));
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object result = requestor.request();
                    localCacheMap.put(cacheKey, result);
                    callback.call(result);
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

            }
        }).start();
    }

    private static void sendData(final ICallback callback, final IRequestor requestor)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestor.request();
                    callback.call(null);
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

            }
        }).start();
    }
}