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
 *    Naval Observatory Vector Astrometry Software (NOVAS), C Edition, Version 3.1
 *
 *    U. S. Naval Observatory
 *    Astronomical Applications Dept.
 *    Washington, DC
 *    http://www.usno.navy.mil/USNO/astronomical-applications
 *    
 * Ephemerides file is based on
 * 
 *    JPL planetary ephemeris DE421
 *    
 *    William Folkner
 *    JPL m/s 301-150; Pasadena, CA  91109
 *    TEL: 818-354-0443
 *    FAX: 818-393-7631
 *    e-mail: William.Folkner@jpl.nasa.gov 
 *     
 */

package eu.cloudmakers.astronometry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Formatter;

/**
 * Wrapper for NOVAS 3.1 library.
 * <p>
 * Underlying JNI code and documentation is based on <blockquote>Naval
 * Observatory Vector Astrometry Software (NOVAS), C Edition, Version 3.1
 * <p>
 * U. S. Naval Observatory<br>
 * Astronomical Applications Dept.<br>
 * Washington, DC<br>
 * http://www.usno.navy.mil/USNO/astronomical-applications</blockquote>
 * 
 * Ephemerides file is based on <blockquote>JPL planetary and lunar ephemeris
 * DE421
 * <p>
 * William Folkner<br>
 * JPL m/s 301-150; Pasadena, CA 91109<br>
 * TEL: 818-354-0443<br>
 * FAX: 818-393-7631<br>
 * e-mail: William.Folkner@jpl.nasa.gov</blockquote>
 * 
 * @author Cloudmakers, s. r. o. (<a
 *         href="mailto:info@cloudmakers.eu">info@cloudmakers.eu</a>)
 * @version 1.0
 */

public class NOVAS {

  /**
   * JPL Planetary and Lunar Ephemerides file number (421 for DE421).
   */

  public static int EPH_DE_NO;

  /**
   * JPL Ephemerides file start date.
   */

  public static double EPH_JD_BEGIN;

  /**
   * JPL Ephemerides file end date.
   */

  public static double EPH_JD_END;

  private static String extract(String name, boolean force) {
    try {
      File file = new File(System.getProperty("java.io.tmpdir"), name);
      if (!(file.exists() && file.length()>0)|| force) {
        InputStream in = NOVAS.class.getResourceAsStream(name);
        OutputStream out = new FileOutputStream(file);
        byte buffer[] = new byte[10240];
        int size;
        while ((size = in.read(buffer)) > 0)
          out.write(buffer, 0, size);
        out.close();
        in.close();
      }
      return file.getAbsolutePath();
    } catch (Exception exception) {
      System.err.println(name+" not found...");
      return null;
    }
  }

  // private static native int init(Node signature, String eph);
  private static native int init(String eph);

  static {
    String arch = System.getProperty("os.arch").toLowerCase();
    String os = System.getProperty("os.name").toLowerCase();
    String vendor = System.getProperty("java.vm.vendor").toLowerCase();

    String jniLibraryName = null;
    if (os.indexOf("mac") >= 0) {
      jniLibraryName = System.mapLibraryName("novas");
      if (vendor.indexOf("oracle") >= 0)
        jniLibraryName = jniLibraryName.substring(0, jniLibraryName.lastIndexOf(".")) + ".jnilib";
    }
    else if (arch.indexOf("64") >= 0)
      jniLibraryName = System.mapLibraryName("novas64");
    else
      jniLibraryName = System.mapLibraryName("novas32");
    System.err.println("jNOVAS 3.1 - Java wrapper for NOVAS 3.1 Library");
    System.err.println("Copyright (c) 2012, CloudMakers, s. r. o.");
    System.err.println();
    System.load(extract(jniLibraryName, true));
    init(extract("JPLEPH.421", false));
  }

  /**
   * Wrapper for "struct cat_entry".
   * <p>
   * Basic astrometric data for any celestial object located outside the solar
   * system; the catalog data for a star.
   * <p>
   * For more information, see NOVAS_C3.1_Guide.pdf, page C-41.
   */

