package engine.users;

import engine.schema.generated.TransPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final Map<String, User> usersSet;

    public UserManager() { usersSet = new HashMap<>(); }

    public synchronized void addUser(String username, int type) { usersSet.put(username, new User(username, type)); }

    public synchronized void removeUser(String username) { usersSet.remove(username); }

    public synchronized Map<String, User> getUsers() { return Collections.unmodifiableMap(usersSet); }

    public boolean isUserExists(String username) { return usersSet.containsKey(username); }
}
