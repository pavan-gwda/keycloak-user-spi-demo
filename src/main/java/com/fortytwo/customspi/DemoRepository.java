package com.fortytwo.customspi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class DemoRepository {

    private final List<DemoUser> users;

    DemoRepository() {
        Long created = System.currentTimeMillis();
        List<String> roles = Collections.singletonList("stoneage");
        users = Arrays.asList(
                new DemoUser("1", "Fred", "lastname", true, created, roles),
                new DemoUser("2", "Wilma", "ls", true, created, roles),
                new DemoUser("3", "Pebbles", "ln", true, created, roles),
                new DemoUser("4", "Barney", "ls", true, created, roles),
                new DemoUser("5", "Betty", "ln", true, created, Collections.emptyList()),
                new DemoUser("6", "Bam Bam", "ln", false, created, Collections.emptyList())
        );

    }

    List<DemoUser> getAllUsers() {
        return users;
    }

    int getUsersCount() {
        return users.size();
    }

    DemoUser findUserById(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    DemoUser findUserByUsernameOrEmail(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }

    List<DemoUser> findUsers(String query) {
        return users.stream()
                .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
                .collect(Collectors.toList());
    }

    boolean validateCredentials(String username, String password) {
        return findUserByUsernameOrEmail(username).getPassword().equals(password);
    }

    boolean updateCredentials(String username, String password) {
        findUserByUsernameOrEmail(username).setPassword(password);
        return true;
    }

}
