package net.vino9.vinobank.client;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import net.vino9.vinobank.FundTransferRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.faulttolerance.Retry;

@Path("/core-banking")
@RegisterRestClient(configKey="core-banking-api")
public interface CoreBankingService {
    @POST
    @Path("/local-transfers")
    @Retry(maxRetries = 3, delay=200, jitter=100)
    Response invokeFundTransfer(FundTransferRequest payloadToSend);
}
