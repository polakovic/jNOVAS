#include "eu_cloudmakers_astronometry_NOVAS.h"

#include "novas.h"
#include "eph_manager.h"

#define safe_strncpy(d, s, l) { strncpy(d, s, l); d[l-1]='\0'; }

short int solarsystem_1 (double tjd, short int body, short int origin, double *position, double *velocity);
short int solarsystem_hp_1 (double tjd[2], short int body, short int origin, double *position, double *velocity);
short int solarsystem_3 (double tjd, short int body, short int origin, double *position, double *velocity);
short int solarsystem_hp_3 (double tjd[2], short int body, short int origin, double *position, double *velocity);


jclass NOVAS_CLASS;
jfieldID EPH_DE_NO_FIELD;
jfieldID EPH_JD_BEGIN_FIELD;
jfieldID EPH_JD_END_FIELD;

jclass CAT_ENTRY_CLASS;
jfieldID STARNAME_FIELD, CATALOG_FIELD, STARNUMBER_FIELD, RA_FIELD, DEC_FIELD, PROMORA_FIELD, PROMODEC_FIELD, PARALLAX_FIELD, RADIALVELOCITY_FIELD;

jclass OBJECT_CLASS;
jfieldID TYPE_FIELD, NUMBER_FIELD, NAME_FIELD, STAR_FIELD;

jclass ON_SURFACE_CLASS;
jfieldID LATITUDE_FIELD, LONGITUDE_FIELD, HEIGHT_FIELD, TEMPERATURE_FIELD, PRESSURE_FIELD;

jclass IN_SPACE_CLASS;
jfieldID SC_POS_FIELD, SC_VEL_FIELD;

jclass OBSERVER_CLASS;
jfieldID WHERE_FIELD, ON_SURF_FIELD, NEAR_EARTH_FIELD;

jclass SKY_POS_CLASS;
jfieldID R_HAT_FIELD, RA__FIELD, DEC__FIELD, DIS_FIELD, RV_FIELD;

jclass DOUBLE_REF_CLASS;
jfieldID VALUE_FIELD;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  return JNI_VERSION_1_4;
}

int has_eph=0;

