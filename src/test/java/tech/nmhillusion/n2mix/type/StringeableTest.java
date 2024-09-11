package tech.nmhillusion.n2mix.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.model.Book;
import tech.nmhillusion.n2mix.model.StaffEntity;
import tech.nmhillusion.n2mix.model.StoryBook;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
class StringeableTest {

    @Test
    void testSimpleCase() {
        final Book book_ = new Book()
                .setAuthor("Ahraham")
                .setPrice(105)
                .setTitle("The way of the monk");

        getLogger(this).info("toString of the book: " + book_);
    }

    private void logForRecursiveInstances() {
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

        getLogger(this)
                .info("staff_1 = %s".formatted(staff_1));
        getLogger(this)
                .info("staff_2 = %s".formatted(staff_2));
        getLogger(this)
                .info("staff_3 = %s".formatted(staff_3));
    }


    @Test
    void testToString() {
        Assertions.assertDoesNotThrow(this::logForRecursiveInstances);
    }

    @Test
    void testWithThreads() {
        Assertions.assertDoesNotThrow(() -> {
            final ExecutorService executorService = Executors.newWorkStealingPool();
            final int MAX_THREADS = 100;

            for (int threadIdx = 0; threadIdx < MAX_THREADS; ++threadIdx) {
                executorService.submit(this::logForRecursiveInstances);
            }
        });
    }

    @Test
    void testWithHierarchyClass() {
        Assertions.assertDoesNotThrow(() -> {
            final StoryBook storyBook = new StoryBook();
            storyBook
                    .setChapterNo("1.1")
                    .setAuthor("Ahraham")
                    .setPrice(105)
                    .setTitle("The way of the monk");

            getLogger(this).info("toString of the book: " + storyBook);
        });
    }
}