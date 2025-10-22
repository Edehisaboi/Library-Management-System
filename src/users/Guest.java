package users;

import authentication.AccessLevel;


public class Guest {

    public AccessLevel getAccessLevel() {
        return AccessLevel.GUEST;
    }

    @Override
    public String toString() {
        return "Role: " + getAccessLevel() + "\nStatus: Limited Access (Guest User)";
    }
}
