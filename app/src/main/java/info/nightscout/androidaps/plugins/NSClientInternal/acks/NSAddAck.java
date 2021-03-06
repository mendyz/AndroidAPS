package info.nightscout.androidaps.plugins.NSClientInternal.acks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.plugins.NSClientInternal.events.EventNSClientRestart;
import info.nightscout.androidaps.plugins.NSClientInternal.services.NSClientService;
import io.socket.client.Ack;

/**
 * Created by mike on 29.12.2015.
 */
public class NSAddAck implements Ack {
    private static Logger log = LoggerFactory.getLogger(NSAddAck.class);
    public String _id = null;
    public String nsClientID = null;
    public JSONObject json = null;
    public void call(Object...args) {
        // Regular response
        try {
            JSONArray responsearray = (JSONArray) (args[0]);
            JSONObject response = null;
            if (responsearray.length()>0) {
                    response = responsearray.getJSONObject(0);
                _id = response.getString("_id");
                json = response;
                if (response.has("NSCLIENT_ID")) {
                    nsClientID = response.getString("NSCLIENT_ID");
                }
            }
            MainApp.bus().post(this);
            return;
        } catch (Exception e) {
        }
        // Check for not authorized
        try {
            JSONObject response = (JSONObject) (args[0]);
            if (response.has("result")) {
                _id = null;
                if (response.getString("result").contains("Not")) {
                    MainApp.bus().post(new EventNSClientRestart());
                    return;
                }
                log.debug("DBACCESS " + response.getString("result"));
            }
            return;
        } catch (Exception e) {
            log.error("Unhandled exception", e);
        }
    }
}