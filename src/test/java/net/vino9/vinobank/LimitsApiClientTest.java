package net.vino9.vinobank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import net.vino9.vinobank.client.LimitsApiClient;
import net.vino9.vinobank.client.NewLimitResponse;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LimitsApiClientTest {

    public static final String TEST_CUSTOMER_ID_1 = "1122334455";

    @Inject
    LimitsApiClient client;

    @Test
    public void testNewLimitsRequest() throws Exception {
        NewLimitResponse response = client.newRequest(TEST_CUSTOMER_ID_1, 1000.12);
        assertNotNull(response.getRequestId());
        assertEquals(TEST_CUSTOMER_ID_1, response.getCustomerId());
    }

    @Test
    public void testDeleteLimitsRequest() throws Exception {
        NewLimitResponse response = client.newRequest(TEST_CUSTOMER_ID_1, 2000.33);
        assertNotNull(response.getRequestId());

        // failed request will throw exception
        client.deleteRequest(TEST_CUSTOMER_ID_1, response.getRequestId());
    }

}
