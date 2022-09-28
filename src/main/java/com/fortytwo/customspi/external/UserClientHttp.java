package com.fortytwo.customspi.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fortytwo.customspi.DemoUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.WebApplicationException;
import java.util.List;

@Slf4j
public class UserClientHttp implements UserClient{

    private final CloseableHttpClient httpClient;
    private final String baseUrl;

    public UserClientHttp(KeycloakSession session, ComponentModel model) {
        this.httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
        this.baseUrl = model.get(Constants.BASE_URL);
    }

    @Override
    @SneakyThrows
    public DemoUser getUser(String id) {
        String url = String.format("%s/%s", baseUrl, id);
        SimpleHttp.Response response = SimpleHttp.doGet(url, httpClient).asResponse();
        if (response.getStatus() == 404) {
            throw new WebApplicationException(response.getStatus());
        }
        return response.asJson(DemoUser.class);
    }

    @Override
    @SneakyThrows
    public List<DemoUser> getAllUsers() {
        String url = String.format("%s/", baseUrl);
        SimpleHttp.Response response = SimpleHttp.doGet(url, httpClient).asResponse();
        if (response.getStatus() == 404) {
            throw new WebApplicationException(response.getStatus());
        }
        return response.asJson(new TypeReference<>() {});
    }


}