JNIEXPORT jint JNICALL Java_eu_cloudmakers_astronometry_NOVAS_init(JNIEnv *env, jclass, jstring file) {
  short result, de_num;
  char *str;
  double jd_beg, jd_end;

  NOVAS_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS"));
  EPH_DE_NO_FIELD=env->GetStaticFieldID(NOVAS_CLASS, "EPH_DE_NO", "I");
  EPH_JD_BEGIN_FIELD=env->GetStaticFieldID(NOVAS_CLASS, "EPH_JD_BEGIN", "D");
  EPH_JD_END_FIELD=env->GetStaticFieldID(NOVAS_CLASS, "EPH_JD_END", "D");
  
  CAT_ENTRY_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$CatalogEntry"));
  STARNAME_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "name", "Ljava/lang/String;");
  CATALOG_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "catalog", "Ljava/lang/String;");
  STARNUMBER_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "number", "I");
  RA_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "ra", "D");
  DEC_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "dec", "D");
  PROMORA_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "raProperMotion", "D");
  PROMODEC_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "decProperMotion", "D");
  PARALLAX_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "parallax", "D");
  RADIALVELOCITY_FIELD=env->GetFieldID(CAT_ENTRY_CLASS, "radialVelocity", "D");
  
  OBJECT_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$CelestialObject"));
  TYPE_FIELD=env->GetFieldID(OBJECT_CLASS, "type", "S");
  NUMBER_FIELD=env->GetFieldID(OBJECT_CLASS, "number", "S");
  NAME_FIELD=env->GetFieldID(OBJECT_CLASS, "name", "Ljava/lang/String;");
  STAR_FIELD=env->GetFieldID(OBJECT_CLASS, "star", "Leu/cloudmakers/astronometry/NOVAS$CatalogEntry;");

  ON_SURFACE_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$PositionOnSurface"));
  LATITUDE_FIELD=env->GetFieldID(ON_SURFACE_CLASS, "latitude", "D");
  LONGITUDE_FIELD=env->GetFieldID(ON_SURFACE_CLASS, "longitude", "D");
  HEIGHT_FIELD=env->GetFieldID(ON_SURFACE_CLASS, "height", "D");
  TEMPERATURE_FIELD=env->GetFieldID(ON_SURFACE_CLASS, "temperature", "D");
  PRESSURE_FIELD=env->GetFieldID(ON_SURFACE_CLASS, "pressure", "D");
  
  IN_SPACE_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$PositionInSpace"));
  SC_POS_FIELD=env->GetFieldID(IN_SPACE_CLASS, "position", "[D");
  SC_VEL_FIELD=env->GetFieldID(IN_SPACE_CLASS, "velocity", "[D");
  
  OBSERVER_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$Observer"));
  WHERE_FIELD=env->GetFieldID(OBSERVER_CLASS, "where", "S");
  ON_SURF_FIELD=env->GetFieldID(OBSERVER_CLASS, "onSurface", "Leu/cloudmakers/astronometry/NOVAS$PositionOnSurface;");
  NEAR_EARTH_FIELD=env->GetFieldID(OBSERVER_CLASS, "nearEarth", "Leu/cloudmakers/astronometry/NOVAS$PositionInSpace;");
  
  SKY_POS_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$SkyPosition"));
  R_HAT_FIELD=env->GetFieldID(SKY_POS_CLASS, "rHat", "[D");
  RA__FIELD=env->GetFieldID(SKY_POS_CLASS, "ra", "D");
  DEC__FIELD=env->GetFieldID(SKY_POS_CLASS, "dec", "D");
  DIS_FIELD=env->GetFieldID(SKY_POS_CLASS, "distance", "D");
  RV_FIELD=env->GetFieldID(SKY_POS_CLASS, "radialVelocity", "D");
  
  DOUBLE_REF_CLASS=(jclass)env->NewGlobalRef(env->FindClass("eu/cloudmakers/astronometry/NOVAS$DoubleRef"));
  VALUE_FIELD=env->GetFieldID(DOUBLE_REF_CLASS, "value", "D");
  
  if (file != NULL) {
    has_eph=1;
  
    result=ephem_open (str=(char *)env->GetStringUTFChars(file, NULL), &jd_beg, &jd_end, &de_num);
    env->ReleaseStringUTFChars(file, str);
  
    env->SetStaticIntField(NOVAS_CLASS, EPH_DE_NO_FIELD, de_num);
    env->SetStaticDoubleField(NOVAS_CLASS, EPH_JD_BEGIN_FIELD, jd_beg);
    env->SetStaticDoubleField(NOVAS_CLASS, EPH_JD_END_FIELD, jd_end);
  }
  
  
  return result;
}


JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_place (JNIEnv *env, jclass, jdouble jd_tt, jobject cel_object, jobject location, jdouble delta_t, jint coord_sys, jint accuracy, jobject output) {
  short result;
  object _cel_object;
  observer _location;
  sky_pos _output;
  jobject star, on_surf, in_space;
  jstring string;
  const char *str;
  jdoubleArray array;
  jdouble *elements;

  memset(&_cel_object, 0, sizeof(object));

  _cel_object.type=env->GetShortField(cel_object, TYPE_FIELD);
  _cel_object.number=env->GetShortField(cel_object, NUMBER_FIELD);
  if ((string=(jstring)env->GetObjectField(cel_object, NAME_FIELD))!=NULL) {
    safe_strncpy(_cel_object.name, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((star=env->GetObjectField(cel_object, STAR_FIELD))!=NULL) {
    if ((string=(jstring)env->GetObjectField(star, STARNAME_FIELD))!=NULL) {
      safe_strncpy(_cel_object.star.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    if ((string=(jstring)env->GetObjectField(star, CATALOG_FIELD))!=NULL) {
      safe_strncpy(_cel_object.star.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    _cel_object.star.starnumber=env->GetIntField(star, STARNUMBER_FIELD);
    _cel_object.star.ra=env->GetDoubleField(star, RA_FIELD);
    _cel_object.star.dec=env->GetDoubleField(star, DEC_FIELD);
    _cel_object.star.promora=env->GetDoubleField(star, PROMORA_FIELD);
    _cel_object.star.promodec=env->GetDoubleField(star, PROMODEC_FIELD);
    _cel_object.star.parallax=env->GetDoubleField(star, PARALLAX_FIELD);
    _cel_object.star.radialvelocity=env->GetDoubleField(star, RADIALVELOCITY_FIELD);
  }
  
  _location.where=env->GetShortField(location, WHERE_FIELD);
  if ((on_surf=env->GetObjectField(location, ON_SURF_FIELD))!=NULL) {
    _location.on_surf.latitude=env->GetDoubleField(on_surf, LATITUDE_FIELD);
    _location.on_surf.longitude=env->GetDoubleField(on_surf, LONGITUDE_FIELD);
    _location.on_surf.height=env->GetDoubleField(on_surf, HEIGHT_FIELD);
    _location.on_surf.temperature=env->GetDoubleField(on_surf, TEMPERATURE_FIELD);
    _location.on_surf.pressure=env->GetDoubleField(on_surf, PRESSURE_FIELD);
  }
  if ((in_space=env->GetObjectField(location, NEAR_EARTH_FIELD))!=NULL) {
    if ((array=(jdoubleArray)env->GetObjectField(in_space, SC_POS_FIELD))!=NULL) {
      elements=env->GetDoubleArrayElements(array, NULL);
      _location.near_earth.sc_pos[0]=elements[0];
      _location.near_earth.sc_pos[1]=elements[1];
      _location.near_earth.sc_pos[2]=elements[2];
      env->ReleaseDoubleArrayElements(array, elements, 0);
    }
    if ((array=(jdoubleArray)env->GetObjectField(in_space, SC_VEL_FIELD))!=NULL) {
      elements=env->GetDoubleArrayElements(array, NULL);
      _location.near_earth.sc_vel[0]=elements[0];
      _location.near_earth.sc_vel[1]=elements[1];
      _location.near_earth.sc_vel[2]=elements[2];
      env->ReleaseDoubleArrayElements(array, elements, 0);
    }
  }
  
  result=place(jd_tt, &_cel_object, &_location, delta_t, coord_sys, accuracy, &_output);
  
  if ((array=(jdoubleArray)env->NewDoubleArray(3))!=NULL) {
    elements=env->GetDoubleArrayElements(array, NULL);
    elements[0]=_output.r_hat[0];
    elements[1]=_output.r_hat[1];
    elements[2]=_output.r_hat[2];
    env->ReleaseDoubleArrayElements(array, elements, 0);
    env->SetObjectField(output, R_HAT_FIELD, array);
  }
  env->SetDoubleField(output, RA__FIELD, _output.ra);
  env->SetDoubleField(output, DEC__FIELD, _output.dec);
  env->SetDoubleField(output, RA__FIELD, _output.ra);
  env->SetDoubleField(output, DIS_FIELD, _output.dis);
  env->SetDoubleField(output, RV_FIELD, _output.rv);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_transformCatalogEntry(JNIEnv *env, jclass, jint option, jdouble inDate, jobject inEntry, jdouble outDate, jstring outCatalog, jobject outEntry) {
  short result;
  cat_entry _inEntry, _outEntry;
  jstring string;
  const char *str;
  char _outCatalog[SIZE_OF_CAT_NAME]="";

  memset(&_inEntry, 0, sizeof(cat_entry));
  memset(&_outEntry, 0, sizeof(cat_entry));

  if ((string=(jstring)env->GetObjectField(inEntry, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_inEntry.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(inEntry, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_inEntry.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _inEntry.starnumber=env->GetIntField(inEntry, STARNUMBER_FIELD);
  _inEntry.ra=env->GetDoubleField(inEntry, RA_FIELD);
  _inEntry.dec=env->GetDoubleField(inEntry, DEC_FIELD);
  _inEntry.promora=env->GetDoubleField(inEntry, PROMORA_FIELD);
  _inEntry.promodec=env->GetDoubleField(inEntry, PROMODEC_FIELD);
  _inEntry.parallax=env->GetDoubleField(inEntry, PARALLAX_FIELD);
  _inEntry.radialvelocity=env->GetDoubleField(inEntry, RADIALVELOCITY_FIELD);

  if (outCatalog!=NULL) {
    safe_strncpy(_outCatalog, str=env->GetStringUTFChars(outCatalog, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(outCatalog, str);
  }

  result=transform_cat(option, inDate, &_inEntry, outDate, _outCatalog, &_outEntry);

  env->SetObjectField(outEntry, STARNAME_FIELD, env->NewStringUTF(_outEntry.starname));
  env->SetObjectField(outEntry, CATALOG_FIELD, env->NewStringUTF(_outEntry.catalog));
  env->SetIntField(outEntry, STARNUMBER_FIELD, _outEntry.starnumber);
  env->SetDoubleField(outEntry, RA_FIELD, _outEntry.ra);
  env->SetDoubleField(outEntry, DEC_FIELD, _outEntry.dec);
  env->SetDoubleField(outEntry, PROMORA_FIELD, _outEntry.promora);
  env->SetDoubleField(outEntry, PROMODEC_FIELD, _outEntry.promodec);
  env->SetDoubleField(outEntry, PARALLAX_FIELD, _outEntry.parallax);
  env->SetDoubleField(outEntry, RADIALVELOCITY_FIELD, _outEntry.radialvelocity);

  return result;
}


JNIEXPORT void JNICALL Java_eu_cloudmakers_astronometry_NOVAS_transformHipparcosEntry(JNIEnv *env, jclass, jobject hipparcos, jobject hip2000) {
  cat_entry _hipparcos, _hip2000;
  jstring string;
  const char *str;
  
  memset(&_hipparcos, 0, sizeof(cat_entry));
  memset(&_hip2000, 0, sizeof(cat_entry));
  
  if ((string=(jstring)env->GetObjectField(hipparcos, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_hipparcos.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(hipparcos, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_hipparcos.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _hipparcos.starnumber=env->GetIntField(hipparcos, STARNUMBER_FIELD);
  _hipparcos.ra=env->GetDoubleField(hipparcos, RA_FIELD);
  _hipparcos.dec=env->GetDoubleField(hipparcos, DEC_FIELD);
  _hipparcos.promora=env->GetDoubleField(hipparcos, PROMORA_FIELD);
  _hipparcos.promodec=env->GetDoubleField(hipparcos, PROMODEC_FIELD);
  _hipparcos.parallax=env->GetDoubleField(hipparcos, PARALLAX_FIELD);
  _hipparcos.radialvelocity=env->GetDoubleField(hipparcos, RADIALVELOCITY_FIELD);
  
  transform_hip(&_hipparcos, &_hip2000);
  
  env->SetObjectField(hip2000, STARNAME_FIELD, env->NewStringUTF(_hip2000.starname));
  env->SetObjectField(hip2000, CATALOG_FIELD, env->NewStringUTF(_hip2000.catalog));
  env->SetIntField(hip2000, STARNUMBER_FIELD, _hip2000.starnumber);
  env->SetDoubleField(hip2000, RA_FIELD, _hip2000.ra);
  env->SetDoubleField(hip2000, DEC_FIELD, _hip2000.dec);
  env->SetDoubleField(hip2000, PROMORA_FIELD, _hip2000.promora);
  env->SetDoubleField(hip2000, PROMODEC_FIELD, _hip2000.promodec);
  env->SetDoubleField(hip2000, PARALLAX_FIELD, _hip2000.parallax);
  env->SetDoubleField(hip2000, RADIALVELOCITY_FIELD, _hip2000.radialvelocity);
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_appStar(JNIEnv *env, jclass, jdouble ttDate, jobject entry, jint accuracy, jobject ra, jobject dec) {
  short result;
  cat_entry _entry;
  jstring string;
  const char *str;
  double _ra, _dec;
  
  memset(&_entry, 0, sizeof(cat_entry));
  
  if ((string=(jstring)env->GetObjectField(entry, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_entry.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(entry, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_entry.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _entry.starnumber=env->GetIntField(entry, STARNUMBER_FIELD);
  _entry.ra=env->GetDoubleField(entry, RA_FIELD);
  _entry.dec=env->GetDoubleField(entry, DEC_FIELD);
  _entry.promora=env->GetDoubleField(entry, PROMORA_FIELD);
  _entry.promodec=env->GetDoubleField(entry, PROMODEC_FIELD);
  _entry.parallax=env->GetDoubleField(entry, PARALLAX_FIELD);
  _entry.radialvelocity=env->GetDoubleField(entry, RADIALVELOCITY_FIELD);
  
  result=app_star(ttDate, &_entry, accuracy, &_ra, &_dec);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_virtualStar(JNIEnv *env, jclass, jdouble ttDate, jobject entry, jint accuracy, jobject ra, jobject dec) {
  short result;
  cat_entry _entry;
  jstring string;
  const char *str;
  double _ra, _dec;
  
  memset(&_entry, 0, sizeof(cat_entry));
  
  if ((string=(jstring)env->GetObjectField(entry, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_entry.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(entry, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_entry.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _entry.starnumber=env->GetIntField(entry, STARNUMBER_FIELD);
  _entry.ra=env->GetDoubleField(entry, RA_FIELD);
  _entry.dec=env->GetDoubleField(entry, DEC_FIELD);
  _entry.promora=env->GetDoubleField(entry, PROMORA_FIELD);
  _entry.promodec=env->GetDoubleField(entry, PROMODEC_FIELD);
  _entry.parallax=env->GetDoubleField(entry, PARALLAX_FIELD);
  _entry.radialvelocity=env->GetDoubleField(entry, RADIALVELOCITY_FIELD);
  
  result=virtual_star(ttDate, &_entry, accuracy, &_ra, &_dec);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_astroStar(JNIEnv *env, jclass, jdouble ttDate, jobject entry, jint accuracy, jobject ra, jobject dec) {
  short result;
  cat_entry _entry;
  jstring string;
  const char *str;
  double _ra, _dec;
  
  memset(&_entry, 0, sizeof(cat_entry));
  
  if ((string=(jstring)env->GetObjectField(entry, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_entry.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(entry, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_entry.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _entry.starnumber=env->GetIntField(entry, STARNUMBER_FIELD);
  _entry.ra=env->GetDoubleField(entry, RA_FIELD);
  _entry.dec=env->GetDoubleField(entry, DEC_FIELD);
  _entry.promora=env->GetDoubleField(entry, PROMORA_FIELD);
  _entry.promodec=env->GetDoubleField(entry, PROMODEC_FIELD);
  _entry.parallax=env->GetDoubleField(entry, PARALLAX_FIELD);
  _entry.radialvelocity=env->GetDoubleField(entry, RADIALVELOCITY_FIELD);
  
  result=astro_star(ttDate, &_entry, accuracy, &_ra, &_dec);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_localStar(JNIEnv *env, jclass, jdouble ttDate, jdouble deltaT, jobject entry, jobject position, jint accuracy, jobject ra, jobject dec) {
  short result;
  cat_entry _entry;
  on_surface _position;
  jstring string;
  const char *str;
  double _ra, _dec;
  
  memset(&_entry, 0, sizeof(cat_entry));
  
  if ((string=(jstring)env->GetObjectField(entry, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_entry.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(entry, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_entry.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _entry.starnumber=env->GetIntField(entry, STARNUMBER_FIELD);
  _entry.ra=env->GetDoubleField(entry, RA_FIELD);
  _entry.dec=env->GetDoubleField(entry, DEC_FIELD);
  _entry.promora=env->GetDoubleField(entry, PROMORA_FIELD);
  _entry.promodec=env->GetDoubleField(entry, PROMODEC_FIELD);
  _entry.parallax=env->GetDoubleField(entry, PARALLAX_FIELD);
  _entry.radialvelocity=env->GetDoubleField(entry, RADIALVELOCITY_FIELD);
  
  _position.latitude=env->GetDoubleField(position, LATITUDE_FIELD);
  _position.longitude=env->GetDoubleField(position, LONGITUDE_FIELD);
  _position.height=env->GetDoubleField(position, HEIGHT_FIELD);
  _position.temperature=env->GetDoubleField(position, TEMPERATURE_FIELD);
  _position.pressure=env->GetDoubleField(position, PRESSURE_FIELD);
  
  result=local_star(ttDate, deltaT, &_entry, &_position, accuracy, &_ra, &_dec);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_topoStar(JNIEnv *env, jclass, jdouble ttDate, jdouble deltaT, jobject entry, jobject position, jint accuracy, jobject ra, jobject dec) {
  short result;
  cat_entry _entry;
  on_surface _position;
  jstring string;
  const char *str;
  double _ra, _dec;
  
  memset(&_entry, 0, sizeof(cat_entry));
  
  if ((string=(jstring)env->GetObjectField(entry, STARNAME_FIELD))!=NULL) {
    safe_strncpy(_entry.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((string=(jstring)env->GetObjectField(entry, CATALOG_FIELD))!=NULL) {
    safe_strncpy(_entry.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  _entry.starnumber=env->GetIntField(entry, STARNUMBER_FIELD);
  _entry.ra=env->GetDoubleField(entry, RA_FIELD);
  _entry.dec=env->GetDoubleField(entry, DEC_FIELD);
  _entry.promora=env->GetDoubleField(entry, PROMORA_FIELD);
  _entry.promodec=env->GetDoubleField(entry, PROMODEC_FIELD);
  _entry.parallax=env->GetDoubleField(entry, PARALLAX_FIELD);
  _entry.radialvelocity=env->GetDoubleField(entry, RADIALVELOCITY_FIELD);
  
  _position.latitude=env->GetDoubleField(position, LATITUDE_FIELD);
  _position.longitude=env->GetDoubleField(position, LONGITUDE_FIELD);
  _position.height=env->GetDoubleField(position, HEIGHT_FIELD);
  _position.temperature=env->GetDoubleField(position, TEMPERATURE_FIELD);
  _position.pressure=env->GetDoubleField(position, PRESSURE_FIELD);
  
  result=topo_star(ttDate, deltaT, &_entry, &_position, accuracy, &_ra, &_dec);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_appPlanet(JNIEnv *env, jclass, jdouble ttDate, jobject body, jint accuracy, jobject ra, jobject dec, jobject distance) {
  short result;
  object _body;
  jobject star;
  jstring string;
  const char *str;
  double _ra, _dec, _distance;
  
  memset(&_body, 0, sizeof(object));

  _body.type=env->GetShortField(body, TYPE_FIELD);
  _body.number=env->GetShortField(body, NUMBER_FIELD);
  if ((string=(jstring)env->GetObjectField(body, NAME_FIELD))!=NULL) {
    safe_strncpy(_body.name, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((star=env->GetObjectField(body, STAR_FIELD))!=NULL) {
    if ((string=(jstring)env->GetObjectField(star, STARNAME_FIELD))!=NULL) {
      safe_strncpy(_body.star.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    if ((string=(jstring)env->GetObjectField(star, CATALOG_FIELD))!=NULL) {
      safe_strncpy(_body.star.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
       env->ReleaseStringUTFChars(string, str);
    }
    _body.star.starnumber=env->GetIntField(star, STARNUMBER_FIELD);
    _body.star.ra=env->GetDoubleField(star, RA_FIELD);
    _body.star.dec=env->GetDoubleField(star, DEC_FIELD);
    _body.star.promora=env->GetDoubleField(star, PROMORA_FIELD);
    _body.star.promodec=env->GetDoubleField(star, PROMODEC_FIELD);
    _body.star.parallax=env->GetDoubleField(star, PARALLAX_FIELD);
    _body.star.radialvelocity=env->GetDoubleField(star, RADIALVELOCITY_FIELD);
  }

  result=app_planet(ttDate, &_body, accuracy, &_ra, &_dec, &_distance);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  env->SetDoubleField(distance, VALUE_FIELD, _distance);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_virtualPlanet(JNIEnv *env, jclass, jdouble ttDate, jobject body, jint accuracy, jobject ra, jobject dec, jobject distance) {
  short result;
  object _body;
  jobject star;
  jstring string;
  const char *str;
  double _ra, _dec, _distance;
  
  memset(&_body, 0, sizeof(object));

  _body.type=env->GetShortField(body, TYPE_FIELD);
  _body.number=env->GetShortField(body, NUMBER_FIELD);
  if ((string=(jstring)env->GetObjectField(body, NAME_FIELD))!=NULL) {
    safe_strncpy(_body.name, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((star=env->GetObjectField(body, STAR_FIELD))!=NULL) {
    if ((string=(jstring)env->GetObjectField(star, STARNAME_FIELD))!=NULL) {
      safe_strncpy(_body.star.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    if ((string=(jstring)env->GetObjectField(star, CATALOG_FIELD))!=NULL) {
      safe_strncpy(_body.star.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
       env->ReleaseStringUTFChars(string, str);
    }
    _body.star.starnumber=env->GetIntField(star, STARNUMBER_FIELD);
    _body.star.ra=env->GetDoubleField(star, RA_FIELD);
    _body.star.dec=env->GetDoubleField(star, DEC_FIELD);
    _body.star.promora=env->GetDoubleField(star, PROMORA_FIELD);
    _body.star.promodec=env->GetDoubleField(star, PROMODEC_FIELD);
    _body.star.parallax=env->GetDoubleField(star, PARALLAX_FIELD);
    _body.star.radialvelocity=env->GetDoubleField(star, RADIALVELOCITY_FIELD);
  }

  result=virtual_planet(ttDate, &_body, accuracy, &_ra, &_dec, &_distance);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  env->SetDoubleField(distance, VALUE_FIELD, _distance);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_astroPlanet(JNIEnv *env, jclass, jdouble ttDate, jobject body, jint accuracy, jobject ra, jobject dec, jobject distance) {
  short result;
  object _body;
  jobject star;
  jstring string;
  const char *str;
  double _ra, _dec, _distance;
  
  memset(&_body, 0, sizeof(object));

  _body.type=env->GetShortField(body, TYPE_FIELD);
  _body.number=env->GetShortField(body, NUMBER_FIELD);
  if ((string=(jstring)env->GetObjectField(body, NAME_FIELD))!=NULL) {
    safe_strncpy(_body.name, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((star=env->GetObjectField(body, STAR_FIELD))!=NULL) {
    if ((string=(jstring)env->GetObjectField(star, STARNAME_FIELD))!=NULL) {
      safe_strncpy(_body.star.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    if ((string=(jstring)env->GetObjectField(star, CATALOG_FIELD))!=NULL) {
      safe_strncpy(_body.star.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
       env->ReleaseStringUTFChars(string, str);
    }
    _body.star.starnumber=env->GetIntField(star, STARNUMBER_FIELD);
    _body.star.ra=env->GetDoubleField(star, RA_FIELD);
    _body.star.dec=env->GetDoubleField(star, DEC_FIELD);
    _body.star.promora=env->GetDoubleField(star, PROMORA_FIELD);
    _body.star.promodec=env->GetDoubleField(star, PROMODEC_FIELD);
    _body.star.parallax=env->GetDoubleField(star, PARALLAX_FIELD);
    _body.star.radialvelocity=env->GetDoubleField(star, RADIALVELOCITY_FIELD);
  }

  result=astro_planet(ttDate, &_body, accuracy, &_ra, &_dec, &_distance);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  env->SetDoubleField(distance, VALUE_FIELD, _distance);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_localPlanet(JNIEnv *env, jclass, jdouble ttDate, jobject body, jdouble deltaT, jobject position, jint accuracy, jobject ra, jobject dec, jobject distance) {
  short result;
  object _body;
  on_surface _position;
  jobject star;
  jstring string;
  const char *str;
  double _ra, _dec, _distance;
  
  memset(&_body, 0, sizeof(object));

  _body.type=env->GetShortField(body, TYPE_FIELD);
  _body.number=env->GetShortField(body, NUMBER_FIELD);
  if ((string=(jstring)env->GetObjectField(body, NAME_FIELD))!=NULL) {
    safe_strncpy(_body.name, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((star=env->GetObjectField(body, STAR_FIELD))!=NULL) {
    if ((string=(jstring)env->GetObjectField(star, STARNAME_FIELD))!=NULL) {
      safe_strncpy(_body.star.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    if ((string=(jstring)env->GetObjectField(star, CATALOG_FIELD))!=NULL) {
      safe_strncpy(_body.star.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
       env->ReleaseStringUTFChars(string, str);
    }
    _body.star.starnumber=env->GetIntField(star, STARNUMBER_FIELD);
    _body.star.ra=env->GetDoubleField(star, RA_FIELD);
    _body.star.dec=env->GetDoubleField(star, DEC_FIELD);
    _body.star.promora=env->GetDoubleField(star, PROMORA_FIELD);
    _body.star.promodec=env->GetDoubleField(star, PROMODEC_FIELD);
    _body.star.parallax=env->GetDoubleField(star, PARALLAX_FIELD);
    _body.star.radialvelocity=env->GetDoubleField(star, RADIALVELOCITY_FIELD);
  }
  
  _position.latitude=env->GetDoubleField(position, LATITUDE_FIELD);
  _position.longitude=env->GetDoubleField(position, LONGITUDE_FIELD);
  _position.height=env->GetDoubleField(position, HEIGHT_FIELD);
  _position.temperature=env->GetDoubleField(position, TEMPERATURE_FIELD);
  _position.pressure=env->GetDoubleField(position, PRESSURE_FIELD);

  result=local_planet(ttDate, &_body, deltaT, &_position, accuracy, &_ra, &_dec, &_distance);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  env->SetDoubleField(distance, VALUE_FIELD, _distance);
  
  return result;
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_topoPlanet(JNIEnv *env, jclass, jdouble ttDate, jobject body, jdouble deltaT, jobject position, jint accuracy, jobject ra, jobject dec, jobject distance) {
  short result;
  object _body;
  on_surface _position;
  jobject star;
  jstring string;
  const char *str;
  double _ra, _dec, _distance;
  
  memset(&_body, 0, sizeof(object));

  _body.type=env->GetShortField(body, TYPE_FIELD);
  _body.number=env->GetShortField(body, NUMBER_FIELD);
  if ((string=(jstring)env->GetObjectField(body, NAME_FIELD))!=NULL) {
    safe_strncpy(_body.name, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
    env->ReleaseStringUTFChars(string, str);
  }
  if ((star=env->GetObjectField(body, STAR_FIELD))!=NULL) {
    if ((string=(jstring)env->GetObjectField(star, STARNAME_FIELD))!=NULL) {
      safe_strncpy(_body.star.starname, str=env->GetStringUTFChars(string, NULL), SIZE_OF_OBJ_NAME-1);
      env->ReleaseStringUTFChars(string, str);
    }
    if ((string=(jstring)env->GetObjectField(star, CATALOG_FIELD))!=NULL) {
      safe_strncpy(_body.star.catalog, str=env->GetStringUTFChars(string, NULL), SIZE_OF_CAT_NAME-1);
       env->ReleaseStringUTFChars(string, str);
    }
    _body.star.starnumber=env->GetIntField(star, STARNUMBER_FIELD);
    _body.star.ra=env->GetDoubleField(star, RA_FIELD);
    _body.star.dec=env->GetDoubleField(star, DEC_FIELD);
    _body.star.promora=env->GetDoubleField(star, PROMORA_FIELD);
    _body.star.promodec=env->GetDoubleField(star, PROMODEC_FIELD);
    _body.star.parallax=env->GetDoubleField(star, PARALLAX_FIELD);
    _body.star.radialvelocity=env->GetDoubleField(star, RADIALVELOCITY_FIELD);
  }
  
  _position.latitude=env->GetDoubleField(position, LATITUDE_FIELD);
  _position.longitude=env->GetDoubleField(position, LONGITUDE_FIELD);
  _position.height=env->GetDoubleField(position, HEIGHT_FIELD);
  _position.temperature=env->GetDoubleField(position, TEMPERATURE_FIELD);
  _position.pressure=env->GetDoubleField(position, PRESSURE_FIELD);

  result=topo_planet(ttDate, &_body, deltaT, &_position, accuracy, &_ra, &_dec, &_distance);
  
  env->SetDoubleField(ra, VALUE_FIELD, _ra);
  env->SetDoubleField(dec, VALUE_FIELD, _dec);
  env->SetDoubleField(distance, VALUE_FIELD, _distance);
  
  return result;
}

JNIEXPORT void JNICALL Java_eu_cloudmakers_astronometry_NOVAS_equatorialToHorizontal(JNIEnv *env, jclass, jdouble ut1Date, jdouble deltaT, jint accuracy, jdouble xp, jdouble yp, jobject position, jdouble ra, jdouble dec, jint refOption, jobject zd, jobject ad, jobject rar, jobject decr) {
  on_surface _position;
  double _zd, _ad, _rar, _decr;
  
  _position.latitude=env->GetDoubleField(position, LATITUDE_FIELD);
  _position.longitude=env->GetDoubleField(position, LONGITUDE_FIELD);
  _position.height=env->GetDoubleField(position, HEIGHT_FIELD);
  _position.temperature=env->GetDoubleField(position, TEMPERATURE_FIELD);
  _position.pressure=env->GetDoubleField(position, PRESSURE_FIELD);

  equ2hor(ut1Date, deltaT, accuracy, xp, yp, &_position, ra, dec, refOption, &_zd, &_ad, &_rar, &_decr);

  env->SetDoubleField(zd, VALUE_FIELD, _zd);
  env->SetDoubleField(ad, VALUE_FIELD, _ad);
  env->SetDoubleField(rar, VALUE_FIELD, _rar);
  env->SetDoubleField(decr, VALUE_FIELD, _decr);
}

JNIEXPORT jshort JNICALL Java_eu_cloudmakers_astronometry_NOVAS_siderealTime(JNIEnv *env, jclass, jdouble ttDate, jdouble deltaT, jint gstType, jint method, jint accuracy, jobject gst) {
  double _gst;
  short result;
  
  result = sidereal_time(ttDate, 0.0, deltaT, accuracy, gstType, method, &_gst);
  env->SetDoubleField(gst, VALUE_FIELD, _gst);

  return result;
}

short int solarsystem (double tjd, short int body, short int origin, double *position, double *velocity)
{
  if (has_eph)
    return solarsystem_1 (tjd, body, origin, position, velocity);
  return solarsystem_3 (tjd, body, origin, position, velocity);
}

short int solarsystem_hp (double tjd[2], short int body, short int origin, double *position, double *velocity)
{
  if (has_eph)
    return solarsystem_hp_1 (tjd, body, origin, position, velocity);
  return solarsystem_hp_3 (tjd, body, origin, position, velocity);
}
