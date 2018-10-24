package service.rpi.com.piramidka.webservice;


import android.content.Context;
import android.widget.Toast;

/**
 * Class for registered user
 */
public class RegisterUser_WebServiceConnector extends WebServiceConnector {
    private Context context;

    public RegisterUser_WebServiceConnector(Context context) {
        super(context);
        this.context = context;
    }

    public void showToastPopup () {
        String msg;
        //An exception has occured durig getting data in Async Task
        if (response.get(0) == null) {
            msg = "Problem z połączeniem podczas rejestracji użytkownika.";
        } else {
            switch (response.get(0)) {
                //Response code 200 - user registered successfully
                case "200":
                    msg = "Użytkownik został zarejestrowany.";
                    break;
                //User probably add into db
                case "409":
                    msg = "Nie można zarejestrować użytkownika.";
                    break;
                //No internet connection with serwice
                case "0":
                    msg = "Nie można nawiązać połączenia z serwisem.";
                    break;
                //Other response code
                default:
                    msg = "Wystąpił problem z serwisem podczas rejestracji użytkownika. Response code " + response;
            }
        }
        if (msg != null) {
            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }
}
