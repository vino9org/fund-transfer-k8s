package net.vino9.vinobank.client;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Consumes("application/json")
@Produces("application/json")
@Path("/")
@RegisterRestClient(configKey="limits-api")
@RegisterClientHeaders(CustomerLimitHeaderFactory.class)
public interface CustomerLimitService {
    @POST
    @Path("/customers/{cust_id}/limits")
    Response newRequest(@PathParam("cust_id") String customerId, Map<String, Object> payload);
}
