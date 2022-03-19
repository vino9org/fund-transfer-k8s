package net.vino9.vinobank.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import net.vino9.vinobank.FundTransferRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/core-banking")
@RegisterRestClient(configKey="core-banking-api")
public interface CoreBankingService {
    @POST
    @Path("/local-transfers")
    Response invokeFundTransfer(FundTransferRequest payloadToSend);
}
