/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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

import java.time.temporal.ChronoUnit;

/**
 * An abstract period measured in terms of a single field, such as days or seconds.
 * <p>
 * PeriodField is an immutable period that can only store years.
 * It is a type-safe way of representing a period in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The number of years may be queried using getYears().
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 *
 * @implSpec
 * This is an abstract class and must be implemented with care to ensure
 * other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 */
public abstract class AbstractPeriodField {

    //-----------------------------------------------------------------------
    /**
     * Constructs a new instance.
     */
    protected AbstractPeriodField() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of time in this period field.
     *
     * @return the amount of time of this period field
     */
    public abstract int getAmount();

    /**
     * Returns a new instance of the subclass with a different amount of time.
     *
     * @param amount  the amount of time to set in the new period field, may be negative
     * @return a new period field, never null
     */
    public abstract AbstractPeriodField withAmount(int amount);

    //-----------------------------------------------------------------------
    /**
     * Gets the unit defining the amount of time.
     *
     * @return the period unit, never null
     */
    public abstract ChronoUnit getUnit();

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified amount of time added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount of time to add, may be negative
     * @return the new period field plus the specified amount of time, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public AbstractPeriodField plus(int amount) {
        if (amount == 0) {
            return this;
        }
        return withAmount(Math.addExact(getAmount(), amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the specified amount of time taken away.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount of time to take away, may be negative
     * @return the new period minus the specified amount of time, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public AbstractPeriodField minus(int amount) {
        return withAmount(Math.subtractExact(getAmount(), amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the amount to multiply by, may be negative
     * @return the new period multiplied by the specified scalar, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public AbstractPeriodField multipliedBy(int scalar) {
        return withAmount(Math.multiplyExact(getAmount(), scalar));
    }

    /**
     * Returns a new instance with the amount divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the amount to divide by, may be negative
     * @return the new period divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     */
    public AbstractPeriodField dividedBy(int divisor) {
        if (divisor == 1) {
            return this;
        }
        return withAmount(getAmount() / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with the amount negated.
     *
     * @return the new period with a negated amount, never null
     * @throws ArithmeticException if the result overflows an int
     */
    public AbstractPeriodField negated() {
        return withAmount(safeNegate(getAmount()));
    }

    /**
     * Negates the input value, throwing an exception if an overflow occurs.
     *
     * @param value  the value to negate
     * @return the negated value
     * @throws ArithmeticException if the value is MIN_VALUE and cannot be negated
     */
    private static int safeNegate(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException("Integer.MIN_VALUE cannot be negated");
        }
        return -value;
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Converts this instance to another type of period.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param <T> the type to be converted to
//     * @param periodType  the period type to convert to, not null
//     * @return the new converted period field, never null
//     * @throws IllegalArgumentException if the conversion is not possible
//     * @throws ArithmeticException if the result overflows an int
//     */
//    public <T extends PeriodField> T convertTo(Class<T> periodType) {
//        ChronoUnit unit = null;
//        try {
//            Field field = periodType.getField("UNIT");
//            unit = (ChronoUnit) field.get(null);
//        } catch (NoSuchFieldException ex) {
//            throw new IllegalArgumentException("UNIT field missing on " + periodType, ex);
//        } catch (SecurityException ex) {
//            throw new IllegalArgumentException("UNIT field not public on " + periodType, ex);
//        } catch (IllegalArgumentException ex) {
//            throw new IllegalArgumentException("UNIT field access error on " + periodType, ex);
//        } catch (IllegalAccessException ex) {
//            throw new IllegalArgumentException("UNIT field not public on " + periodType, ex);
//        } catch (NullPointerException ex) {
//            throw new IllegalArgumentException("UNIT field not static on " + periodType, ex);
//        } catch (ClassCastException ex) {
//            throw new IllegalArgumentException("UNIT field not a ChronoUnit on " + periodType, ex);
//        }
//        return null; //getUnit().convert(this);
//    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param obj  the other amount of time, null returns false
     * @return true if this amount of time is the same as that specified
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractPeriodField && getClass() == obj.getClass()) {
            AbstractPeriodField other = (AbstractPeriodField) obj;
            return getAmount() == other.getAmount();
        }
        return false;
    }

    /**
     * Returns the hash code for this period field.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode() ^ getAmount();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount of time.
     *
     * @return the amount of time in ISO8601 string format
     */
    @Override
    public abstract String toString();

}