  public static class CatalogEntry implements Serializable {
    private static final long serialVersionUID = 1261364142377699474L;

    /**
     * Name of celestial object (limited to 50 characters).
     */

    public String name;

    /**
     * Catalog designator (e.g. HIP, limited to 3 characters).
     */

    public String catalog;

    /**
     * Integer identifier assigned to object.
     */

    public int number;

    /**
     * ICRS right ascension in hours.
     */

    public double ra;

    /**
     * ICRS declination in degrees.
     */

    public double dec;

    /**
     * ICRS proper motion in right ascension in milliarcseconds/year.
     */

    public double raProperMotion;

    /**
     * ICRS proper motion in declination in milliarcseconds/year.
     */

    public double decProperMotion;

    /**
     * Parallax in milliarcseconds.
     */

    public double parallax;

    /**
     * Radial velocity in km/s.
     */

    public double radialVelocity;

    /**
     * Create uninitialized instance.
     */

    public CatalogEntry() {
    }

    /**
     * Create initialized instance.
     */

    public CatalogEntry(String name, String catalog, int number, double ra, double dec, double raProperMotion, double decProperMotion, double parallax, double radialVelocity) {
      this.name = name;
      this.catalog = catalog;
      this.number = number;
      this.ra = ra;
      this.dec = dec;
      this.raProperMotion = raProperMotion;
      this.decProperMotion = decProperMotion;
      this.parallax = parallax;
      this.radialVelocity = radialVelocity;
    }

    /**
     * Dump attributes.
     */

    public String toString() {
      return new Formatter().format("CatalogEntry { %s, %s, %d, [%#16.5S, %#16.5s], [%3.2f, %3.2f], %3.2f, %3.2f }", name, catalog, number, new Hexagesimal(ra), new Hexagesimal(dec), raProperMotion, decProperMotion, parallax, radialVelocity).toString();
    }
  }

  /**
   * Wrapper for "struct object".
   * <p>
   * For more information, see NOVAS_C3.1_Guide.pdf, page C-42.
   */

  public static class CelestialObject {

    /**
     * Type of object.
     * <p>
     * <blockquote>0 = major planet Pluto, Sun, or Moon<br>
     * 1 = minor planet<br>
     * 2 = object located outside the solar system (star, nebula, galaxy,
     * etc.)</blockquote>
     */

    public short type;

    /**
     * Object number.
     * <p>
     * <blockquote>For type = 0: Mercury &rarr; 1, ..., Pluto &rarr; 9, Sun
     * &rarr; 10, Moon &rarr; 11.<br>
     * For type = 1: minor planet number.<br>
     * For type = 2: set to 0 (object is fully specified by star
     * attribute)</blockquote>
     */

    public short number;

    /**
     * Name of the object (limited to 50 characters).
     */

    public String name;

    /**
     * Basic astrometric data for any celestial object located outside the solar
     * system; the catalog data for a star.
     * <p>
     * Set to null for type != 2.
     */

    public CatalogEntry star;

    /**
     * Create initialized instance for type = 0 or type = 1.
     */

    public CelestialObject(short type, short number, String name) {
      this.type = type;
      this.number = number;
      this.name = name;
      star = null;
    }

    /**
     * Create initialized instance for type = 2.
     */

    public CelestialObject(CatalogEntry star) {
      this.type = 2;
      this.number = 0;
      this.name = star.name;
      this.star = star;
    }

    /**
     * Dump attributes.
     */

    public String toString() {
      return new Formatter().format("CelestialObject { %d, %s, %d, %s }", type, name, number, star).toString();
    }
  }

  /**
   * Wrapper for "struct on_surface".
   * <p>
   * Structure on_surface contains data for the observer's location on the
   * surface of the Earth. The atmospheric parameters (temperature and pressure)
   * are used only by the refraction function called from function equ2hor when
   * ref_option = 2; dummy values can be used otherwise.
   * <p>
   * For more information, see NOVAS_C3.1_Guide.pdf, page C-42.
   */
  public static class PositionOnSurface {

