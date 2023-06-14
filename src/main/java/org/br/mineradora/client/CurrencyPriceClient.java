package org.br.mineradora.client;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.LinkedHashMap;


@Path("/last")
@RegisterRestClient
public interface CurrencyPriceClient {



    @GET
    @Path("/{pair}")
    CurrencyPriceDTO getPriceByPair(@PathParam("pair") String pair);

    @GET
    @Path("/{pair}")
    LinkedHashMap getPriceByPairObject(@PathParam("pair") String pair);
}
