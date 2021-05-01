/*
 * jNOVAS 3.1 - Java wrapper for important functions from NOVAS 3.1 library
 * 
 * Copyright (c) 2012 Cloudmakers, s. r. o. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Underlying JNI code and documentation is based on
 * 
 *    Naval Observatory Vector Astrometry Software (NOVAS)
 *    C Edition, Version 3.1
 *
 *    U. S. Naval Observatory
 *    Astronomical Applications Dept.
 *    Washington, DC
 *    http://www.usno.navy.mil/USNO/astronomical-applications
 */

package eu.cloudmakers.astronometry;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.IllegalFormatWidthException;
import java.util.StringTokenizer;

/**
 * The Hexagesimal class wraps a value of the primitive type double in an object.
 * <p>
 * In addition, this class provides methods for converting a double to a String and a String to a
 * double in a hexagesimal notation and support for formatting with Formatter class.
 * 
 * @author Cloudmakers, s. r. o. (<a href="mailto:info@cloudmakers.eu">info@cloudmakers.eu</a>)
 * @version 1.0
 * 
 */

public class Hexagesimal extends Number implements Formattable {
  private static final long serialVersionUID = 9131523547312848964L;

  private static void format(Formatter formatter, int flags, int width, int precision, double value) {
    int fixedWidth;
    if (precision <= 0) {
      fixedWidth = width;
      precision = 0;
    } else
      fixedWidth = width - precision - 1;
    double d = Math.abs(value);
    double m = 60 * (d - (int) d);
    double s = 60 * (m - (int) m);
    if ((flags & FormattableFlags.ALTERNATE) == 0) {
      if (fixedWidth < 7)
        throw new IllegalFormatWidthException(width);
      if (precision == 0)
        formatter.format("%" + (fixedWidth - 6) + "d %02d %02", (int) value, (int) m, (int) s);
      else
        formatter.format("%" + (fixedWidth - 6) + "d %02d %0" + (precision + 3) + "." + precision + "f", (int) value, (int) m, s);
    } else {
      if (fixedWidth < 10)
        throw new IllegalFormatWidthException(width);
      fixedWidth -= 3;
      if ((flags & FormattableFlags.UPPERCASE) == 0) {
        if (precision == 0)
          formatter.format("%" + (fixedWidth - 6) + "d\u00B0 %02d\' %02d\"", (int) value, (int) m, (int) s);
        else
          formatter.format("%" + (fixedWidth - 6) + "d\u00B0 %02d\' %0" + (precision + 3) + "." + precision + "f\"", (int) value, (int) m, s);
      } else {
        if (precision == 0)
          formatter.format("%" + (fixedWidth - 6) + "dh %02dm %02ds", (int) value, (int) m, (int) s);
        else
          formatter.format("%" + (fixedWidth - 6) + "dh %02dm %0" + (precision + 3) + "." + precision + "fs", (int) value, (int) m, s);
      }
    }
  }

  private double value;

  /**
   * Create initialized instance
   * 
   * @param value
   *          the value to be represented by the <code>Hexagesimal</code>.
   */

  public Hexagesimal(double value) {
    this.value = value;
  }

  /**
   * Returns the value of this <code>Hexagesimal</code> as an int (by casting to type int).
   */

  public int intValue() {
    return (int) value;
  }

  /**
   * Returns the value of this <code>Hexagesimal</code> as an long (by casting to type long).
   */

  public long longValue() {
    return (long) value;
  }

  /**
   * Returns the value of this <code>Hexagesimal</code> as an float (by casting to type float).
   */

  public float floatValue() {
    return (float) value;
  }

  /**
   * Returns the value of this <code>Hexagesimal</code> as an byte (by casting to type byte).
   */

  public byte byteValue() {
    return (byte) value;
  }

  /**
   * Returns the value of this <code>Hexagesimal</code> as an short (by casting to type short).
   */

  public short shortValue() {
    return (short) value;
  }

  /**
   * Returns the value of this <code>Hexagesimal</code> object.
   */

  public double doubleValue() {
    return value;
  }

  /**
   * Implements formatTo() method of Formattable interface.
   * 
   * <blockquote>Use ALTERNATE flag for HMS or DMS separators instead of spaces<br>
   * Use UPPERCASE flag for HMS separators instead of DMS separators</blockquote>
   */
  public void formatTo(Formatter formatter, int flags, int width, int precision) {
    format(formatter, flags, width, precision, value);
  }

  /**
   * Returns a <code>Hexagesimal</code> object holding the double value represented by the argument
   * hexagesimal string.
   * 
   * @param string
   *          the string to be parsed.
   * @return a <code>Hexagesimal</code> object holding the value represented by the argument.
   */
  public static Hexagesimal valueOf(String string) {
    double value = 0;
    int div = 1;
    StringTokenizer tokenizer = new StringTokenizer(string, " :;\u00B0'\"hms");
    while (tokenizer.hasMoreTokens()) {
      String nextToken = tokenizer.nextToken();
      if (nextToken.length() > 0) {
        value += Double.parseDouble(nextToken) / div;
        div = div * 60;
      }
    }
    return new Hexagesimal(value);
  }

  /**
   * Returns a hexagesimal string representation of the argument.
   * 
   * @param value
   *          the double to be converted.
   * @param flags
   *          flags corresponding to FormattableFlags.
   * 
   *          <blockquote>Use ALTERNATE flag for HMS or DMS separators instead of spaces<br>
   *          Use UPPERCASE flag for HMS separators instead of DMS separators</blockquote>
   * @param width
   *          the minimum number of characters to be written to the output. If the length of the
   *          converted value is less than the width then the output will be padded by ' ' until the
   *          total number of characters equals width. The padding is at the beginning.
   * @param precision
   *          the precision of seconds field.
   * @return the heaxesimal string representation of the argument.
   */

  public static String toString(double value, int flags, int width, int precision) {
    Formatter formatter = new Formatter();
    format(formatter, flags, width, precision, value);
    return formatter.toString();
  }

  /**
   * Returns a hexagesimal string representation of the argument.
   * 
   * @param value
   *          the double to be converted.
   * 
   * @return the heaxgesimal string representation of the argument in <code>DD MM SS.SS</code>
   *         format.
   */

  public static String toString(double value) {
    Formatter formatter = new Formatter();
    format(formatter, 0, 13, 2, value);
    return formatter.toString();
  }

  /**
   * Returns a hexagesimal string representation of the object.
   * 
   * @param value
   *          the double to be converted.
   * 
   * @return the heaxgesimal string representation of the object in <code>DD MM SS.SS</code> format.
   */

  public String toString() {
    return toString(value);
  }
}
