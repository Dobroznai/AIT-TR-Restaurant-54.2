package ait.de.model;

/*
1. Table (Столик)
   - Описание: Представляет информацию о столике в ресторане.
   - Поля:
     - int tableId — идентификатор столика.
     - int capacity — вместимость столика.
     - boolean isVip — является ли столик VIP.
   - Методы:
     - Конструктор и геттеры/сеттеры.
 */
public class Table {
    private int tableId;
    private int capacity;
    private boolean isVip;

    public Table(int tableId, int capacity, boolean isVip) {
        this.tableId = tableId;
        this.capacity = capacity;
        this.isVip = isVip;
    }

    // Геттеры и сеттеры

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("tableId=").append(tableId);
        sb.append(", capacity=").append(capacity);
        sb.append(", isVip=").append(isVip);
        sb.append('}');
        return sb.toString();
    }

} // klass ended
