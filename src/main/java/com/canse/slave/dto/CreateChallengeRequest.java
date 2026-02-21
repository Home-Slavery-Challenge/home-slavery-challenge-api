package com.canse.slave.dto;

public record CreateChallengeRequest(String name, String owner, Long[] participants, String[] rewards,
                                     String[] tasks) {

}
