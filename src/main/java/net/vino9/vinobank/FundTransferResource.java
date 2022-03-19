package net.vino9.vinobank;

import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.vino9.vinobank.client.ApiGatewayException;
import net.vino9.vinobank.client.CoreBankingService;
import net.vino9.vinobank.client.LimitsApiClient;
import net.vino9.vinobank.client.NewLimitResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/transfers")
public class FundTransferResource {

    private static final Logger logger = Logger.getLogger(FundTransferResource.class);

    @Inject
    LimitsApiClient limitsApiClient;

    @Inject
    @RestClient
    CoreBankingService coreBankingService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTransfer(FundTransferRequest request) {
        logger.infof("Received request %s", request);

        if (request.getCreditAccountId().isEmpty()) {
            return Response.status(400, "credit_account_id is required").build();
        }

        // check if the transfer exceed per customer limit
        NewLimitResponse limitsApiResponse;
        try {
            limitsApiResponse = limitsApiClient.newRequest(request.getCustomerId(), request.getAmount());
        } catch (ApiGatewayException e) {
            return Response.status(500,
                String.format("unable to reserve transfer limit, error message = %s", e.getMessage())
            ).build();
        }

        // call core banking to perform transfer
        Response coreBankingResponse = coreBankingService.invokeFundTransfer(request);
        if (coreBankingResponse.getStatus() != 200) {
            return Response.status(500,
                String.format("error from core banking service: %d", coreBankingResponse.getStatus())
            ).build();
        }

        String coreBankingResponseBody = coreBankingResponse.readEntity(String.class);
        logger.infof("core banking response body is %s", coreBankingResponseBody);

        return Response.accepted(coreBankingResponseBody).build();
    }
}