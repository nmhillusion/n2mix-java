package tech.nmhillusion.n2mix.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.model.StaffEntity;

import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
class StringeableTest {

    @Test
    void testToString() {
        Assertions.assertDoesNotThrow(() -> {
            final StaffEntity staff_1 = new StaffEntity()
                    .setId(1)
                    .setName("Nguyen Van A")
                    .setDepartment("IT");

            final StaffEntity staff_2 = new StaffEntity()
                    .setId(2)
                    .setName("Le Van B")
                    .setDepartment("IT");
            final StaffEntity staff_3 = new StaffEntity()
                    .setId(3)
                    .setName("Tran Tran")
                    .setDepartment("BD");

            staff_1.setManager(staff_3);
            staff_2.setManager(staff_3);
            staff_3.setManager(staff_1);

            staff_1.setColleagues(
                    List.of(
                            staff_2, staff_3
                    )
            );

            staff_2.setColleagues(
                    List.of(
                            staff_1, staff_3, staff_2
                    )
            );

            staff_3.setColleagues(
                    List.of(
                            staff_1, staff_3
                    )
            );

            LogHelper.getLogger(this)
                    .info("staff_1 = %s".formatted(staff_1));
            LogHelper.getLogger(this)
                    .info("staff_2 = %s".formatted(staff_2));
            LogHelper.getLogger(this)
                    .info("staff_3 = %s".formatted(staff_3));

        });
    }
}