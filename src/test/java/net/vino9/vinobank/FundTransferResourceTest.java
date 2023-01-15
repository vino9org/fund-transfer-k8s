package net.vino9.vinobank;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

@QuarkusTest
class FundTransferResourceTest {

    @Test
    void testNewFundTransfer() {
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
    void testInvalidFundTransfer() {
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
        // the following IDs needs to exist in the core-banking-sim
        request.setReferenceId("UNIQ_1");
        request.setCustomerId("C11");
        request.setAccountId("A11");
        request.setCreditAccountId("A22");
        request.setCurrency("SGD");
        request.setAmount(100.10);
        request.setMemo("just testing");
        request.setTransactionDate("2022-03-13");
        return request;
    }
}