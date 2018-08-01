package service.rpi.com.piramidka;


import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class WebServiceHandlerTest {

    private MockContext context;

    @Before
    public void precondition () {
        context = mock(MockContext.class);
    }

    @Test
    public void startWebServiceHandler () {
        new WebServiceHandler(context).execute();
    }
}
