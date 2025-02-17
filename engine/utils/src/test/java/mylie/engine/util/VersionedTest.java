package mylie.engine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionedTest {

    @Test
    void testConstructor(){
        Versioned<String> versioned = new Versioned<>("test");
        assertEquals("test",versioned.value());
        assertEquals(0,versioned.version());
    }

    @Test
    void testEmptyConstructor(){
        Versioned<String> versioned = new Versioned<>();
        assertNull(versioned.value());
        assertEquals(0,versioned.version());
    }

    @Test
    void testReferenceUpdate(){
        Versioned<String> versioned = new Versioned<>("test");
        Versioned.Ref<String> ref1 = versioned.ref();
        assertEquals("test",ref1.value(false));
        assertEquals("test",ref1.value(true));
        assertFalse(ref1.requiresUpdate());
        versioned.value("test2");
        assertTrue(ref1.requiresUpdate());
        assertEquals("test",ref1.value(false));
        assertEquals("test2",ref1.value(true));
    }

    @Test
    void testUpdateValue(){
        Versioned<String> versioned = new Versioned<>("test");
        assertEquals("test",versioned.value());
        assertEquals(0,versioned.version());
        assertDoesNotThrow(() ->versioned.value("test2",1));
        assertEquals("test2",versioned.value());
        assertEquals(1,versioned.version());
        assertThrows(AssertionError.class, () ->versioned.value("test3",1));
    }
}