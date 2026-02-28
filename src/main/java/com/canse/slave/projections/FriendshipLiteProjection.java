package com.canse.slave.projections;

public interface FriendshipLiteProjection {
    Long getId();

    boolean isChecked();

    UserLite getRequester();
    UserLite getReceiver();

    interface UserLite {
        Long getId();
        String getUsername();
    }
}