    /**
     * Geodetic (ITRS) latitude; north positive in degrees.
     */

    public double latitude;

    /**
     * Geodetic (ITRS) longitude; east positive in degrees.
     */

    public double longitude;

    /**
     * Height of the observer in meters.
     */

    public double height;

    /**
     * Temperature in degrees Celsius.
     */

    public double temperature;

    /**
     * Atmospheric pressure millibars.
     */

    public double pressure;

    /**
     * Create initialized instance.
     */

    public PositionOnSurface(double latitude, double longitude, double height, double temperature, double pressure) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.height = height;
      this.temperature = temperature;
      this.pressure = pressure;
    }

    /**
     * Dump attributes.
     */

    public String toString() {
      return new Formatter().format("PositionOnSurface { [%#10s, %#10s], %2.1f, %2.1f, %2.1f }", new Hexagesimal(latitude), new Hexagesimal(longitude), height, temperature, pressure).toString();
    }
  }

  /**
   * Wrapper for "struct in_space".
   * <p>
   * Data for an observer's location on a near-Earth spacecraft.
   * <p>
   * For more information, see NOVAS_C3.1_Guide.pdf, page C-43.
   */

  public static class PositionInSpace {

    /**
     * Geocentric position vector (x, y, z), components in km.
     */

    public double position[];

    /**
     * Geocentric velocity vector (x_dot, y_dot, z_dot), components in km/s.
     */

    public double velocity[];

    /**
     * Create initialized instance.
     */

    public PositionInSpace(double position[], double velocity[]) {
      this.position = position;
      this.velocity = velocity;
    }

    /**
     * Dump attributes.
     */

    public String toString() {
      Formatter formatter = new Formatter().format("PositionInSpace {");
      if (position != null)
        formatter.format("[%2.1f, %2.1f, %2.1f]", position[0], position[1], position[2]);
      if (position != null && velocity != null)
        formatter.format(", ");
      if (velocity != null)
        formatter.format("[%2.1f, %2.1f, %2.1f]", velocity[0], velocity[1], velocity[2]);
      return formatter.format("}").toString();
    }
  }

  /**
   * Wrapper for "struct observer".
   * <p>
   * General container for information specifying the location of the observer.
   * <p>
   * For more information, see NOVAS_C3.1_Guide.pdf, page C-43.
   */

  public static class Observer {

    /**
     * integer code specifying location of observer: <blockquote>0 &rarr;
     * observer at geocenter<br>
     * 1 &rarr; observer on surface of earth<br>
     * 2 &rarr; observer on near-earth spacecraft</blockquote>
     */

    public short where;

    /**
     * Structure containing data for an observer's location on the surface of
     * the Earth (where = 1).
     */

    public PositionOnSurface onSurface;

    /**
     * Data for an observer's location on a near-Earth spacecraft (where = 2).
     */

    public PositionInSpace nearEarth;

    /**
     * Create initialized instance with location at geocenter.
     */

    public Observer() {
      this.where = 0;
      this.onSurface = null;
      this.nearEarth = null;
    }

    /**
     * Create initialized instance with location on surface.
     */

    public Observer(PositionOnSurface onSurface) {
      this.where = 1;
      this.onSurface = onSurface;
      this.nearEarth = null;
    }

    /**
     * Create initialized instance with location in space.
     */

    public Observer(PositionInSpace nearEarth) {
      this.where = 2;
      this.onSurface = null;
      this.nearEarth = nearEarth;
    }

    /**
     * Dump attributes.
     */

    public String toString() {
      switch (where) {
      case 1:
        return new Formatter().format("Observer { %d, %s }", where, onSurface).toString();
      case 2:
        return new Formatter().format("Observer { %d, %s }", where, nearEarth).toString();
      default:
        return new Formatter().format("Observer { %d }", where).toString();
      }
    }
  }

  /**
   * Wrapper for "struct sky_pos".
   * <p>
   * Contains data specifying a celestial object's place on the sky,
   * specifically the output from function place.
   * <p>
   * For more information, see NOVAS_C3.1_Guide.pdf, page C-43.
   */

  public static class SkyPosition {

    /**
     * Unit vector toward object.
     */

    public double rHat[];

    /**
     * Apparent, topocentric, or astrometric right ascension in hours.
     */

    public double ra;

    /**
     * Apparent, topocentric, or astrometric declination in degrees.
     */

    public double dec;

    /**
     * True (geometric, Euclidian) distance to solar system body or 0.0 for star
     * in AU.
     */

    public double distance;

    /**
     * Radial velocity in km/s.
     */

    public double radialVelocity;

    /**
     * Create uninitialized instance.
     */

    public SkyPosition() {
    }

    /**
     * Create initialized instance.
     */

    public SkyPosition(double[] rHat, double ra, double dec, double distance, double radialVelocity) {
      this.rHat = rHat;
      this.ra = ra;
      this.dec = dec;
      this.distance = distance;
      this.radialVelocity = radialVelocity;
    }

    /**
     * Dump attributes.
     */

    public String toString() {
      Formatter formatter = new Formatter().format("SkyPosition {");
      if (rHat != null)
        formatter.format("[%5.4f, %5.4f, %5.4f], ", rHat[0], rHat[1], rHat[2]);
      return formatter.format("[%#10S, %#10s], %2.1f, %5.4f }", new Hexagesimal(ra), new Hexagesimal(dec), distance, radialVelocity).toString();
    }
  }

  // /**
  // * Wrapper for "struct ra_of_cio".
  // * <p>
  // * The right ascension of the Celestial Intermediate Origin with respect to
  // the GCRS.
  // * <p>
  // * For more information, see NOVAS_C3.1_Guide.pdf, page C-43.
  // */
  //
  // public static class CelestialIntermediateOriginRA {
  //
  // /**
  // * TDB Julian date.
  // */
  //
  // public double date;
  //
  // /**
  // * Right ascension of the CIO with respect to the GCRS in arcseconds.
  // */
  //
  // public double ra;
  //
  // /**
  // * Dump attributes.
  // */
  //
  // public String toString() {
  // return new
  // Formatter().format("CelestialIntermediateOriginRA { %#10S, %2.1f }", date,
  // new
  // Hexagesimal(ra)).toString();
  // }
  // }

  /**
   * Wrapper for output parameters.
   */

  public static class DoubleRef {

    /**
     * Wrapped value.
     */

    public double value;

    /**
     * Empty wrapper.
     */

    public DoubleRef() {
    }

    /**
     * Initialize wrapped value.
     */

    public DoubleRef(double value) {
      this.value = value;
    }

    /**
     * Dump wrapped value.
     */

    public String toString() {
      return Double.toString(value);
    }
  }

  /**
   * Compute the apparent direction of a star or solar system body at a
   * specified time and in a specified coordinate system.
   * <p>
   * Wrapper for place() method, for more information, see NOVAS_C3.1_Guide.pdf,
   * page C-48.
   * 
   * @param ttDate
   *          TT Julian date for place.
   * @param object
   *          the celestial object of interest
   * @param location
   *          the location of the observer
   * @param deltaT
   *          difference TT-UT1 at <code>ttDate</code> in seconds of time
   * @param coordinateSystem
   *          code specifying coordinate system of the output position:
   *          <blockquote> 0 &rarr; GCRS or "local GCRS"<br>
   *          1 &rarr; true equator and equinox of date<br>
   *          2 &rarr; true equator and CIO of date<br>
   *          3 &rarr; astrometric coordinates, i.e., without light deflection
   *          or aberration) </blockquote>
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <blockquote> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </blockquote>
   * @param output
   *          output data specifying object's place on the sky at
   *          <code>ttDate</code>, with respect to the specified output
   *          coordinate system
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; invalid value of 'coord_sys'<br>
   *         2 &rarr; invalid value of 'accuracy'<br>
   *         3 &rarr; Earth is the observed object, and the observer is either
   *         at the geocenter or on the Earth's surface (not permitted)<br>
   *         > 10, < 40 &rarr; 10 + error from function 'ephemeris'<br>
   *         > 40, < 50 &rarr; 40 + error from function 'geo_posvel'<br>
   *         > 50, < 70 &rarr; 50 + error from function 'light_time'<br>
   *         > 70, < 80 &rarr; 70 + error from function 'grav_def'<br>
   *         > 80, < 90 &rarr; 80 + error from function 'cio_location'<br>
   *         > 90, < 100 &rarr; 90 + error from function 'cio_basis'
   *         </blockquote>
   */

  public static native short place(double ttDate, CelestialObject object, Observer location, double deltaT, int coordinateSystem, int accuracy, SkyPosition output);

  /**
   * Transform a star's catalog quantities for a change of epoch and/or equator
   * and equinox. Also used to rotate catalog quantities on the dynamical
   * equator and equinox of J2000.0 to the ICRS or vice versa.
   * <p>
   * Wrapper for tansform_cat() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-57.
   * 
   * @param option
   *          Transformation option: <blockquote> 1 &rarr; change epoch; same
   *          equator and equinox<br>
   *          2 &rarr; change equator and equinox; same epoch<br>
   *          3 &rarr; change equator and equinox and epoch<br>
   *          4 &rarr; change equator and equinox J2000.0 to ICRS<br>
   *          5 &rarr; change ICRS to equator and equinox of
   *          J2000.0</blockquote>
   * 
   * @param inDate
   *          TT Julian date, or year, of input catalog data.
   * @param inEntry
   *          an entry from the input catalog, with units as given in the class
   *          definition.
   * @param outDate
   *          TT Julian date, or year, of transformed catalog data.
   * @param outCatalog
   *          Catalog identifier (e.g. HIP = Hipparcos, TY2 = Tycho-2).
   * @param OutEntry
   *          The transformed catalog entry, with units as given in the class
   *          definition
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of an input date for option 2 or 3<br>
   *         2 &rarr; length of 'newcat_id' out of bounds </blockquote>
   */

  public static native short transformCatalogEntry(int option, double inDate, CatalogEntry inEntry, double outDate, String outCatalog, CatalogEntry OutEntry);

  /**
   * Convert Hipparcos catalog data at epoch J1991.25 to epoch J2000.0, for use
   * within NOVAS. To be used only for Hipparcos or Tycho stars with linear
   * space motion. Both input and output data is in the ICRS.
   * <p>
   * Wrapper for transform_hip() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-59.
   * 
   * @param hipparcos
   *          an entry from the Hipparcos catalog, at epoch J1991.25, with all
   *          members having Hipparcos catalog units. <blockquote>Epoch:
   *          J1991.25<br>
   *          Right ascension (RA): degrees<br>
   *          Declination (Dec): degrees<br>
   *          Proper motion in RA: milliarcseconds per year<br>
   *          Proper motion in Dec: milliarcseconds per year<br>
   *          Parallax: milliarcseconds<br>
   *          Radial velocity: kilometers per second (not in
   *          catalog)</blockquote>
   * @param hip2000
   *          the transformed input entry, at epoch J2000.0. <blockquote>Epoch:
   *          J2000.0<br>
   *          Right ascension: hours<br>
   *          Declination: degrees<br>
   *          Proper motion in RA: milliarcseconds per year<br>
   *          Proper motion in Dec: milliarcseconds per year<br>
   *          Parallax: milliarcseconds<br>
   *          Radial velocity: kilometers per second<br>
   *          </blockquote>
   */

  public static native void transformHipparcosEntry(CatalogEntry hipparcos, CatalogEntry hip2000);

  /**
   * Compute the apparent place of a star at date <code>ttDate</code>, given its
   * catalog mean place, proper motion, parallax, and radial velocity.
   * <p>
   * Wrapper for app_start() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-61.
   * 
   * @param ttDate
   *          TT Julian date for apparent place.
   * @param star
   *          catalog data for the object in the ICRS.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Apparent right ascension in hours, referred to true equator and
   *          equinox of date <code>ttDate</code>.
   * @param dec
   *          Apparent declination in degrees, referred to true equator and
   *          equinox of date <code>ttDate</code>.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         > 10 &rarr; Error code from function 'make_object'.<br>
   *         > 20 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short appStar(double ttDate, CatalogEntry star, int accuracy, DoubleRef ra, DoubleRef dec);

  /**
   * Compute the virtual place of a star at date <code>ttDate</code>, given its
   * catalog mean place, proper motion, parallax, and radial velocity.
   * <p>
   * Wrapper for virtual_star() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-64.
   * 
   * @param ttDate
   *          TT Julian date for apparent place.
   * @param star
   *          catalog data for the object in the ICRS.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Virtual right ascension in hours, referred to the GCRS.
   * @param dec
   *          Virtual declination in degrees, referred to the GCRS.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         > 10 &rarr; Error code from function 'make_object'.<br>
   *         > 20 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short virtualStar(double ttDate, CatalogEntry star, int accuracy, DoubleRef ra, DoubleRef dec);

  /**
   * Compute the astrometric place of a star at date <code>ttDate</code>, given
   * its catalog mean place, proper motion, parallax, and radial velocity.
   * <p>
   * Wrapper for astro_star() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-66.
   * 
   * @param ttDate
   *          TT Julian date for astrometric place.
   * @param star
   *          catalog data for the object in the ICRS.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Astrometric right ascension in hours (referred to the ICRS,
   *          without light deflection or aberration).
   * @param dec
   *          Astrometric declination in degrees (referred to the ICRS, without
   *          light deflection or aberration).
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         > 10 &rarr; Error code from function 'make_object'.<br>
   *         > 20 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short astroStar(double ttDate, CatalogEntry star, int accuracy, DoubleRef ra, DoubleRef dec);

  /**
   * Compute the local place of a star at date <code>ttDate</code>, given its
   * catalog mean place, proper motion, parallax, and radial velocity.
   * <p>
   * Wrapper for local_star() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-65.
   * 
   * @param ttDate
   *          TT Julian date for local place.
   * @param deltaT
   *          Difference TT-UT1 at <code>ttDate</code>, in seconds of time.
   * @param star
   *          catalog data for the object in the ICRS.
   * @param position
   *          the position of the observer
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Local right ascension in hours, referred to the 'local GCRS'.
   * @param dec
   *          Local declination in degrees, referred to the 'local GCRS'.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'where' in structure 'position'.<br>
   *         > 10 &rarr; Error code from function 'make_object'.<br>
   *         > 20 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short localStar(double ttDate, double deltaT, CatalogEntry star, PositionOnSurface position, int accuracy, DoubleRef ra, DoubleRef dec);

  /**
   * Compute the topocentric place of a star at date <code>ttDate</code>, given
   * its catalog mean place, proper motion, parallax, and radial velocity.
   * <p>
   * Wrapper for topo_star() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-62.
   * 
   * @param ttDate
   *          TT Julian date for topocentric place.
   * @param deltaT
   *          Difference TT-UT1 at <code>ttDate</code>, in seconds of time.
   * @param star
   *          catalog data for the object in the ICRS.
   * @param position
   *          the position of the observer
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Topocentric right ascension in hours, referred to true equator and
   *          equinox of date <code>ttDate</code>.
   * @param dec
   *          Topocentric declination in degrees, referred to true equator and
   *          equinox of date <code>ttDate</code>.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'where' in structure 'position'.<br>
   *         > 10 &rarr; Error code from function 'make_object'.<br>
   *         > 20 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short topoStar(double ttDate, double deltaT, CatalogEntry star, PositionOnSurface position, int accuracy, DoubleRef ra, DoubleRef dec);

  /**
   * Compute the apparent place of a solar system body.
   * <p>
   * Wrapper for app_planet() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-67.
   * 
   * @param ttDate
   *          TT Julian date for topocentric place.
   * @param body
   *          structure containing the body designation for the solar system
   *          body.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Apparent right ascension in hours, referred to true equator and
   *          equinox of date.
   * @param dec
   *          Apparent declination in degrees, referred to true equator and
   *          equinox of date.
   * @param distance
   *          True distance from Earth to the body at <code>ttDate</code> in AU.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'type' in structure 'body'.<br>
   *         > 10 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short appPlanet(double ttDate, CelestialObject body, int accuracy, DoubleRef ra, DoubleRef dec, DoubleRef distance);

  /**
   * Compute the virtual place of a solar system body.
   * <p>
   * Wrapper for virtual_planet() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-70.
   * 
   * @param ttDate
   *          TT Julian date for virtual place.
   * @param body
   *          structure containing the body designation for the solar system
   *          body.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Virtual right ascension in hours, referred to the GCRS.
   * @param dec
   *          Virtual declination in degrees, referred to the GCRS.
   * @param distance
   *          True distance from Earth to the body in AU.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'type' in structure 'body'.<br>
   *         > 10 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short virtualPlanet(double ttDate, CelestialObject body, int accuracy, DoubleRef ra, DoubleRef dec, DoubleRef distance);

  /**
   * Compute the astrometric place of a solar system body.
   * <p>
   * Wrapper for astro_planet() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-73.
   * 
   * @param ttDate
   *          TT Julian date for astrometric place.
   * @param body
   *          structure containing the body designation for the solar system
   *          body.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Astrometric right ascension in hours (referred to the ICRS,
   *          without light deflection or aberration).
   * @param dec
   *          Astrometric declination in degrees (referred to the ICRS, without
   *          light deflection or aberration).
   * @param distance
   *          True distance from Earth to the body in AU.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'type' in structure 'body'.<br>
   *         > 10 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short astroPlanet(double ttDate, CelestialObject body, int accuracy, DoubleRef ra, DoubleRef dec, DoubleRef distance);

  /**
   * Compute the local place of a solar system body.
   * <p>
   * Wrapper for local_planet() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-71.
   * 
   * @param ttDate
   *          TT Julian date for local place.
   * @param body
   *          structure containing the body designation for the solar system
   *          body.
   * @param deltaT
   *          Difference TT-UT1 at <code>ttDate</code>, in seconds of time.
   * @param position
   *          the position of the observer
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Local right ascension in hours, referred to the 'local GCRS'.
   * @param dec
   *          Local declination in degrees, referred to the 'local GCRS'.
   * @param distance
   *          True distance from Earth to the body in AU.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'where' in structure 'position'.<br>
   *         > 10 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short localPlanet(double ttDate, CelestialObject body, double deltaT, PositionOnSurface position, int accuracy, DoubleRef ra, DoubleRef dec, DoubleRef distance);

  /**
   * Compute the topocentric place of a solar system body.
   * <p>
   * Wrapper for topu_planet() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-68.
   * 
   * @param ttDate
   *          TT Julian date for topocentric place.
   * @param body
   *          structure containing the body designation for the solar system
   *          body.
   * @param deltaT
   *          Difference TT-UT1 at <code>ttDate</code>, in seconds of time.
   * @param position
   *          the position of the observer
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param ra
   *          Topocentric right ascension in hours, referred to true equator and
   *          equinox of date.
   * @param dec
   *          Topocentric declination in degrees, referred to true equator and
   *          equinox of date.
   * @param distance
   *          True distance from Earth to the body at <code>ttDate</code> in AU.
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'where' in structure 'position'.<br>
   *         > 10 &rarr; Error code from function 'place'.</blockquote>
   */

  public static native short topoPlanet(double ttDate, CelestialObject body, double deltaT, PositionOnSurface position, int accuracy, DoubleRef ra, DoubleRef dec, DoubleRef distance);

  /**
   * Transform topocentric right ascension and declination to zenith distance
   * and azimuth. It uses a method that properly accounts for polar motion,
   * which is significant at the sub-arcsecond level. This function can also
   * adjust coordinates for atmospheric refraction.
   * <p>
   * Wrapper for equ2hor() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-55.
   * 
   * @param ut1Date
   *          UT1 Julian date.
   * @param deltaT
   *          difference TT-UT1 at <code>ut1Date</code>, in seconds.
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @param xp
   *          conventionally-defined xp coordinate of celestial intermediate
   *          pole with respect to ITRS reference pole, in arcseconds.
   * @param yp
   *          conventionally-defined yp coordinate of celestial intermediate
   *          pole with respect to ITRS reference pole, in arcseconds.
   * @param position
   *          observer's location
   * @param ra
   *          topocentric right ascension of object of interest, in hours,
   *          referred to true equator and equinox of date.
   * @param dec
   *          topocentric declination of object of interest, in degrees,
   *          referred to true equator and equinox of date.
   * @param refOption
   *          refracion options <blockquote> 0 &rarr; no refraction<br>
   *          1 &rarr; include refraction, using 'standard' atmospheric
   *          conditions.<br>
   *          2 &rarr; include refraction, using atmospheric parameters input in
   *          the 'location' structure.</blockquote>
   * @param zd
   *          Topocentric zenith distance in degrees, affected by refraction if
   *          <code>refOption</code> is non-zero.
   * @param ad
   *          Topocentric azimuth (measured east from north) in degrees.
   * @param rar
   *          Topocentric right ascension of object of interest, in hours,
   *          referred to true equator and equinox of date, affected by
   *          refraction if <code>refOption</code> is non-zero.
   * @param decr
   *          Topocentric declination of object of interest, in degrees,
   *          referred to true equator and equinox of date, affected by
   *          refraction if <code>refOption</code> is non-zero.
   */

  public static native void equatorialToHorizontal(double ut1Date, double deltaT, int accuracy, double xp, double yp, PositionOnSurface position, double ra, double dec, int refOption, DoubleRef zd, DoubleRef ad, DoubleRef rar, DoubleRef decr);

  /**
   * Computes the Greenwich sidereal time, either mean or apparent, at Julian
   * date.
   * <p>
   * Wrapper for sidereal_time() method, for more information, see
   * NOVAS_C3.1_Guide.pdf, page C-51.
   * 
   * @param ttDate
   *          UT1 Julian date for topocentric place.
   * @param deltaT
   *          Difference TT-UT1 at <code>ttDate</code>, in seconds of time.
   * @param gstType
   *          Selection for type <BLOCKQUOTE> 0 &rarr; compute Greenwich mean
   *          sidereal time<BR>
   *          1 &rarr; compute Greenwich apparent sidereal time</BLOCKQUOTE>
   * @param method
   *          Selection for method <BLOCKQUOTE> 0 &rarr; CIO-based method<BR>
   *          1 &rarr; equinox-based method</BLOCKQUOTE>
   * @param accuracy
   *          code specifying the relative accuracy of the output position:
   *          <BLOCKQUOTE> 0 &rarr; full accuracy<BR>
   *          1 &rarr; reduced accuracy </BLOCKQUOTE>
   * @return error code: <blockquote> 0 &rarr; no problems<br>
   *         1 &rarr; Invalid value of 'accuracy'.<br>
   *         2 &rarr; Error code from function 'method'.<br>
   *         > 10 &rarr; 10 + error from function 'cio_rai'.</blockquote>
   * @param gst
   *          Greenwich (mean or apparent) sidereal time, in hours.
   */

  public static native short siderealTime(double ttDate, double deltaT, int gstType, int method, int accuracy, DoubleRef gst);

}
