package com.demo.enums;

public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String direction;

    SortDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public static SortDirection fromString(String value) {
        for (SortDirection sortDirection : SortDirection.values()) {
            if (sortDirection.direction.equalsIgnoreCase(value)) {
                return sortDirection;
            }
        }
        throw new IllegalArgumentException("Invalid sort direction: " + value);
    }
}

