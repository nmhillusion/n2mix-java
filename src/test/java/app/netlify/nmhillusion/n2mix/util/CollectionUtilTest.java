package app.netlify.nmhillusion.n2mix.util;

import app.netlify.nmhillusion.n2mix.helper.log.LogHelper;
import app.netlify.nmhillusion.n2mix.type.ChainList;
import app.netlify.nmhillusion.n2mix.type.ChainMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilTest {

    @Test
    void isNullOrEmpty() {
        assertTrue(CollectionUtil.isNullOrEmpty((Map<?, ?>) null));
        assertTrue(CollectionUtil.isNullOrEmpty(new ArrayList<>()));
        assertTrue(CollectionUtil.isNullOrEmpty(new HashSet<>()));
        assertTrue(CollectionUtil.isNullOrEmpty(new HashMap<>()));

        assertFalse(CollectionUtil.isNullOrEmpty(new ChainList<>().chainAdd("abc")));
        assertFalse(CollectionUtil.isNullOrEmpty(new ChainMap<>().chainPut("name", "abc")));
    }

    @Test
    void isNullOrEmptyArgv() {
        assertTrue(CollectionUtil.isNullOrEmptyArgv());
        assertFalse(CollectionUtil.isNullOrEmptyArgv("b"));
        assertFalse(CollectionUtil.isNullOrEmptyArgv(1));
    }

    @Test
    void getOrDefaultEmpty() {
        assertNotNull(CollectionUtil.getOrDefaultEmpty(null));
        assertNotNull(CollectionUtil.getOrDefaultEmpty(new ArrayList<>()));
    }

    @Test
    void getOrDefaultEmptyAgrv() {
        assertNotNull(CollectionUtil.getOrDefaultEmptyArgv());
        final Iterable<Integer> orDefaultEmptyAgrv2 = CollectionUtil.getOrDefaultEmptyArgv(1, 2, 3);
        assertNotNull(orDefaultEmptyAgrv2);

        for (Integer integer_ : orDefaultEmptyAgrv2) {
            LogHelper.getLogger(this).info("item: " + integer_);
        }
    }

    @Test
    void getFirstOfListAgrv() {
        assertNull(CollectionUtil.getFirstOfListArgv());
        assertEquals(2, CollectionUtil.getFirstOfListArgv(2, 3, 4, 5));
        assertNotEquals(3, CollectionUtil.getFirstOfListArgv(1, 2, 3, 4));
    }

    @Test
    void getFirstOfList() {
        assertNull(CollectionUtil.getFirstOfList(new ArrayList<>()));
        assertEquals(2, CollectionUtil.getFirstOfList(new ChainList<Integer>()
                        .chainAdd(2)
                        .chainAdd(3)
                )
        );
        assertNotEquals(3, CollectionUtil.getFirstOfList(new ChainList<Integer>()
                .chainAdd(1)
                .chainAdd(2)
                .chainAdd(3)
        ));
    }

    @Test
    void listFromIterable() {
        final ChainList<String> list_ = new ChainList<String>().chainAdd("a").chainAdd("b");
        assertEquals(
                list_,
                CollectionUtil.listFromIterator(list_.iterator())
        );

        final ChainList<String> list2_ = new ChainList<String>().chainAdd("a").chainAdd("b");
        assertEquals(
                list_,
                CollectionUtil.listFromIterable(list2_)
        );

        final ChainList<String> list3_ = new ChainList<String>().chainAdd("c").chainAdd("b");
        assertNotEquals(
                list_,
                CollectionUtil.listFromIterable(list3_)
        );
    }
}