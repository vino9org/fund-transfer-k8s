package net.vino9.vinobank;

import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

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
        request.setCustomerId("ABCD");
        request.setAccountId("1123");
        request.setCreditAccountId("2222");
        request.setCurrency("SGD");
        request.setAmount(1000.10);
        request.setMemo("just testing");
        request.setTransactionDate("2022-03-13");
        return request;
    }
}