package nucleus5.view;

import android.os.Parcel;

/**
 * 序列化，反序列化工具类
 */
class ParcelFn {

  private static final ClassLoader CLASS_LOADER = ParcelFn.class.getClassLoader();

  static <T> T unmarshall(byte[] array) {
    Parcel parcel = Parcel.obtain();
    parcel.unmarshall(array, 0, array.length);
    parcel.setDataPosition(0);
    Object value = parcel.readValue(CLASS_LOADER);
    parcel.recycle();
    return (T) value;
  }

  static byte[] marshall(Object o) {
    Parcel parcel = Parcel.obtain();
    parcel.writeValue(o);
    byte[] result = parcel.marshall();
    parcel.recycle();
    return result;
  }
}