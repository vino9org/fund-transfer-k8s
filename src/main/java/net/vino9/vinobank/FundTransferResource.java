package net.vino9.vinobank;

import net.vino9.vinobank.client.CoreBankingService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/transfers")
public class FundTransferResource {

    private static final Logger logger = Logger.getLogger(FundTransferResource.class);

    @Inject
    @RestClient
    CoreBankingService coreBankingService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTransfer(FundTransferRequest request) {
        logger.infof("Received request %s", request);

        if (request.getCreditAccountId().isEmpty()) {
            return Response.status(422, "credit_account_id is required").build();
        }

        // call core banking to perform transfer
        Response coreBankingResponse = coreBankingService.invokeFundTransfer(request);
        switch (coreBankingResponse.getStatus()) {
            case 200:
                String coreBankingResponseBody = coreBankingResponse.readEntity(String.class);
                logger.infof("core banking response body is %s", coreBankingResponseBody);
                return Response.accepted(coreBankingResponseBody).build();

            case 422:
                return Response.status(422,
                        String.format("request validation error, message=%s",
                                coreBankingResponse.readEntity(String.class))
                ).build();

            default:
                return Response.status(500,
                        String.format("technical error calling core banking. code=%d, message=%s",
                                coreBankingResponse.getStatus(), coreBankingResponse.readEntity(String.class))
                ).build();
        }
    }
}