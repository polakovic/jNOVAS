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


import java.util.FormattableFlags;

import eu.cloudmakers.astronometry.Hexagesimal;
import eu.cloudmakers.astronometry.NOVAS;
import eu.cloudmakers.astronometry.NOVAS.CatalogEntry;
import eu.cloudmakers.astronometry.NOVAS.CelestialObject;
import eu.cloudmakers.astronometry.NOVAS.DoubleRef;
import eu.cloudmakers.astronometry.NOVAS.Observer;
import eu.cloudmakers.astronometry.NOVAS.PositionOnSurface;
import eu.cloudmakers.astronometry.NOVAS.SkyPosition;
import eu.cloudmakers.astronometry.Utils;

/**
 * CheckOut jNOVAS library
 * 
 * @author Cloudmakers, s. r. o. (<a href="mailto:info@cloudmakers.eu">info@cloudmakers.eu</a>)
 * @version 1.0
 */

public class CheckOut {

  static double UTC_NOW = Utils.JDNow();
  static double TT_NOW=UTC_NOW+(34+32.184)/86400.0;
  static double UT1_NOW=UTC_NOW-0.477677/86400.0;
  static double DELTA_T = 34+32.184+0.477677;
  static int ACCURACY = 1;
  static int COORDINATE_SYSTEM = 1;
  static PositionOnSurface POSITION = new PositionOnSurface(Hexagesimal.valueOf("48 09").doubleValue(), Hexagesimal.valueOf("17 07").doubleValue(), 153.0, 10.0, 1010.0);
  static Observer SURFACE_OBSERVER = new Observer(POSITION);
  static CatalogEntry POLARIS_HIP = new CatalogEntry("POLARIS", "HIP", 11767, 180 * 0.6622851337 / Math.PI, 180 * 1.5579531082 / Math.PI, 44.48, -11.85, 7.54, -17.4);
  
