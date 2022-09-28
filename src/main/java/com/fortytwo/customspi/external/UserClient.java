package com.fortytwo.customspi.external;

import com.fortytwo.customspi.DemoUser;

import java.util.List;

public interface UserClient {

    DemoUser getUser(String id);
    List<DemoUser> getAllUsers();
}
