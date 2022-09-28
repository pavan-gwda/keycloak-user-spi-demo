package com.fortytwo.customspi;

import com.fortytwo.customspi.external.UserClient;
import com.fortytwo.customspi.external.UserClientHttp;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.*;
import java.util.stream.Collectors;

public class DemoUserStorageProvider implements UserStorageProvider,
        UserLookupProvider, UserQueryProvider, CredentialInputUpdater, CredentialInputValidator,
        UserRegistrationProvider {

    private final KeycloakSession session;
    private final ComponentModel model;
//    private final DemoRepository repository;
    private final UserClient userHttpClient;

    public DemoUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
//        this.repository = repository;
        this.userHttpClient = new UserClientHttp(session,model);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }


    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;

        return Objects.requireNonNull(userHttpClient.getAllUsers().stream().filter(lUser -> lUser.getUsername().equalsIgnoreCase(user.getUsername()) || lUser.getEmail().equalsIgnoreCase(user.getUsername())).findFirst().orElse(null))
                .getPassword().equals(cred.getChallengeResponse());

    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        findUserByUsernameOrEmail(user.getUsername()).setPassword(cred.getChallengeResponse());
        return true;
    }

    DemoUser findUserByUsernameOrEmail(String username) {
        return userHttpClient.getAllUsers()
                .stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }


    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        String externalId = StorageId.externalId(id);
        DemoUser foundUser = userHttpClient.getAllUsers().stream().filter(user -> user.getId().equals(externalId)).findFirst().orElse(null);
        assert foundUser != null;
        return new UserAdapter(session, realm, model, foundUser);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        DemoUser userFound = findUserByUsernameOrEmail(username);
        if (userFound != null) {
            return new UserAdapter(session, realm, model, userFound);
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return getUserByUsername(email, realm);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return  userHttpClient.getAllUsers().size();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        //before provisioning in keycloak call iam api
        return userHttpClient.getAllUsers()
                .stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return userHttpClient.getAllUsers()
                .stream()
                .filter(user -> user.getUsername().contains(search) || user.getEmail().contains(search))
                .collect(Collectors.toList())
                .stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return searchForUser(search, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return getUsers(realm, firstResult, maxResults);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return Collections.emptyList();
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        return false;
    }
}
