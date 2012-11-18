/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra;

import static javax.time.calendrical.ChronoField.HOUR_OF_DAY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeAccessor;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test HourOfDay.
 */
@Test
public class TestHourOfDay {

    private static final int MAX_LENGTH = 23;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(HourOfDay.class));
        assertTrue(Comparable.class.isAssignableFrom(HourOfDay.class));
        assertTrue(WithAdjuster.class.isAssignableFrom(HourOfDay.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        HourOfDay test = HourOfDay.of(1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    public void test_immutable() {
        Class<HourOfDay> cls = HourOfDay.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            } else {
                assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            }
        }
    }

    //-----------------------------------------------------------------------
    public void test_factory_int() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i);
            assertEquals(test.getValue(), i);
            assertEquals(HourOfDay.of(i), test);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_int_minuteTooLow() {
        HourOfDay.of(-1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_int_hourTooHigh() {
        HourOfDay.of(24);
    }

    //-----------------------------------------------------------------------
    public void test_factory_AmPmInt() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i < 12 ? AmPm.AM : AmPm.PM, i % 12);
            assertEquals(test.getValue(), i);
            assertEquals(HourOfDay.of(i), test);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_AmPmInt_hourTooLow() {
        HourOfDay.of(AmPm.AM, -1);
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_AmPmInt_hourTooHigh() {
        HourOfDay.of(AmPm.AM, 12);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_AmPmInt_nullAmPm() {
        HourOfDay.of((AmPm) null, 1);
    }

    //-----------------------------------------------------------------------
    public void test_factory_CalendricalObject() {
        LocalTime time = LocalTime.of(0, 20);
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.from(time);
            assertEquals(test.getValue(), i);
            time = time.plusHours(1);
        }
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_factory_Calendrical_noDerive() {
        HourOfDay.from(LocalDate.of(2012, 3, 2));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_CalendricalObject_null() {
        HourOfDay.from((DateTimeAccessor) null);
    }

    //-----------------------------------------------------------------------
    public void test_getField() {
        assertSame(HourOfDay.of(1).getField(), HOUR_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // adjustTime()
    //-----------------------------------------------------------------------
    public void test_adjustTime() {
        LocalTime base = LocalTime.of(0, 20);
        LocalTime expected = base;
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i);
            assertEquals(test.doWithAdjustment(base), expected);
            expected = expected.plusHours(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustTime_nullLocalTime() {
        HourOfDay test = HourOfDay.of(1);
        test.doWithAdjustment((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // getAmPm()
    //-----------------------------------------------------------------------
    public void test_getAmPm() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i);
            assertEquals(test.getAmPm(), i < 12 ? AmPm.AM : AmPm.PM);
        }
    }

    //-----------------------------------------------------------------------
    // getHourOfAmPm()
    //-----------------------------------------------------------------------
    public void test_getHourOfAmPm() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i);
            assertEquals(test.getHourOfAmPm(), i % 12);
        }
    }

    //-----------------------------------------------------------------------
    // getClockHourOfAmPm()
    //-----------------------------------------------------------------------
    public void test_getClockHourOfAmPm() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i);
            assertEquals(test.getClockHourOfAmPm(), (i % 12 == 0 ? 12 : i % 12));
        }
    }

    //-----------------------------------------------------------------------
    // getClockHourOfDay()
    //-----------------------------------------------------------------------
    public void test_getClockHourOfDay() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay test = HourOfDay.of(i);
            assertEquals(test.getClockHourOfDay(), (i == 0 ? 24 : i));
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay a = HourOfDay.of(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                HourOfDay b = HourOfDay.of(j);
                if (i < j) {
                    assertEquals(a.compareTo(b), -1);
                    assertEquals(b.compareTo(a), 1);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1);
                    assertEquals(b.compareTo(a), -1);
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullHourOfDay() {
        HourOfDay doy = null;
        HourOfDay test = HourOfDay.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay a = HourOfDay.of(i);
            for (int j = 0; j <= MAX_LENGTH; j++) {
                HourOfDay b = HourOfDay.of(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    public void test_equals_nullHourOfDay() {
        HourOfDay doy = null;
        HourOfDay test = HourOfDay.of(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        HourOfDay test = HourOfDay.of(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = 0; i <= MAX_LENGTH; i++) {
            HourOfDay a = HourOfDay.of(i);
            assertEquals(a.toString(), "HourOfDay=" + i);
        }
    }

}
