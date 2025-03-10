package ait.de.model;

import lombok.Getter;
import lombok.EqualsAndHashCode;

/**
 * Represents a restaurant table with a unique ID, capacity, and VIP status.
 */
@Getter
@EqualsAndHashCode(of = "tableId")
public class Table {
    private final int tableId;  // Unique table ID (1-10)
    private final int capacity; // Number of seats (matches tableId)
    private final boolean isVip; // VIP status of the table

    /**
     * Private constructor to enforce controlled table creation.
     *
     * @param tableId  Unique table ID (1-10).
     * @param isVip    Defines whether the table is VIP.
     */
    private Table(int tableId, boolean isVip) {
        this.tableId = tableId;
        this.capacity = tableId; // Capacity is equal to tableId
        this.isVip = isVip;
    }

    /**
     * Custom string representation of Table.
     *
     * @return Formatted string with table details.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("tableId=").append(tableId);
        sb.append(", capacity=").append(capacity);
        sb.append(", isVip=").append(isVip);
        sb.append('}');
        return sb.toString();
    }
}