package service.rpi.com.piramidka.webservice;

import android.content.Context;

/**
 *  Interface provides method to connect with webService
 */
public interface WebServiceConnectorInterface {

    String getStatusCode ();

    String getReceivedMessage ();

    String prepareUserName ();

    void showToastPopup ();

    void connect (String... data);
}
