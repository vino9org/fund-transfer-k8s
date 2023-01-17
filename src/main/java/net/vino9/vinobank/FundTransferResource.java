package net.vino9.vinobank;

import net.vino9.vinobank.client.CoreBankingService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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