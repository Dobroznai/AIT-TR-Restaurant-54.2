package ait.de.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testng.Assert.assertEquals;

public class TableTest {
    private Table table;

    @BeforeEach
    void setUp() {
        // Инициализация объекта table перед каждым тестом
    }

    @Test
    void testShouldReturnCorrectTableIdAndCapacityForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            assertEquals(i, table.getTableId(), "ID стола должен быть " + i);
            assertEquals(i, table.getCapacity(), "Вместимость стола должна быть " + i);
        }
    }

    @Test
    void testShouldReturnCorrectVipStatusForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            // Проверяем начальный статус VIP
            assertFalse(table.isVip(), "Стол с ID " + i + " не должен быть VIP по умолчанию");

            // Стол с VIP статусом
            table = new Table(i, true); // Стол с VIP статусом
            assertTrue(table.isVip(), "Стол с ID " + i + " должен быть VIP, если передан true");
        }
    }

    @Test
    void testShouldNotAllowChangingTableId() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            // ID стола не меняется, поэтому проверяем, что tableId остается неизменным
            assertEquals(i, table.getTableId(), "ID стола с " + i + " должен оставаться неизменным");
        }
    }

    @Test
    void testShouldReturnCorrectStringRepresentationWhenToStringIsCalledForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            String expected = "Table{tableId=" + i + ", capacity=" + i + ", isVip=false}";
            assertEquals(expected, table.toString(), "Строковое представление стола с ID " + i + " неверно");
        }
    }

    @Test
    void testShouldReturnTrueWhenEqualsIsCalledWithSameObjectForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = table;
            assertTrue(table.equals(anotherTable), "Стол с ID " + i + " должен быть равен самому себе");
        }
    }

    @Test
    void testShouldReturnTrueWhenEqualsIsCalledWithObjectHavingSameTableIdForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = new Table(i, false); // Тот же ID, та же вместимость
            assertTrue(table.equals(anotherTable), "Столы с ID " + i + " должны быть равны, так как у них одинаковый ID");
        }
    }

    @Test
    void testShouldReturnFalseWhenEqualsIsCalledWithObjectHavingDifferentTableIdForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = new Table(i + 1, false); // Разный ID
            assertFalse(table.equals(anotherTable), "Столы с ID " + i + " не должны быть равны, так как у них разные ID");
        }
    }

    @Test
    void testShouldReturnSameHashCodeWhenHashCodeIsCalledOnObjectsWithSameTableIdForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = new Table(i, false); // Тот же ID, та же вместимость
            assertEquals(table.hashCode(), anotherTable.hashCode(), "Столы с одинаковым ID " + i + " должны иметь одинаковый hashCode");
        }
    }
}