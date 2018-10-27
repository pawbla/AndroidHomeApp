package service.rpi.com.piramidka.webservice;

import android.content.Context;
import android.view.Menu;

/**
 *  Interface provides method to connect with webService
 */
public interface WebServiceConnectorInterface {

    String getStatusCode ();

    String getReceivedMessage ();

    String prepareUserName ();

    void connect (String... data);

    void updateConnectionIcon (Menu menu);
}
