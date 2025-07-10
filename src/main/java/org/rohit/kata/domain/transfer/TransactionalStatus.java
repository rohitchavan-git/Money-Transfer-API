package org.rohit.kata.domain.transfer;

public enum TransactionalStatus {
    SUCCEED("SUCCEED"),
    FAILED("FAILED");

    private final String name;

    TransactionalStatus(String label) {
        this.name = label;
    }

    public String getName() {
        return name;
    }
}
