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
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            assertEquals(i, table.getTableId(), "ID стола должен быть " + i);
            assertEquals(i, table.getCapacity(), "Вместимость стола должна быть " + i);
        }
    }

    @Test
    void testShouldUpdateVipStatusForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            table.setVip(true);
            assertTrue(table.isVip(), "Стол с ID " + i + " должен быть VIP после вызова setVip(true)");

            table.setVip(false);
            assertFalse(table.isVip(), "Стол с ID " + i + " не должен быть VIP после вызова setVip(false)");
        }
    }

    @Test
    void testShouldNotUpdateTableIdWhenSetTableIdIsCalledWithInvalidId() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            table.setTableId(-1); // Невалидный ID стола
            assertEquals(i, table.getTableId(), "ID стола с " + i + " должен остаться неизменным для некорректного значения");
        }
    }

    @Test
    void testShouldUpdateCapacityWhenSetCapacityIsCalledWithValidValue() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            // Попытка установить вместимость, которая не превышает ID стола
            table.setCapacity(i); // Вместимость равна ID
            assertEquals(i, table.getCapacity(), "Вместимость стола с ID " + i + " должна быть " + i);

            // Попытка установить вместимость больше, чем ID стола, должна быть отклонена
            table.setCapacity(i + 1); // Вместимость больше ID
            assertEquals(i, table.getCapacity(), "Вместимость стола с ID " + i + " не должна превышать " + i);
        }
    }

    @Test
    void testShouldNotUpdateCapacityWhenSetCapacityIsCalledWithInvalidValue() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            // Попытка установить некорректную вместимость
            table.setCapacity(-2); // Невалидная вместимость
            assertEquals(i, table.getCapacity(), "Вместимость стола с ID " + i + " должна остаться неизменной для некорректного значения (-2)");

            // Попытка установить вместимость больше ID
            table.setCapacity(i + 1); // Невалидная вместимость (больше ID)
            assertEquals(i, table.getCapacity(), "Вместимость стола с ID " + i + " должна остаться неизменной для некорректного значения (больше ID)");
        }
    }

    @Test
    void testShouldReturnCorrectStringRepresentationWhenToStringIsCalledForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            String expected = "Table{tableId=" + i + ", capacity=" + i + ", isVip=false}";
            assertEquals(expected, table.toString(), "Строковое представление стола с ID " + i + " неверно");
        }
    }

    @Test
    void testShouldReturnTrueWhenEqualsIsCalledWithSameObjectForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = table;
            assertTrue(table.equals(anotherTable), "Стол с ID " + i + " должен быть равен самому себе");
        }
    }

    @Test
    void testShouldReturnTrueWhenEqualsIsCalledWithObjectHavingSameTableIdForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = new Table(i, i, false); // Тот же ID, та же вместимость
            assertTrue(table.equals(anotherTable), "Столы с ID " + i + " должны быть равны, так как у них одинаковый ID");
        }
    }

    @Test
    void testShouldReturnFalseWhenEqualsIsCalledWithObjectHavingDifferentTableIdForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = new Table(i + 1, i, false); // Разный ID
            assertFalse(table.equals(anotherTable), "Столы с ID " + i + " не должны быть равны, так как у них разные ID");
        }
    }

    @Test
    void testShouldReturnSameHashCodeWhenHashCodeIsCalledOnObjectsWithSameTableIdForMultipleTables() {
        for (int i = 1; i <= 10; i++) {
            table = new Table(i, i, false); // Стол с ID = i и вместимостью = i

            Table anotherTable = new Table(i, i, false); // Тот же ID, та же вместимость
            assertEquals(table.hashCode(), anotherTable.hashCode(), "Столы с одинаковым ID " + i + " должны иметь одинаковый hashCode");
        }
    }
}