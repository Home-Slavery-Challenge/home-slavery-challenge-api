package com.canse.slave.projections;

public interface ChallengeLiteProjection {
    Long getId();
    String getName();
    OwnerInfo getOwner();

    interface OwnerInfo {
        String getUsername();
    }
}
