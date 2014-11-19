package com.upokecenter.cbor;
/*
Written by Peter O. in 2014.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://upokecenter.com/d/
 */

  final class Base64 {
private Base64() {
}
    private static final String Base64URL =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    private static final String Base64Classic =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static void ToBase64(StringBuilder str, byte[] data, boolean padding) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      ToBase64(str, data, 0, data.length, Base64Classic, padding);
    }

    public static void ToBase64URL(
StringBuilder str,
byte[] data,
boolean padding) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      ToBase64(str, data, 0, data.length, Base64URL, padding);
    }

    public static void ToBase64(
StringBuilder str,
byte[] data,
int offset,
int count,
boolean padding) {
      ToBase64(str, data, offset, count, Base64Classic, padding);
    }

    public static void ToBase64URL(
StringBuilder str,
byte[] data,
int offset,
int count,
boolean padding) {
      ToBase64(str, data, offset, count, Base64URL, padding);
    }

    public static void WriteBase64(
StringOutput writer,
byte[] data,
int offset,
int count,
boolean padding) throws java.io.IOException {
      WriteBase64(writer, data, offset, count, Base64Classic, padding);
    }

    public static void WriteBase64URL(
StringOutput writer,
byte[] data,
int offset,
int count,
boolean padding) throws java.io.IOException {
      WriteBase64(writer, data, offset, count, Base64URL, padding);
    }

    public static String ToBase64String(byte[] data, boolean padding) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      return ToBase64String(data, 0, data.length, padding);
    }

    public static String ToBase64URLString(byte[] data, boolean padding) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      return ToBase64String(data, 0, data.length, padding);
    }

    public static String ToBase64String(
byte[] data,
int offset,
int count,
boolean padding) {
      StringBuilder builder = new StringBuilder();
      ToBase64(builder, data, offset, count, Base64Classic, padding);
      return builder.toString();
    }

    public static String ToBase64URLString(
byte[] data,
int offset,
int count,
boolean padding) {
      StringBuilder builder = new StringBuilder();
      ToBase64(builder, data, offset, count, Base64Classic, padding);
      return builder.toString();
    }

    private static void ToBase64(
StringBuilder str,
byte[] data,
int offset,
int count,
String alphabet,
boolean padding) {
      if (str == null) {
        throw new NullPointerException("str");
      }
      if (data == null) {
        throw new NullPointerException("data");
      }
      if (offset < 0) {
        throw new IllegalArgumentException(
          "offset (" + offset + ") is less than " + "0 ");
      }
      if (offset > data.length) {
        throw new IllegalArgumentException(
          "offset (" + offset + ") is more than " + data.length);
      }
      if (count < 0) {
        throw new IllegalArgumentException("count (" + count + ") is less than " +
                              "0 ");
      }
      if (count > data.length) {
        throw new IllegalArgumentException(
          "count (" + count + ") is more than " + data.length);
      }
      if (data.length - offset < count) {
        throw new IllegalArgumentException("data's length minus " + offset + " (" +
                (data.length - offset) + ") is less than " + count);
      }
      int length = offset + count;
      int i = offset;
      for (i = offset; i < (length - 2); i += 3) {
        str.append(alphabet.charAt((data[i] >> 2) & 63));
        str.append(alphabet.charAt(((data[i] & 3) << 4) + ((data[i + 1] >> 4) & 15)));
        str.append(alphabet.charAt(((data[i + 1] & 15) << 2) + ((data[i + 2] >> 6) &
                              3)));
        str.append(alphabet.charAt(data[i + 2] & 63));
      }
      int lenmod3 = count % 3;
      if (lenmod3 != 0) {
        i = length - lenmod3;
        str.append(alphabet.charAt((data[i] >> 2) & 63));
        if (lenmod3 == 2) {
          str.append(alphabet.charAt(((data[i] & 3) << 4) + ((data[i + 1] >> 4) &
                              15)));
          str.append(alphabet.charAt((data[i + 1] & 15) << 2));
          if (padding) {
            str.append("=");
          }
        } else {
          str.append(alphabet.charAt((data[i] & 3) << 4));
          if (padding) {
            str.append("==");
          }
        }
      }
    }

    private static void WriteBase64(
StringOutput writer,
byte[] data,
int offset,
int count,
String alphabet,
boolean padding) throws java.io.IOException {
      if (writer == null) {
        throw new NullPointerException("writer");
      }
      if (offset < 0) {
        throw new IllegalArgumentException("offset (" + offset + ") is less than " +
                              "0 ");
      }
      if (offset > data.length) {
        throw new IllegalArgumentException("offset (" + offset + ") is more than " +
                              data.length);
      }
      if (count < 0) {
        throw new IllegalArgumentException("count (" + count + ") is less than " +
                              "0 ");
      }
      if (count > data.length) {
        throw new IllegalArgumentException("count (" + count + ") is more than " +
                              data.length);
      }
      if (data.length - offset < count) {
        throw new IllegalArgumentException("data's length minus " + offset + " (" +
                (data.length - offset) + ") is less than " + count);
      }
      int length = offset + count;
      int i = offset;
      char[] buffer = new char[4];
      for (i = offset; i < (length - 2); i += 3) {
        buffer[0] = (char)alphabet.charAt((data[i] >> 2) & 63);
        buffer[1] = (char)alphabet.charAt(((data[i] & 3) << 4) +
                ((data[i + 1] >> 4) & 15));
        buffer[2] = (char)alphabet.charAt(((data[i + 1] & 15) << 2) + ((data[i +
                2] >> 6) & 3));
        buffer[3] = (char)alphabet.charAt(data[i + 2] & 63);
        writer.WriteChar(buffer[0]);
        writer.WriteChar(buffer[1]);
        writer.WriteChar(buffer[2]);
        writer.WriteChar(buffer[3]);
      }
      int lenmod3 = count % 3;
      if (lenmod3 != 0) {
        i = length - lenmod3;
        buffer[0] = (char)alphabet.charAt((data[i] >> 2) & 63);
        if (lenmod3 == 2) {
          buffer[1] = (char)alphabet.charAt(((data[i] & 3) << 4) + ((data[i + 1] >>
                4) & 15));
          buffer[2] = (char)alphabet.charAt((data[i + 1] & 15) << 2);
          writer.WriteChar(buffer[0]);
          writer.WriteChar(buffer[1]);
          writer.WriteChar(buffer[2]);
          if (padding) {
            writer.WriteChar('=');
          }
        } else {
          buffer[1] = (char)alphabet.charAt((data[i] & 3) << 4);
          writer.WriteChar(buffer[0]);
          writer.WriteChar(buffer[1]);
          if (padding) {
            writer.WriteChar('=');
            writer.WriteChar('=');
          }
        }
      }
    }
  }
