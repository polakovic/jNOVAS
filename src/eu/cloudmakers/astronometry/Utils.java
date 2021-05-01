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

import java.util.Date;

import eu.cloudmakers.astronometry.NOVAS.DoubleRef;

/**
 * The utility functions for astronometry package.
 * 
 * @author Cloudmakers, s. r. o. (<a
 *         href="mailto:info@cloudmakers.eu">info@cloudmakers.eu</a>)
 * @version 1.0
 * 
 */

public class Utils {

	public static final double JD_ORIGIN = 2440587.5;
	public static final double DELTA_T = 34 + 32.184 + 0.477677;
	public static final double DELTA_UTC_UT1 = -0.477677 / 86400.0;

	/**
	 * Compute current Julian Date.
	 */

	public static double JDNow() {
		return System.currentTimeMillis() / 86400000.0 + JD_ORIGIN;
	}

	/**
	 * Convert UTC Date to Julian Date
	 */

	public static double UTC2JD(Date utc) {
		return utc.getTime() / 86400000.0 + JD_ORIGIN;
	}

	/**
	 * Convert Julian Date to UTC Date
	 */

	public static Date JD2UTC(double jd) {
		return new Date((long) ((jd - JD_ORIGIN) * 86400000));
	}

	/**
	 * Compute Greenwich sidereal time
	 */

	public static double GST() {
		DoubleRef gst = new DoubleRef();
		NOVAS.siderealTime(JDNow() + Utils.DELTA_UTC_UT1, DELTA_T, 1, 0, 0, gst);
		return gst.value;
	}
}
