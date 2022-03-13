package net.vino9.vinobank;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/transfers")
public class FundTransferResource {
    private static final Logger logger = Logger.getLogger(FundTransferResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTransfer(FundTransferRequest request) {
        logger.infof("Received request %s", request);

        if (request.getCreditAccountId().isEmpty()) {
            return Response.status(400, "credit_account_id is required").build();
        }
        Map<String, String> result = Map.of("transaction_id", "123");
        return Response.accepted(result).build();
    }
}