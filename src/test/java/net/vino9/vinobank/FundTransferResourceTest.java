package net.vino9.vinobank;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FundTransferResourceTest {

    @Test
    public void testNewFundTransfer() {
        var request = newRequest();

        // @formatter:off
        given()
            .body(request)
            .header("Content-Type", MediaType.APPLICATION_JSON)
        .when()
            .post("/transfers")
        .then()
            .statusCode(202)
            .body("transaction_id", notNullValue());
        // @formatter:on
    }

    @Test
    public void testInvalidFundTransfer() {
        var request = newRequest();
        request.setCreditAccountId("");

        // @formatter:off
        given()
            .body(request)
            .header("Content-Type", MediaType.APPLICATION_JSON)
        .when()
            .post("/transfers")
        .then()
            .statusCode(400);
        // @formatter:on
    }

    private FundTransferRequest newRequest() {
        FundTransferRequest request = new FundTransferRequest();
        // the following IDs needs to exist in the corebanking sim
        request.setCustomerId("CUS01FYS5XB5V2RBV52NYDEESNTP4");
        request.setAccountId("ACC01FYS5XB5VBJYNE91JQQ5BQZFB");
        request.setCreditAccountId("ACC01FYS5XB5V2BGWBJ42A66RNT32");
        request.setCurrency("SGD");
        request.setAmount(100.10);
        request.setMemo("just testing");
        request.setTransactionDate("2022-03-13");
        return request;
    }
}