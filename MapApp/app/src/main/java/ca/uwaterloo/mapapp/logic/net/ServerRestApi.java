package ca.uwaterloo.mapapp.logic.net;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import ca.uwaterloo.mapapp.logic.net.services.IServerRestService;
import ca.uwaterloo.mapapp.shared.ICallback;
import ca.uwaterloo.mapapp.shared.IRequestor;
import ca.uwaterloo.mapapp.shared.objects.event.EventImage;
import ca.uwaterloo.mapapp.shared.objects.event.EventNote;
import ca.uwaterloo.mapapp.shared.objects.event.EventRanking;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class ServerRestApi {
    private static HashMap<String, Object> localCacheMap = new HashMap<>();

    private static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(IServerRestService.API_ENDPOINT)
            .setConverter(new GsonConverter(gson))
            .build();

    private static IServerRestService service = restAdapter.create(IServerRestService.class);

    public static void requestBuildings(final ICallback callback) {
        requestData(callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEvents();
            }
        });
    }

    public static void requestEvents(final ICallback callback) {
        requestData(callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEvents();
            }
        });
    }

    public static void requestEventImages(final ICallback callback, final Integer eventId) {
        requestData(callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventImages(eventId);
            }
        });
    }

    public static void addOrSetEventImage(final ICallback callback, final EventImage image) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Response result = service.setEventImage(image);
                return result.getStatus() == 200;
            }
        });
    }

    public static void deleteEventImage(final ICallback callback, final EventImage image) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Response result = service.deleteEventImage(image.getId());
                return result.getStatus() == 200;
            }
        });
    }

    public static void requestEventTimes(final ICallback callback, final Integer eventId) {
        requestData(callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventTimes(eventId);
            }
        });
    }

    public static void requestEventNotes(final ICallback callback, final Integer eventId) {
        requestData(callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventNotes(eventId);
            }
        });
    }

    public static void requestEventRankings(final ICallback callback, final Integer eventId) {
        requestData(callback, new IRequestor() {
            @Override
            public Object request() {
                return service.getEventRankings(eventId);
            }
        });
    }

    public static void addOrSetEventNote(final ICallback callback, final EventNote note) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Response result = service.setEventNote(note);
                return result.getStatus() == 200;
            }
        });
    }

    public static void addOrSetEventRanking(final ICallback callback, final EventRanking ranking) {
        sendData(callback, new IRequestor() {
            @Override
            public Object request() {
                Log.i("ServerRestApi", gson.toJson(ranking, EventRanking.class));
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

    private static void requestData(final ICallback callback, final IRequestor requestor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object result = requestor.request();
                    callback.call(result);
                } catch (Exception e) {
                    Log.e("ServerRestApi", "Exception:" , e);
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static void sendData(final ICallback callback, final IRequestor requestor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestor.request();
                    callback.call(null);
                } catch (Exception e) {
                    Log.e("ServerRestApi", "Exception:" , e);
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
