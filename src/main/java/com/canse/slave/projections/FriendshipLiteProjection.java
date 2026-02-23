package com.canse.slave.projections;

public interface FriendshipLiteProjection {
    Long getId();

    // IMPORTANT: comme ton champ s'appelle "isChecked"
    // la bonne méthode côté projection est généralement getChecked() ou isChecked()
    // mais pour être 100% sûr avec Spring Data, je te conseille isChecked()
    boolean isChecked();

    UserLite getRequester();
    UserLite getReceiver();

    interface UserLite {
        Long getId();
        String getUsername();
    }
}