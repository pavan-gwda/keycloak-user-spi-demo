package com.fortytwo.customspi;

import com.fortytwo.customspi.external.Constants;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class DemoUserStorageProviderFactory implements UserStorageProviderFactory<DemoUserStorageProvider> {

    @Override
    public DemoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // here you can setup the user storage provider, initiate some connections, etc.
        return new DemoUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return "42-user-provider-with-api";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property(Constants.BASE_URL,"Base url", "Base url of ur user management end point", ProviderConfigProperty.STRING_TYPE, "localhost:9000/users", null)
                .build();
    }
}
