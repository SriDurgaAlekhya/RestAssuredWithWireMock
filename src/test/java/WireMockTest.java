

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.joda.time.*;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time;


public class WireMockTest {
    String BASE_URL = "http://dummy.restapiexample.com/";
    MaxEmpoyee employee = new MaxEmpoyee();
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    @Test
    public void wireMock() throws InterruptedException, IOException {

        WireMockServer wireMockServer = new WireMockServer(options().port(9091));
        //connect to the server
        wireMockServer.start();

        Response response = RestAssured.get("http://localhost:9091/api/v1/employees");
        int mockStatusCode = response.getStatusCode();


        if (mockStatusCode == 200) { //if status code is 200 from stub

            String oldTime = response.getHeader("lastsyctime");
            Date currentDate = new Date();

            String currentTime = currentDate.toString();
            boolean syncRequired = isSyncRequired(oldTime, currentTime);


            if (syncRequired == true) {
                response = sync();

            }


        } else {
            response = sync();


        }

        Employee maxEmployee = employee.highestSalary(response);
        System.out.println(maxEmployee.getEmployee_salary());
        System.out.println(maxEmployee.getEmployee_name());


    }


    public Response sync() {

        Response responseFromRealUrl = RestAssured.get("http://dummy.restapiexample.com/api/v1/employees");
        int statuscodeFromRealUrl = responseFromRealUrl.getStatusCode();


        String dataFromRealUrl = responseFromRealUrl.getBody().asString();

        configureFor("localhost", 9091);
        Date currentDate = new Date();
        String currentTime = format.format(currentDate);

        stubFor(get(urlEqualTo("/api/v1/employees")).willReturn(aResponse().withStatus(statuscodeFromRealUrl).withBody(dataFromRealUrl).withHeader("lastsyctime", currentTime)));
        WireMock.saveAllMappings();
        return RestAssured.get("http://localhost:9091/api/v1/employees");


    }

    public boolean isSyncRequired(String oldTime, String currentTime) {
        try {

            Date d1 = format.parse(oldTime);
            Date d2 = format.parse(currentTime);
            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);
            int hoursDiff = Hours.hoursBetween(dt1, dt2).getHours() % 24;

            if (hoursDiff >= 24) {

                return true;

            }

        } catch (Exception e) {
            e.printStackTrace();


        }
        return false;
    }


}