  public static void main(String[] args) {

    short result;
    CatalogEntry entry = new CatalogEntry();
    SkyPosition position = new SkyPosition();
    DoubleRef ra = new DoubleRef();
    DoubleRef dec = new DoubleRef();

    DoubleRef rar = new DoubleRef();
    DoubleRef decr = new DoubleRef();
    
    DoubleRef zd = new DoubleRef();
    DoubleRef ad = new DoubleRef();

    
    System.out.println("Checkout test started...");

    System.out.println("JPL planetary ephemeris DE"+NOVAS.EPH_DE_NO);
    System.out.println(" <- begin date: "+NOVAS.EPH_JD_BEGIN+" ("+ Utils.JD2UTC(NOVAS.EPH_JD_BEGIN) + ")");
    System.out.println(" <- end date: "+NOVAS.EPH_JD_END+" ("+ Utils.JD2UTC(NOVAS.EPH_JD_END) + ")");
    
    System.out.println("\ntransformHipparcosEntry(...):");
    System.out.println(" -> hipparcos: " + POLARIS_HIP);
    NOVAS.transformHipparcosEntry(POLARIS_HIP, entry);
    System.out.println(" <- entry: " + entry);

    System.out.println("\nplace(...):");
    System.out.println(" -> ttDate: " + TT_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> object: " + entry);
    System.out.println(" -> location: " + SURFACE_OBSERVER);
    System.out.println(" -> deltaT: " + DELTA_T);
    System.out.println(" -> coordinateSystem: " + COORDINATE_SYSTEM);
    System.out.println(" -> accuracy: " + ACCURACY);
    result = NOVAS.place(TT_NOW, new CelestialObject(entry), SURFACE_OBSERVER, DELTA_T, 1, ACCURACY, position);
    System.out.println(" <- result: " + result);
    System.out.println(" <- position: " + position);

    System.out.println("\nappStar(...):");
    System.out.println(" -> ttDate: " + TT_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> entry: " + entry);
    System.out.println(" -> accuracy: " + ACCURACY);
    result = NOVAS.appStar(TT_NOW, entry, ACCURACY, ra, dec);
    System.out.println(" <- result: " + result);
    System.out.println(" <- ra: " + Hexagesimal.toString(ra.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" <- dec: " + Hexagesimal.toString(dec.value, FormattableFlags.ALTERNATE, 13, 2));

    System.out.println("\nvirtualStar(...):");
    System.out.println(" -> ttDate: " + TT_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> entry: " + entry);
    System.out.println(" -> accuracy: " + ACCURACY);
    result = NOVAS.virtualStar(TT_NOW, entry, ACCURACY, ra, dec);
    System.out.println(" <- result: " + result);
    System.out.println(" <- ra: " + Hexagesimal.toString(ra.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" <- dec: " + Hexagesimal.toString(dec.value, FormattableFlags.ALTERNATE, 13, 2));

    System.out.println("\nastroStar(...):");
    System.out.println(" -> ttDate: " + TT_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> entry: " + entry);
    System.out.println(" -> accuracy: " + ACCURACY);
    result = NOVAS.astroStar(TT_NOW, entry, ACCURACY, ra, dec);
    System.out.println(" <- result: " + result);
    System.out.println(" <- ra: " + Hexagesimal.toString(ra.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" <- dec: " + Hexagesimal.toString(dec.value, FormattableFlags.ALTERNATE, 13, 2));

    System.out.println("\nlocalStar(...):");
    System.out.println(" -> ttDate: " + TT_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> deltaT: " + DELTA_T);
    System.out.println(" -> entry: " + entry);
    System.out.println(" -> position: " + POSITION);
    System.out.println(" -> accuracy: " + ACCURACY);
    result = NOVAS.localStar(TT_NOW, DELTA_T, entry, POSITION, ACCURACY, ra, dec);
    System.out.println(" <- result: " + result);
    System.out.println(" <- ra: " + Hexagesimal.toString(ra.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" <- dec: " + Hexagesimal.toString(dec.value, FormattableFlags.ALTERNATE, 13, 2));

    System.out.println("\ntopoStar(...):");
    System.out.println(" -> ttDate: " + TT_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> deltaT: " + DELTA_T);
    System.out.println(" -> entry: " + entry);
    System.out.println(" -> position: " + POSITION);
    System.out.println(" -> accuracy: " + ACCURACY);
    result = NOVAS.topoStar(TT_NOW, DELTA_T, entry, POSITION, ACCURACY, ra, dec);
    System.out.println(" <- result: " + result);
    System.out.println(" <- ra: " + Hexagesimal.toString(ra.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" <- dec: " + Hexagesimal.toString(dec.value, FormattableFlags.ALTERNATE, 13, 2));

    System.out.println("\nequatorialToHorizontal(...):");
    System.out.println(" -> ut1Date: " + UT1_NOW + " (" + Utils.JD2UTC(UTC_NOW) + ")");
    System.out.println(" -> deltaT: " + DELTA_T);
    System.out.println(" -> entry: " + entry);
    System.out.println(" -> accuracy: " + ACCURACY);
    System.out.println(" -> xp: " + 0.0);
    System.out.println(" -> yp: " + 0.0);
    System.out.println(" -> position: " + POSITION);
    System.out.println(" -> ra: " + Hexagesimal.toString(ra.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" -> dec: " + Hexagesimal.toString(dec.value, FormattableFlags.ALTERNATE, 13, 2));
    NOVAS.equatorialToHorizontal(UT1_NOW, DELTA_T, ACCURACY, 0.0, 0.0, POSITION, ra.value, dec.value, 0, zd, ad, rar, decr);
    System.out.println(" <- zd: " + Hexagesimal.toString(90-zd.value, FormattableFlags.ALTERNATE, 13, 2));
    System.out.println(" <- ad: " + Hexagesimal.toString(ad.value, FormattableFlags.ALTERNATE, 13, 2));
    System.out.println(" <- rar: " + Hexagesimal.toString(rar.value, FormattableFlags.ALTERNATE | FormattableFlags.UPPERCASE, 13, 2));
    System.out.println(" <- decr: " + Hexagesimal.toString(decr.value, FormattableFlags.ALTERNATE, 13, 2));

    System.out.println("\nCheckout test finished...");
  }
}
