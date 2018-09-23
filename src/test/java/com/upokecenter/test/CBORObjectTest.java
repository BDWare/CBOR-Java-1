package com.upokecenter.test;

import java.util.*;
import java.io.*;
import org.junit.Assert;
import org.junit.Test;
import com.upokecenter.util.*;
import com.upokecenter.cbor.*;
import com.upokecenter.numbers.*;

  public class CBORObjectTest {
    private static final String[] ValueJsonFails = {
      "\"\\uxxxx\"",
      "\"\\ud800\udc00\"",
      "\"\ud800\\udc00\"", "\"\\U0023\"", "\"\\u002x\"", "\"\\u00xx\"",
      "\"\\u0xxx\"", "\"\\u0\"", "\"\\u00\"", "\"\\u000\"", "trbb",
      "trub", "falsb", "nulb", "[true", "[true,", "[true]!",
      "[\"\ud800\\udc00\"]", "[\"\\ud800\udc00\"]",
      "[\"\\udc00\ud800\udc00\"]", "[\"\\ud800\ud800\udc00\"]",
      "[\"\\ud800\"]", "[1,2,", "[1,2,3", "{,\"0\":0,\"1\":1}",
      "{\"0\":0,,\"1\":1}", "{\"0\":0,\"1\":1,}", "[,0,1,2]", "[0,,1,2]",
  "[0,1,,2]", "[0,1,2,]", "[0001]", "{a:true}",
      "{\"a\"://comment\ntrue}", "{\"a\":/*comment*/true}", "{'a':true}",
      "{\"a\":'b'}", "{\"a\t\":true}", "{\"a\r\":true}", "{\"a\n\":true}",
  "['a']", "{\"a\":\"a\t\"}", "[\"a\\'\"]", "[NaN]", "[+Infinity]",
  "[-Infinity]", "[Infinity]", "{\"a\":\"a\r\"}", "{\"a\":\"a\n\"}",
  "[\"a\t\"]", "\"test\"\"", "\"test\"x", "\"test\"\u0300",
      "\"test\"\u0005", "[5]\"", "[5]x", "[5]\u0300", "[5]\u0005",
      "{\"test\":5}\"", "{\"test\":5}x", "{\"test\":5}\u0300",
      "{\"test\":5}\u0005", "true\"", "truex", "true}", "true\u0300",
      "true\u0005", "8024\"", "8024x", "8024}", "8024\u0300",
      "8024\u0005", "{\"test\":5}}", "{\"test\":5}{", "[5]]", "[5][",
      "0000", "0x1", "0xf", "0x20", "0x01",
  "-3x", "-3e89x",
      "0X1", "0Xf", "0X20", "0X01", ".2", ".05", "-.2",
      "-.05", "23.", "23.e0", "23.e1", "0.", "[0000]", "[0x1]",
      "[0xf]", "[0x20]", "[0x01]", "[.2]", "[.05]", "[-.2]", "[-.05]",
  "[23.]", "[23.e0]", "[23.e1]", "[0.]", "\"abc", "\"ab\u0004c\"",
  "\u0004\"abc\"", "[1,\u0004" + "2]" };

    private static final String[] ValueJsonSucceeds = {
      "[0]",
      "[0.1]",
      "[0.1001]",
      "[0.0]",
      "[-3 " + ",-5]",
  "[0.00]", "[0.000]", "[0.01]", "[0.001]", "[0.5]", "[0E5]",
  "[0E+6]", "[\"\ud800\udc00\"]", "[\"\\ud800\\udc00\"]",
  "[\"\\ud800\\udc00\ud800\udc00\"]", "23.0e01", "23.0e00", "[23.0e01]",
  "[23.0e00]", "0", "1", "0.2", "0.05", "-0.2", "-0.05" };

    private static final CBOREncodeOptions ValueNoDuplicateKeys = new
      CBOREncodeOptions(true, false);

    static void CheckPropertyNames(
  Object ao,
  PODOptions cc,
  String p1,
  String p2,
  String p3) {
      CBORObjectTest.CheckPropertyNames(
 CBORObject.FromObject(ao, cc),
 p1,
 p2,
 p3);
    }

    static void CheckArrayPropertyNames(
  CBORObject co,
  int expectedCount,
  String p1,
  String p2,
  String p3) {
      Assert.assertEquals(CBORType.Array, co.getType());
      Assert.assertEquals(expectedCount, co.size());
      for (int i = 0; i < co.size(); ++i) {
        CBORObjectTest.CheckPropertyNames(co.get(i), p1, p2, p3);
      }
      CBORTestCommon.AssertRoundTrip(co);
    }

    static void CheckPODPropertyNames(
  CBORObject co,
  PODOptions cc,
  String p1,
  String p2,
  String p3) {
      Assert.assertEquals(CBORType.Map, co.getType());
      String keyName = cc.getUseCamelCase() ? "propValue" : "PropValue";
      if (!co.ContainsKey(keyName)) {
        Assert.fail("Expected " + keyName + " to exist: " + co.toString());
      }
      CBORObjectTest.CheckPropertyNames(co.get(keyName), p1, p2, p3);
    }

    static void CheckPODInDictPropertyNames(
  CBORObject co,
  String p1,
  String p2,
  String p3) {
      Assert.assertEquals(CBORType.Map, co.getType());
      if (!co.ContainsKey("PropValue")) {
        Assert.fail("Expected PropValue to exist: " + co.toString());
      }
      CBORObjectTest.CheckPropertyNames(co.get("PropValue"), p1, p2, p3);
    }

    static void CheckPropertyNames(
      CBORObject o,
      String p1,
      String p2,
      String p3) {
if (o.ContainsKey("PrivatePropA")) {
 Assert.fail();
 }
if (o.ContainsKey("privatePropA")) {
 Assert.fail();
 }
if (o.ContainsKey("StaticPropA")) {
 Assert.fail();
 }
if (o.ContainsKey("staticPropA")) {
 Assert.fail();
 }
      Assert.assertEquals(CBORType.Map, o.getType());
      if (!o.ContainsKey(p1)) {
        Assert.fail("Expected " + p1 + " to exist: " + o.toString());
      }
      if (!o.ContainsKey(p2)) {
        Assert.fail("Expected " + p2 + " to exist: " + o.toString());
      }
      if (!o.ContainsKey(p3)) {
        Assert.fail("Expected " + p3 + " to exist: " + o.toString());
      }
      CBORTestCommon.AssertRoundTrip(o);
    }

    static void CheckPropertyNames(Object ao) {
      PODOptions valueCcTF = new PODOptions(true, false);
      PODOptions valueCcFF = new PODOptions(false, false);
      PODOptions valueCcFT = new PODOptions(false, true);
      PODOptions valueCcTT = new PODOptions(true, true);
      CBORObjectTest.CheckPropertyNames(
    ao,
    valueCcTF,
    "PropA",
    "PropB",
    "PropC");
      /*
TODO: The following case conflicts with the Java version
of the CBOR library. Resolving this conflict may result in the
Java version being backward-incompatible and so require
a major version change.
//--
       CBORObjectTest.CheckPropertyNames(
  ao,
  valueCcFF,
  "PropA",
  "PropB",
  "IsPropC");
     */ CBORObjectTest.CheckPropertyNames(
  ao,
  valueCcFT,
  "propA",
  "propB",
  "isPropC");
      CBORObjectTest.CheckPropertyNames(
    ao,
    valueCcTT,
    "propA",
    "propB",
    "propC");
    }

    public static CBORObject GetNumberData() {
      return new AppResources("Resources").GetJSON("numbers");
    }

    public static void TestFailingJSON(String str) {
      TestFailingJSON(str, new CBOREncodeOptions(true, true));
    }

    public static void TestFailingJSON(String str, CBOREncodeOptions opt) {
      byte[] bytes = null;
      try {
        bytes = DataUtilities.GetUtf8Bytes(str, false);
      } catch (IllegalArgumentException ex2) {
        System.out.println(ex2.getMessage());
        // Check only FromJSONString
        try {
          CBORObject.FromJSONString(str, opt);
          Assert.fail("Should have failed");
        } catch (CBORException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        return;
      }
      {
java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(bytes);

        try {
          CBORObject.ReadJSON(ms, opt);
          Assert.fail("Should have failed");
        } catch (CBORException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(str + "\r\n" + ex.toString());
          throw new IllegalStateException("", ex);
        }
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
}
      try {
        CBORObject.FromJSONString(str, opt);
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    public static CBORObject TestSucceedingJSON(String str) {
      return TestSucceedingJSON(str, null);
    }

    public static CBORObject TestSucceedingJSON(
  String str,
  CBOREncodeOptions options) {
      byte[] bytes = DataUtilities.GetUtf8Bytes(str, false);
      try {
        {
java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(bytes);

          CBORObject obj = options == null ? CBORObject.ReadJSON(ms) :
                    CBORObject.ReadJSON(ms, options);
          CBORObject obj2 = options == null ? CBORObject.FromJSONString(str) :
                    CBORObject.FromJSONString(str, options);
          TestCommon.CompareTestEqualAndConsistent(
            obj,
            obj2);
          CBORTestCommon.AssertRoundTrip(obj);
          return obj;
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
}
      } catch (Exception ex) {
        Assert.fail(ex.toString() + "\n" + str);
        throw new IllegalStateException("", ex);
      }
    }

    public String CharString(int cp, boolean quoted, char[] charbuf) {
      int index = 0;
      if (quoted) {
        charbuf[index++] = (char)0x22;
      }
      if (cp < 0x10000) {
        if (cp >= 0xd800 && cp < 0xe000) {
          return null;
        }
        charbuf[index++] = (char)cp;
        if (quoted) {
          charbuf[index++] = (char)0x22;
        }
        return new String(charbuf, 0, index);
      } else {
        cp -= 0x10000;
        charbuf[index++] = (char)((cp >> 10) + 0xd800);
        charbuf[index++] = (char)((cp & 0x3ff) + 0xdc00);
        if (quoted) {
          charbuf[index++] = (char)0x22;
        }
        return new String(charbuf, 0, index);
      }
    }
    @Test
    public void TestAbs() {
      Assert.assertEquals(
        CBORObject.FromObject(2),
        CBORObject.FromObject(-2).Abs());
      Assert.assertEquals(
        CBORObject.FromObject(2),
        CBORObject.FromObject(2).Abs());
      Assert.assertEquals(
        CBORObject.FromObject(2.5),
        CBORObject.FromObject(-2.5).Abs());
      Assert.assertEquals(
        CBORObject.FromObject(EDecimal.FromString("6.63")),
        CBORObject.FromObject(EDecimal.FromString("-6.63")).Abs());
      Assert.assertEquals(
        CBORObject.FromObject(EFloat.FromString("2.75")),
        CBORObject.FromObject(EFloat.FromString("-2.75")).Abs());
      Assert.assertEquals(
        CBORObject.FromObject(ERational.FromDouble(2.5)),
        CBORObject.FromObject(ERational.FromDouble(-2.5)).Abs());
    }
    @Test
    public void TestAdd() {
      CBORObject cbor = CBORObject.NewMap();
      CBORObject cborNull = CBORObject.Null;
      cbor.Add(null, true);
      Assert.assertEquals(CBORObject.True, cbor.get(cborNull));
      cbor.Add("key", null);
      Assert.assertEquals(CBORObject.Null, cbor.get("key"));
    }
    @Test
    public void TestAddConverter() {
      // not implemented yet
    }

    private static EDecimal AsED(CBORObject obj) {
      return obj.AsEDecimal();
    }
    @Test
    public void TestAddition() {
      RandomGenerator r = new RandomGenerator();
      for (int i = 0; i < 1000; ++i) {
        CBORObject o1 = CBORTestCommon.RandomNumber(r);
        CBORObject o2 = CBORTestCommon.RandomNumber(r);
        EDecimal cmpDecFrac = AsED(o1).Add(AsED(o2));
        EDecimal cmpCobj = AsED(CBORObject.Addition(o1, o2));
        TestCommon.CompareTestEqual(cmpDecFrac, cmpCobj);
        CBORTestCommon.AssertRoundTrip(o1);
        CBORTestCommon.AssertRoundTrip(o2);
      }
      try {
        CBORObject.Addition(null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Addition(CBORObject.FromObject(2), null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Addition(CBORObject.Null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Addition(CBORObject.FromObject(2), CBORObject.Null);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestAddTagHandler() {
      // not implemented yet
    }

    @Test
    public void TestAsEInteger() {
      try {
        CBORObject.FromObject((Object)null).AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Null.AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewArray().AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsEInteger();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        String numberString = numberinfo.get("number").AsString();
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(numberString));
        if (!numberinfo.get("integer").equals(CBORObject.Null)) {
          Assert.assertEquals(
            numberinfo.get("integer").AsString(),
            cbornumber.AsEInteger().toString());
        } else {
          try {
            cbornumber.AsEInteger();
            Assert.fail("Should have failed");
          } catch (ArithmeticException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
        }
      }

      {
        String stringTemp =
          CBORObject.FromObject((float)0.75).AsEInteger().toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((float)0.99).AsEInteger().toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((float)0.0000000000000001).AsEInteger()
                .toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((float)0.5).AsEInteger().toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((float)1.5).AsEInteger().toString();
        Assert.assertEquals(
        "1",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((float)2.5).AsEInteger().toString();
        Assert.assertEquals(
        "2",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((float)328323f).AsEInteger().toString();
        Assert.assertEquals(
        "328323",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)0.75).AsEInteger().toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)0.99).AsEInteger().toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)0.0000000000000001).AsEInteger()
                .toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)0.5).AsEInteger().toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)1.5).AsEInteger().toString();
        Assert.assertEquals(
        "1",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)2.5).AsEInteger().toString();
        Assert.assertEquals(
        "2",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject((double)328323).AsEInteger().toString();
        Assert.assertEquals(
        "328323",
        stringTemp);
      }
      try {
        CBORObject.FromObject(Float.POSITIVE_INFINITY).AsEInteger();
        Assert.fail("Should have failed");
      } catch (ArithmeticException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(Float.NEGATIVE_INFINITY).AsEInteger();
        Assert.fail("Should have failed");
      } catch (ArithmeticException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(Float.NaN).AsEInteger();
        Assert.fail("Should have failed");
      } catch (ArithmeticException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(Double.POSITIVE_INFINITY).AsEInteger();
        Assert.fail("Should have failed");
      } catch (ArithmeticException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(Double.NEGATIVE_INFINITY).AsEInteger();
        Assert.fail("Should have failed");
      } catch (ArithmeticException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(Double.NaN).AsEInteger();
        Assert.fail("Should have failed");
      } catch (ArithmeticException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
    }
    @Test
    public void TestAsBoolean() {
      if (!(CBORObject.True.AsBoolean())) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(0).AsBoolean())) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject("").AsBoolean())) {
 Assert.fail();
 }
      if (CBORObject.False.AsBoolean()) {
 Assert.fail();
 }
      if (CBORObject.Null.AsBoolean()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.AsBoolean()) {
 Assert.fail();
 }
      if (!(CBORObject.NewArray().AsBoolean())) {
 Assert.fail();
 }
      if (!(CBORObject.NewMap().AsBoolean())) {
 Assert.fail();
 }
    }
    @Test
    public void TestAsByte() {
      try {
        CBORObject.NewArray().AsByte();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsByte();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsByte();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsByte();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsByte();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsByte();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (numberinfo.get("byte").AsBoolean()) {
          Assert.assertEquals(
    TestCommon.StringToInt(numberinfo.get("integer").AsString()),
    ((int)cbornumber.AsByte()) & 0xff);
        } else {
          try {
            cbornumber.AsByte();
            Assert.fail("Should have failed " + cbornumber);
          } catch (ArithmeticException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString() + cbornumber);
            throw new IllegalStateException("", ex);
          }
        }
      }
      for (int i = 0; i < 255; ++i) {
        Assert.assertEquals((byte)i, CBORObject.FromObject(i).AsByte());
      }
      for (int i = -200; i < 0; ++i) {
        try {
          CBORObject.FromObject(i).AsByte();
        } catch (ArithmeticException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString()); throw new
            IllegalStateException("", ex);
        }
      }
      for (int i = 256; i < 512; ++i) {
        try {
          CBORObject.FromObject(i).AsByte();
        } catch (ArithmeticException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString()); throw new
            IllegalStateException("", ex);
        }
      }
    }

    @Test
    public void TestAsDouble() {
      try {
        CBORObject.NewArray().AsDouble();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsDouble();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsDouble();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsDouble();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsDouble();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsDouble();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        AreEqualExact(
  (double)EDecimal.FromString(numberinfo.get("number").AsString()).ToDouble(),
  cbornumber.AsDouble());
      }
    }

    @Test
    public void TestAsEDecimal() {
      Assert.assertEquals(
        CBORTestCommon.DecPosInf,
        CBORObject.FromObject(Float.POSITIVE_INFINITY).AsEDecimal());
      Assert.assertEquals(
        CBORTestCommon.DecNegInf,
        CBORObject.FromObject(Float.NEGATIVE_INFINITY).AsEDecimal());
      {
        String stringTemp =
          CBORObject.FromObject(Float.NaN).AsEDecimal().toString();
        Assert.assertEquals(
        "NaN",
        stringTemp);
      }
      Assert.assertEquals(
        CBORTestCommon.DecPosInf,
        CBORObject.FromObject(Double.POSITIVE_INFINITY).AsEDecimal());
      Assert.assertEquals(
        CBORTestCommon.DecNegInf,
        CBORObject.FromObject(Double.NEGATIVE_INFINITY).AsEDecimal());
      {
        Object objectTemp = "NaN";
        Object objectTemp2 = CBORObject.FromObject(Double.NaN).AsEDecimal()
                    .toString();
        Assert.assertEquals(objectTemp, objectTemp2);
      }
      try {
        CBORObject.NewArray().AsEDecimal();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsEDecimal();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsEDecimal();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsEDecimal();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsEDecimal();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsEDecimal();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestAsEFloat() {
      Assert.assertEquals(
        CBORTestCommon.FloatPosInf,
        CBORObject.FromObject(Float.POSITIVE_INFINITY).AsEFloat());
      Assert.assertEquals(
        CBORTestCommon.FloatNegInf,
        CBORObject.FromObject(Float.NEGATIVE_INFINITY).AsEFloat());
      if (!(CBORObject.FromObject(Float.NaN).AsEFloat()
                    .IsNaN())) {
 Assert.fail();
 }
      Assert.assertEquals(
        CBORTestCommon.FloatPosInf,
        CBORObject.FromObject(Double.POSITIVE_INFINITY).AsEFloat());
      Assert.assertEquals(
        CBORTestCommon.FloatNegInf,
        CBORObject.FromObject(Double.NEGATIVE_INFINITY).AsEFloat());
      if (!(CBORObject.FromObject(Double.NaN).AsEFloat()
                    .IsNaN())) {
 Assert.fail();
 }
    }
    @Test
    public void TestAsERational() {
      Assert.assertEquals(
        CBORTestCommon.RatPosInf,
        CBORObject.FromObject(Float.POSITIVE_INFINITY).AsERational());
      Assert.assertEquals(
        CBORTestCommon.RatNegInf,
        CBORObject.FromObject(Float.NEGATIVE_INFINITY).AsERational());
      if (!(CBORObject.FromObject(CBORObject.FromObject(Float.NaN)
        .AsERational()).IsNaN()))Assert.fail();
      Assert.assertEquals(
        CBORTestCommon.RatPosInf,
        CBORObject.FromObject(Double.POSITIVE_INFINITY).AsERational());
      Assert.assertEquals(
        CBORTestCommon.RatNegInf,
        CBORObject.FromObject(Double.NEGATIVE_INFINITY).AsERational());
      if (!(
  CBORObject.FromObject(CBORObject.FromObject(Double.NaN)
          .AsERational()).IsNaN()))Assert.fail();
    }
    @Test
    public void TestAsInt16() {
      try {
        CBORObject.NewArray().AsInt16();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsInt16();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsInt16();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsInt16();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsInt16();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsInt16();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(
            EDecimal.FromString(numberinfo.get("number").AsString()));
        if (numberinfo.get("int16").AsBoolean()) {
          Assert.assertEquals(
    TestCommon.StringToInt(numberinfo.get("integer").AsString()),
    cbornumber.AsInt16());
        } else {
          try {
            cbornumber.AsInt16();
            Assert.fail("Should have failed " + cbornumber);
          } catch (ArithmeticException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString() + cbornumber);
            throw new IllegalStateException("", ex);
          }
        }
      }
    }

    @Test
    public void TestAsInt32() {
      try {
        CBORObject.NewArray().AsInt32();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsInt32();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsInt32();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsInt32();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsInt32();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsInt32();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        EDecimal edec =
          EDecimal.FromString(numberinfo.get("number").AsString());
        CBORObject cbornumber = CBORObject.FromObject(edec);
        boolean isdouble = numberinfo.get("double").AsBoolean();
        CBORObject cbornumberdouble = CBORObject.FromObject(edec.ToDouble());
        boolean issingle = numberinfo.get("single").AsBoolean();
        CBORObject cbornumbersingle = CBORObject.FromObject(edec.ToSingle());
        if (numberinfo.get("int32").AsBoolean()) {
          Assert.assertEquals(
    TestCommon.StringToInt(numberinfo.get("integer").AsString()),
    cbornumber.AsInt32());
          if (isdouble) {
            Assert.assertEquals(
    TestCommon.StringToInt(numberinfo.get("integer").AsString()),
    cbornumberdouble.AsInt32());
          }
          if (issingle) {
            Assert.assertEquals(
    TestCommon.StringToInt(numberinfo.get("integer").AsString()),
    cbornumbersingle.AsInt32());
          }
        } else {
          try {
            cbornumber.AsInt32();
            Assert.fail("Should have failed " + cbornumber);
          } catch (ArithmeticException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString() + cbornumber);
            throw new IllegalStateException("", ex);
          }
          if (isdouble) {
            try {
              cbornumberdouble.AsInt32();
              Assert.fail("Should have failed");
            } catch (ArithmeticException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
          }
          if (issingle) {
            try {
              cbornumbersingle.AsInt32();
              Assert.fail("Should have failed");
            } catch (ArithmeticException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
          }
        }
      }
    }
    @Test
    public void TestAsInt64() {
      try {
        CBORObject.NewArray().AsInt64();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsInt64();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsInt64();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsInt64();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsInt64();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsInt64();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        EDecimal edec =
          EDecimal.FromString(numberinfo.get("number").AsString());
        CBORObject cbornumber = CBORObject.FromObject(edec);
        boolean isdouble = numberinfo.get("double").AsBoolean();
        CBORObject cbornumberdouble = CBORObject.FromObject(edec.ToDouble());
        boolean issingle = numberinfo.get("single").AsBoolean();
        CBORObject cbornumbersingle = CBORObject.FromObject(edec.ToSingle());
        if (numberinfo.get("int64").AsBoolean()) {
          Assert.assertEquals(
   TestCommon.StringToLong(numberinfo.get("integer").AsString()),
   cbornumber.AsInt64());
          if (isdouble) {
            Assert.assertEquals(
   TestCommon.StringToLong(numberinfo.get("integer").AsString()),
   cbornumberdouble.AsInt64());
          }
          if (issingle) {
            Assert.assertEquals(
   TestCommon.StringToLong(numberinfo.get("integer").AsString()),
   cbornumbersingle.AsInt64());
          }
        } else {
          try {
            cbornumber.AsInt64();
            Assert.fail("Should have failed " + cbornumber);
          } catch (ArithmeticException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString() + cbornumber);
            throw new IllegalStateException("", ex);
          }
          if (isdouble) {
            try {
              cbornumberdouble.AsInt64();
              Assert.fail("Should have failed");
            } catch (ArithmeticException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
          }
          if (issingle) {
            try {
              cbornumbersingle.AsInt64();
              Assert.fail("Should have failed");
            } catch (ArithmeticException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
          }
        }
      }
    }
    @Test
    public void TestAsSByte() {
      // not implemented yet
    }
    @Test
    public void TestAsSingle() {
      try {
        CBORObject.NewArray().AsSingle();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsSingle();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.AsSingle();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.AsSingle();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Undefined.AsSingle();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("").AsSingle();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        AreEqualExact(
  (float)EDecimal.FromString(numberinfo.get("number").AsString()).ToSingle(),
  cbornumber.AsSingle());
      }
    }
    @Test
    public void TestAsString() {
      {
        String stringTemp = CBORObject.FromObject("test").AsString();
        Assert.assertEquals(
        "test",
        stringTemp);
      }
      try {
        CBORObject.FromObject(CBORObject.Null).AsString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(true).AsString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(false).AsString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(5).AsString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewArray().AsString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().AsString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestAsUInt16() {
      // not implemented yet
    }
    @Test
    public void TestAsUInt32() {
      // not implemented yet
    }
    @Test
    public void TestAsUInt64() {
      // not implemented yet
    }
    @Test
    public void TestCanFitInDouble() {
      if (!(CBORObject.FromObject(0).CanFitInDouble())) {
 Assert.fail();
 }
      if (CBORObject.True.CanFitInDouble()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").CanFitInDouble()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanFitInDouble()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanFitInDouble()) {
 Assert.fail();
 }
      if (CBORObject.False.CanFitInDouble()) {
 Assert.fail();
 }
      if (CBORObject.Null.CanFitInDouble()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.CanFitInDouble()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (numberinfo.get("double").AsBoolean()) {
          if (!(cbornumber.CanFitInDouble())) {
 Assert.fail();
 }
        } else {
          if (cbornumber.CanFitInDouble()) {
 Assert.fail();
 }
        }
      }
      RandomGenerator rand = new RandomGenerator();
      for (int i = 0; i < 2047; ++i) {
        // Try a random double with a given
        // exponent
        if (!(
  CBORObject.FromObject(
  RandomObjects.RandomDouble(
  rand,
  i)).CanFitInDouble()))Assert.fail();
      }
    }
    @Test
    public void TestCanFitInInt32() {
      if (!(CBORObject.FromObject(0).CanFitInInt32())) {
 Assert.fail();
 }
      if (CBORObject.True.CanFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").CanFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.False.CanFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.Null.CanFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.CanFitInInt32()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (numberinfo.get("int32").AsBoolean() &&
  numberinfo.get("isintegral").AsBoolean()) {
          if (!(cbornumber.CanFitInInt32())) {
 Assert.fail();
 }
          if (!(CBORObject.FromObject(cbornumber.AsInt32())
                    .CanFitInInt32()))Assert.fail();
        } else {
          if (cbornumber.CanFitInInt32()) {
 Assert.fail();
 }
        }
      }
    }
    @Test
    public void TestCanFitInInt64() {
      if (!(CBORObject.FromObject(0).CanFitInSingle())) {
 Assert.fail();
 }
      if (CBORObject.True.CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.False.CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.Null.CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.CanFitInSingle()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (numberinfo.get("int64").AsBoolean() &&
  numberinfo.get("isintegral").AsBoolean()) {
          if (!(cbornumber.CanFitInInt64())) {
 Assert.fail();
 }
          if (!(CBORObject.FromObject(cbornumber.AsInt64())
                    .CanFitInInt64()))Assert.fail();
        } else {
          if (cbornumber.CanFitInInt64()) {
 Assert.fail();
 }
        }
      }
    }
    @Test
    public void TestCanFitInSingle() {
      if (!(CBORObject.FromObject(0).CanFitInSingle())) {
 Assert.fail();
 }
      if (CBORObject.True.CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.False.CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.Null.CanFitInSingle()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.CanFitInSingle()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (numberinfo.get("single").AsBoolean()) {
          if (!(cbornumber.CanFitInSingle())) {
 Assert.fail();
 }
        } else {
          if (cbornumber.CanFitInSingle()) {
 Assert.fail();
 }
        }
      }

      RandomGenerator rand = new RandomGenerator();
      for (int i = 0; i < 255; ++i) {
        // Try a random float with a given
        // exponent
        if (!(
  CBORObject.FromObject(
  RandomObjects.RandomSingle(
  rand,
  i)).CanFitInSingle()))Assert.fail();
      }
    }
    @Test
    public void TestCanTruncatedIntFitInInt32() {
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        11)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        12)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        13)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        14)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        15)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        16)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        17)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        18)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        19)).CanTruncatedIntFitInInt32()))Assert.fail();
      if (!(CBORObject.FromObject(0).CanTruncatedIntFitInInt32())) {
 Assert.fail();
 }
      if (CBORObject.True.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("")
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.False.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.Null.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(
            EDecimal.FromString(numberinfo.get("number").AsString()));
        if (numberinfo.get("int32").AsBoolean()) {
          if (!(cbornumber.CanTruncatedIntFitInInt32())) {
 Assert.fail();
 }
        } else {
          if (cbornumber.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
        }
      }

      if (CBORObject.True.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.False.CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(0).CanTruncatedIntFitInInt32())) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(2.5).CanTruncatedIntFitInInt32())) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(Integer.MIN_VALUE)
                    .CanTruncatedIntFitInInt32())) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(Integer.MAX_VALUE)
                    .CanTruncatedIntFitInInt32())) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.POSITIVE_INFINITY)
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.NEGATIVE_INFINITY)
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.NaN)
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(CBORTestCommon.DecPosInf)
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(CBORTestCommon.DecNegInf)
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(EDecimal.NaN)
                    .CanTruncatedIntFitInInt32()) {
 Assert.fail();
 }
    }

    @Test
    public void TestCanTruncatedIntFitInInt64() {
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        11)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        12)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        13)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        14)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        15)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        16)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        17)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        18)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(EFloat.Create(
        -2,
        19)).CanTruncatedIntFitInInt64()))Assert.fail();
      if (!(CBORObject.FromObject(0).CanTruncatedIntFitInInt64())) {
 Assert.fail();
 }
      if (CBORObject.True.CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("")
                    .CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      if (CBORObject.False.CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      if (CBORObject.Null.CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (numberinfo.get("int64").AsBoolean()) {
          if (!(cbornumber.CanTruncatedIntFitInInt64())) {
 Assert.fail();
 }
        } else {
          if (cbornumber.CanTruncatedIntFitInInt64()) {
 Assert.fail();
 }
        }
      }
    }

    @Test(timeout = 100000)
    public void TestCompareTo() {
      RandomGenerator r = new RandomGenerator();
      int CompareCount = 500;
      for (int i = 0; i < CompareCount; ++i) {
        CBORObject o1 = CBORTestCommon.RandomCBORObject(r);
        CBORObject o2 = CBORTestCommon.RandomCBORObject(r);
        CBORObject o3 = CBORTestCommon.RandomCBORObject(r);
        TestCommon.CompareTestRelations(o1, o2, o3);
      }
      for (int i = 0; i < 5000; ++i) {
        CBORObject o1 = CBORTestCommon.RandomNumber(r);
        CBORObject o2 = CBORTestCommon.RandomNumber(r);
        CompareDecimals(o1, o2);
      }
      TestCommon.CompareTestEqual(
  CBORObject.FromObject(0.1),
  CBORObject.FromObject(0.1));
      TestCommon.CompareTestEqual(
  CBORObject.FromObject(0.1f),
  CBORObject.FromObject(0.1f));
      for (int i = 0; i < 50; ++i) {
        CBORObject o1 = CBORObject.FromObject(Float.NEGATIVE_INFINITY);
        CBORObject o2 = CBORTestCommon.RandomNumberOrRational(r);
        if (o2.IsInfinity() || o2.IsNaN()) {
          continue;
        }
        TestCommon.CompareTestLess(o1, o2);
        o1 = CBORObject.FromObject(Double.NEGATIVE_INFINITY);
        TestCommon.CompareTestLess(o1, o2);
        o1 = CBORObject.FromObject(Float.POSITIVE_INFINITY);
        TestCommon.CompareTestLess(o2, o1);
        o1 = CBORObject.FromObject(Double.POSITIVE_INFINITY);
        TestCommon.CompareTestLess(o2, o1);
        o1 = CBORObject.FromObject(Float.NaN);
        TestCommon.CompareTestLess(o2, o1);
        o1 = CBORObject.FromObject(Double.NaN);
        TestCommon.CompareTestLess(o2, o1);
      }
      byte[] bytes1 = { 0, 1 };
      byte[] bytes2 = { 0, 2 };
      byte[] bytes3 = { 0, 2, 0 };
      byte[] bytes4 = { 1, 1 };
      byte[] bytes5 = { 1, 1, 4 };
      byte[] bytes6 = { 1, 2 };
      byte[] bytes7 = { 1, 2, 6 };
      CBORObject[] sortedObjects = {
        CBORObject.Undefined, CBORObject.Null,
        CBORObject.False, CBORObject.True,
        CBORObject.FromObject(Double.NEGATIVE_INFINITY),
        CBORObject.FromObject(EDecimal.FromString("-1E+5000")),
        CBORObject.FromObject(Long.MIN_VALUE),
        CBORObject.FromObject(Integer.MIN_VALUE),
        CBORObject.FromObject(-2), CBORObject.FromObject(-1),
        CBORObject.FromObject(0), CBORObject.FromObject(1),
        CBORObject.FromObject(2), CBORObject.FromObject(Long.MAX_VALUE),
        CBORObject.FromObject(EDecimal.FromString("1E+5000")),
        CBORObject.FromObject(Double.POSITIVE_INFINITY),
        CBORObject.FromObject(Double.NaN), CBORObject.FromSimpleValue(0),
        CBORObject.FromSimpleValue(19), CBORObject.FromSimpleValue(32),
        CBORObject.FromSimpleValue(255), CBORObject.FromObject(bytes1),
        CBORObject.FromObject(bytes2), CBORObject.FromObject(bytes3),
        CBORObject.FromObject(bytes4), CBORObject.FromObject(bytes5),
        CBORObject.FromObject(bytes6), CBORObject.FromObject(bytes7),
        CBORObject.FromObject("aa"), CBORObject.FromObject("ab"),
        CBORObject.FromObject("abc"), CBORObject.FromObject("ba"),
        CBORObject.FromObject(CBORObject.NewArray()),
        CBORObject.FromObject(CBORObject.NewMap()),
      };
      for (int i = 0; i < sortedObjects.length; ++i) {
        for (int j = i; j < sortedObjects.length; ++j) {
          if (i == j) {
            TestCommon.CompareTestEqual(sortedObjects[i], sortedObjects[j]);
          } else {
            TestCommon.CompareTestLess(sortedObjects[i], sortedObjects[j]);
          }
        }
        Assert.assertEquals(1, sortedObjects[i].compareTo(null));
      }
      CBORObject sp = CBORObject.FromObject(Float.POSITIVE_INFINITY);
      CBORObject sn = CBORObject.FromObject(Float.NEGATIVE_INFINITY);
      CBORObject snan = CBORObject.FromObject(Float.NaN);
      CBORObject dp = CBORObject.FromObject(Double.POSITIVE_INFINITY);
      CBORObject dn = CBORObject.FromObject(Double.NEGATIVE_INFINITY);
      CBORObject dnan = CBORObject.FromObject(Double.NaN);
      TestCommon.CompareTestEqual(sp, sp);
      TestCommon.CompareTestEqual(sp, dp);
      TestCommon.CompareTestEqual(dp, dp);
      TestCommon.CompareTestEqual(sn, sn);
      TestCommon.CompareTestEqual(sn, dn);
      TestCommon.CompareTestEqual(dn, dn);
      TestCommon.CompareTestEqual(snan, snan);
      TestCommon.CompareTestEqual(snan, dnan);
      TestCommon.CompareTestEqual(dnan, dnan);
      TestCommon.CompareTestLess(sn, sp);
      TestCommon.CompareTestLess(sn, dp);
      TestCommon.CompareTestLess(sn, snan);
      TestCommon.CompareTestLess(sn, dnan);
      TestCommon.CompareTestLess(sp, snan);
      TestCommon.CompareTestLess(sp, dnan);
      TestCommon.CompareTestLess(dn, dp);
      TestCommon.CompareTestLess(dp, dnan);
      Assert.assertEquals(1, CBORObject.True.compareTo(null));
      Assert.assertEquals(1, CBORObject.False.compareTo(null));
      Assert.assertEquals(1, CBORObject.Null.compareTo(null));
      Assert.assertEquals(1, CBORObject.NewArray().compareTo(null));
      Assert.assertEquals(1, CBORObject.NewMap().compareTo(null));
      Assert.assertEquals(1, CBORObject.FromObject(100).compareTo(null));
      Assert.assertEquals(1, CBORObject.FromObject(Double.NaN).compareTo(null));
      TestCommon.CompareTestLess(CBORObject.Undefined, CBORObject.Null);
      TestCommon.CompareTestLess(CBORObject.Null, CBORObject.False);
      TestCommon.CompareTestLess(CBORObject.False, CBORObject.True);
      TestCommon.CompareTestLess(CBORObject.False, CBORObject.FromObject(0));
      TestCommon.CompareTestLess(
   CBORObject.False,
   CBORObject.FromSimpleValue(0));
      TestCommon.CompareTestLess(
        CBORObject.FromSimpleValue(0),
        CBORObject.FromSimpleValue(1));
      TestCommon.CompareTestLess(
        CBORObject.FromObject(0),
        CBORObject.FromObject(1));
      TestCommon.CompareTestLess(
        CBORObject.FromObject(0.0f),
        CBORObject.FromObject(1.0f));
      TestCommon.CompareTestLess(
        CBORObject.FromObject(0.0),
        CBORObject.FromObject(1.0));
    }
    @Test
    public void TestContainsKey() {
      // not implemented yet
    }
    @Test
    public void TestCount() {
      Assert.assertEquals(0, CBORObject.True.size());
      Assert.assertEquals(0, CBORObject.False.size());
      Assert.assertEquals(0, CBORObject.NewArray().size());
      Assert.assertEquals(0, CBORObject.NewMap().size());
    }

    @Test
    public void TestDecodeFromBytes() {
      try {
        CBORObject.DecodeFromBytes(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(new byte[] { });
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(new byte[] { 0 }, null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(new byte[] { 0x1c });
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(new byte[] { 0x1e });
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(new byte[] { (byte)0xfe });
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.DecodeFromBytes(new byte[] { (byte)0xff });
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test
    public void TestDecodeFromBytesNoDuplicateKeys() {
      byte[] bytes;
      bytes = new byte[] { (byte)0xa2, 0x01, 0x00, 0x02, 0x03 };
      try {
        CBORObject.DecodeFromBytes(bytes, ValueNoDuplicateKeys);
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      bytes = new byte[] { (byte)0xa2, 0x01, 0x00, 0x01, 0x03 };
      try {
        CBORObject.DecodeFromBytes(bytes, ValueNoDuplicateKeys);
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      bytes = new byte[] { (byte)0xa2, 0x01, 0x00, 0x01, 0x03 };
      try {
        CBORObject.DecodeFromBytes(bytes, new CBOREncodeOptions(true, true));
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      bytes = new byte[] { (byte)0xa2, 0x60, 0x00, 0x60, 0x03 };
      try {
        CBORObject.DecodeFromBytes(bytes, ValueNoDuplicateKeys);
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
   bytes = new byte[] { (byte)0xa3, 0x60, 0x00, 0x62, 0x41, 0x41, 0x00, 0x60, 0x03 };
      try {
        CBORObject.DecodeFromBytes(bytes, ValueNoDuplicateKeys);
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      bytes = new byte[] { (byte)0xa2, 0x61, 0x41, 0x00, 0x61, 0x41, 0x03 };
      try {
        CBORObject.DecodeFromBytes(bytes, ValueNoDuplicateKeys);
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestDivide() {
      try {
        CBORObject.Divide(null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Divide(CBORObject.FromObject(2), null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Divide(CBORObject.Null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Divide(CBORObject.FromObject(2), CBORObject.Null);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test
    public void TestEncodeToBytes() {
      // Test minimum data length
      int[] ranges = {
        -24, 23, 1,
        -256, -25, 2,
        24, 255, 2,
        256, 266, 3,
        -266, -257, 3,
        65525, 65535, 3,
        -65536, -65525, 3,
        65536, 65546, 5,
        -65547, -65537, 5,
      };
      String[] bigRanges = {
        "4294967285", "4294967295",
        "4294967296", "4294967306",
        "18446744073709551604", "18446744073709551615",
        "-4294967296", "-4294967286",
        "-4294967306", "-4294967297",
        "-18446744073709551616", "-18446744073709551604"
      };
      int[] bigSizes = { 5, 9, 9, 5, 9, 9 };
      for (int i = 0; i < ranges.length; i += 3) {
        for (int j = ranges[i]; j <= ranges[i + 1]; ++j) {
          byte[] bytes = CBORObject.FromObject(j).EncodeToBytes();
          if (bytes.length != ranges[i + 2]) {
            Assert.assertEquals(TestCommon.IntToString(j), ranges[i + 2], bytes.length);
          }
          bytes = CBORObject.FromObject(j).EncodeToBytes(new
            CBOREncodeOptions(false, false, true));
          if (bytes.length != ranges[i + 2]) {
            Assert.assertEquals(TestCommon.IntToString(j), ranges[i + 2], bytes.length);
          }
        }
      }
      String veryLongString = TestCommon.Repeat("x", 10000);
      byte[] stringBytes = CBORObject.FromObject(veryLongString)
      .EncodeToBytes(new CBOREncodeOptions(false, false, true));
      Assert.assertEquals(10003, stringBytes.length);
      stringBytes = CBORObject.FromObject(veryLongString)
      .EncodeToBytes(new CBOREncodeOptions(false, true));
      Assert.assertEquals(10003, stringBytes.length);

      for (int i = 0; i < bigRanges.length; i += 2) {
        EInteger bj = EInteger.FromString(bigRanges[i]);
        EInteger valueBjEnd = EInteger.FromString(bigRanges[i + 1]);
        while (bj.compareTo(valueBjEnd)< 0) {
          byte[] bytes = CBORObject.FromObject(bj).EncodeToBytes();
          if (bytes.length != bigSizes[i / 2]) {
            Assert.assertEquals(bj.toString(), bigSizes[i / 2], bytes.length);
          }
          bytes = CBORObject.FromObject(bj)
          .EncodeToBytes(new CBOREncodeOptions(false, false, true));
          if (bytes.length != bigSizes[i / 2]) {
            Assert.assertEquals(bj.toString(), bigSizes[i / 2], bytes.length);
          }
          bj = bj.Add(EInteger.FromInt32(1));
        }
      }
      try {
        CBORObject.True.EncodeToBytes(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestEquals() {
      byte[] cborbytes = new byte[] { (byte)0xd8, 0x1e, (byte)0x82, 0x00, 0x19,
        0x0f, 0x50 };
      CBORObject cbor = CBORObject.DecodeFromBytes(cborbytes);
      CBORObject cbor2 = CBORObject.DecodeFromBytes(cborbytes);
      TestCommon.CompareTestEqualAndConsistent(cbor, cbor2);
      ERational erat = ERational.Create(0, 3920);
      cbor2 = CBORObject.FromObject(erat);
      TestCommon.CompareTestEqualAndConsistent(cbor, cbor2);
      cbor2 = CBORObject.FromObject(cbor2);
      TestCommon.CompareTestEqualAndConsistent(cbor, cbor2);
      TestWriteObj(erat, erat);
      erat = ERational.Create(
  EInteger.FromInt32(0),
  EInteger.FromString("84170882933504200501581262010093"));
      cbor = CBORObject.FromObject(erat);
      ERational erat2 = ERational.Create(
  EInteger.FromInt32(0),
  EInteger.FromString("84170882933504200501581262010093"));
      cbor2 = CBORObject.FromObject(erat2);
      TestCommon.CompareTestEqualAndConsistent(cbor, cbor2);
      cbor2 = CBORObject.FromObject(cbor2);
      TestCommon.CompareTestEqualAndConsistent(cbor, cbor2);
      TestWriteObj(cbor, cbor2);
      TestWriteObj(erat, erat2);
    }

    @Test
    public void TestEquivalentNegativeInfinity() {
      TestCommon.CompareTestEqualAndConsistent(
      CBORObject.FromObject(CBORTestCommon.DecNegInf),
      CBORObject.FromObject(CBORTestCommon.FloatNegInf));
      {
        CBORObject objectTemp =
          CBORObject.FromObject(CBORTestCommon.DecNegInf);
        CBORObject objectTemp2 =
          CBORObject.FromObject(CBORTestCommon.FloatNegInf);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
        CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.DecNegInf);
        CBORObject objectTemp2 = CBORObject.FromObject(Double.NEGATIVE_INFINITY);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
     CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.FloatNegInf);
        CBORObject objectTemp2 = CBORObject.FromObject(Double.NEGATIVE_INFINITY);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
        CBORObject objectTemp =
          CBORObject.FromObject(CBORTestCommon.FloatNegInf);
        CBORObject objectTemp2 = CBORObject.FromObject(Double.NEGATIVE_INFINITY);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
     CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.FloatNegInf);
        CBORObject objectTemp2 =
          CBORObject.FromObject(CBORTestCommon.FloatNegInf);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
    }

    @Test
    public void TestEquivalentPositiveInfinity() {
      TestCommon.CompareTestEqualAndConsistent(
      CBORObject.FromObject(CBORTestCommon.DecPosInf),
      CBORObject.FromObject(CBORTestCommon.FloatPosInf));
      {
        CBORObject objectTemp =
          CBORObject.FromObject(CBORTestCommon.DecPosInf);
      CBORObject objectTemp2 = CBORObject.FromObject(CBORTestCommon.RatPosInf);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
        CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.DecPosInf);
        CBORObject objectTemp2 = CBORObject.FromObject(Double.POSITIVE_INFINITY);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
     CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.FloatPosInf);
        CBORObject objectTemp2 = CBORObject.FromObject(Double.POSITIVE_INFINITY);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
        CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.RatPosInf);
        CBORObject objectTemp2 = CBORObject.FromObject(Double.POSITIVE_INFINITY);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
      {
     CBORObject objectTemp = CBORObject.FromObject(CBORTestCommon.FloatPosInf);
      CBORObject objectTemp2 = CBORObject.FromObject(CBORTestCommon.RatPosInf);
        TestCommon.CompareTestEqualAndConsistent(objectTemp, objectTemp2);
      }
    }

    @Test
    public void TestFalse() {
      CBORTestCommon.AssertJSONSer(CBORObject.False, "false");
      Assert.assertEquals(CBORObject.False, CBORObject.FromObject(false));
    }

    @Test
    public void TestFromJSONString() {
      char[] charbuf = new char[4];
      CBORObject cbor;
      // Test single-character strings
      for (int i = 0; i < 0x110000; ++i) {
        if (i >= 0xd800 && i < 0xe000) {
          continue;
        }
        String str = this.CharString(i, true, charbuf);
        if (i < 0x20 || i == 0x22 || i == 0x5c) {
          TestFailingJSON(str);
        } else {
          cbor = TestSucceedingJSON(str);
          String exp = this.CharString(i, false, charbuf);
          if (!exp.equals(cbor.AsString())) {
            Assert.assertEquals(exp, cbor.AsString());
          }
        }
      }
      for (String str : ValueJsonFails) {
        TestFailingJSON(str);
      }
      for (String str : ValueJsonSucceeds) {
        TestSucceedingJSON(str);
      }
      try {
        CBORObject.FromJSONString("\ufeff\u0020 {}");
        Assert.fail("Should have failed");
      } catch (CBORException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromJSONString("[]", null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      TestFailingJSON("{\"a\":1,\"a\":2}", ValueNoDuplicateKeys);
      String aba = "{\"a\":1,\"b\":3,\"a\":2}";
      TestFailingJSON(aba, ValueNoDuplicateKeys);
      cbor = TestSucceedingJSON(aba, new CBOREncodeOptions(false, true));
      Assert.assertEquals(CBORObject.FromObject(2), cbor.get("a"));
      aba = "{\"a\":1,\"a\":4}";
      cbor = TestSucceedingJSON(aba, new CBOREncodeOptions(false, true));
      Assert.assertEquals(CBORObject.FromObject(4), cbor.get("a"));
      cbor = TestSucceedingJSON("\"\\t\"");
      {
        String stringTemp = cbor.AsString();
        Assert.assertEquals(
        "\t",
        stringTemp);
      }
      Assert.assertEquals(CBORObject.True, TestSucceedingJSON("true"));
      Assert.assertEquals(CBORObject.False, TestSucceedingJSON("false"));
      Assert.assertEquals(CBORObject.Null, TestSucceedingJSON("null"));
      Assert.assertEquals(5, TestSucceedingJSON(" 5 ").AsInt32());
      {
        String stringTemp = TestSucceedingJSON("\"\\/\\b\"").AsString();
        Assert.assertEquals(
        "/\b",
        stringTemp);
      }
      {
        String stringTemp = TestSucceedingJSON("\"\\/\\f\"").AsString();
        Assert.assertEquals(
        "/\f",
        stringTemp);
      }
      String jsonTemp = TestCommon.Repeat(
     "[",
     2000) + TestCommon.Repeat(
     "]",
     2000);
      TestFailingJSON(jsonTemp);
    }

    @Test
    public void TestTagArray() {
      CBORObject obj = CBORObject.FromObjectAndTag("test", 999);
      {
    String stringTemp = CBORObject.FromObject(obj.GetAllTags()).ToJSONString();
        Assert.assertEquals(
          "[999]",
          stringTemp);
      }
      obj = CBORObject.FromObject("test");
      {
    String stringTemp = CBORObject.FromObject(obj.GetAllTags()).ToJSONString();
        Assert.assertEquals(
          "[]",
          stringTemp);
      }
    }
    @Test
    public void TestEI() {
      CBORObject cbor = CBORObject.FromObject(EInteger.FromString("100"));
      Assert.assertEquals(CBORType.Number, cbor.getType());
      {
        String stringTemp = cbor.toString();
        Assert.assertEquals(
        "100",
        stringTemp);
      }
      cbor = CBORObject.FromObject(EDecimal.FromString("200"));
      Assert.assertEquals(CBORType.Number, cbor.getType());
      {
        String stringTemp = cbor.toString();
        Assert.assertEquals(
        "200",
        stringTemp);
      }
      cbor = CBORObject.FromObject(EFloat.FromString("300"));
      Assert.assertEquals(CBORType.Number, cbor.getType());
      {
        String stringTemp = cbor.toString();
        Assert.assertEquals(
        "300",
        stringTemp);
      }
      cbor = CBORObject.FromObject(ERational.Create(1, 2));
      Assert.assertEquals(CBORType.Number, cbor.getType());
      {
        String stringTemp = cbor.toString();
        Assert.assertEquals(
        "1/2",
        stringTemp);
      }
    }
    @Test
    public void TestFromObject() {
      CBORObject[] cborarray = new CBORObject[2];
      cborarray[0] = CBORObject.False;
      cborarray[1] = CBORObject.True;
      CBORObject cbor = CBORObject.FromObject(cborarray);
      Assert.assertEquals(2, cbor.size());
      Assert.assertEquals(CBORObject.False, cbor.get(0));
      Assert.assertEquals(CBORObject.True, cbor.get(1));
      CBORTestCommon.AssertRoundTrip(cbor);
      Assert.assertEquals(CBORObject.Null, CBORObject.FromObject((int[])null));
      long[] longarray = { 2, 3 };
      cbor = CBORObject.FromObject(longarray);
      Assert.assertEquals(2, cbor.size());
      if (!(CBORObject.FromObject(2).compareTo(cbor.get(0)) == 0))Assert.fail();
      if (!(CBORObject.FromObject(3).compareTo(cbor.get(1)) == 0))Assert.fail();
      CBORTestCommon.AssertRoundTrip(cbor);
      Assert.assertEquals(
        CBORObject.Null,
        CBORObject.FromObject((ERational)null));
      Assert.assertEquals(
        CBORObject.Null,
        CBORObject.FromObject((EDecimal)null));
      Assert.assertEquals(
        CBORObject.FromObject(10),
        CBORObject.FromObject(ERational.Create(10, 1)));
      try {
        CBORObject.FromObject(ERational.Create(10, 2));
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }

      try {
        CBORObject.FromObject(CBORObject.FromObject(Double.NaN).signum());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      cbor = CBORObject.True;
      try {
        CBORObject.FromObject(cbor.get(0));
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        cbor.set(0, CBORObject.False);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        cbor = CBORObject.False;
        CBORObject.FromObject(cbor.getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(CBORObject.NewArray().getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(CBORObject.NewArray().signum());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(CBORObject.NewMap().signum());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    private void CheckKeyValue(CBORObject o, String key, Object value) {
      if (!o.ContainsKey(key)) {
        Assert.fail("Expected " + key + " to exist: " + o.toString());
      }
      TestCommon.AssertEqualsHashCode(o.get(key), value);
    }

    @Test
    public void TestFromObject_Dictionary() {
      Map<String, Object> dict = new HashMap<String, Object>();
      dict.put("TestKey","TestValue");
      dict.put("TestKey2","TestValue2");
      CBORObject c = CBORObject.FromObject(dict);
      this.CheckKeyValue(c, "TestKey", "TestValue");
      this.CheckKeyValue(c, "TestKey2", "TestValue2");
      c = CBORObject.FromObject((Object)dict);
      this.CheckKeyValue(c, "TestKey", "TestValue");
      this.CheckKeyValue(c, "TestKey2", "TestValue2");
    }

    public final class PODClass {
      public PODClass() {
        this.setPropA(0);
        this.setPropB(1);
        this.setPropC(false);
        this.propVarprivatepropa = 2;
        this.setFloatProp(0);
        this.setDoubleProp(0);
        this.setStringProp("");
        this.setStringArray(null);
      }

      private final int getPrivatePropA() { return propVarprivatepropa; }
private final int propVarprivatepropa;

      public final int getPropA() { return propVarpropa; }
public final void setPropA(int value) { propVarpropa = value; }
private int propVarpropa;

      public final int getPropB() { return propVarpropb; }
public final void setPropB(int value) { propVarpropb = value; }
private int propVarpropb;

      public final boolean isPropC() { return propVarispropc; }
public final void setPropC(boolean value) { propVarispropc = value; }
private boolean propVarispropc;

      public final float getFloatProp() { return propVarfloatprop; }
public final void setFloatProp(float value) { propVarfloatprop = value; }
private float propVarfloatprop;

      public final double getDoubleProp() { return propVardoubleprop; }
public final void setDoubleProp(double value) { propVardoubleprop = value; }
private double propVardoubleprop;

      public final String getStringProp() { return propVarstringprop; }
public final void setStringProp(String value) { propVarstringprop = value; }
private String propVarstringprop;

      public final String[] getStringArray() { return propVarstringarray; }
public final void setStringArray(String[] value) { propVarstringarray = value; }
private String[] propVarstringarray;
    }

    public final class NestedPODClass {
      public NestedPODClass() {
        this.propVarpropvalue = new PODClass();
      }

      public final PODClass getPropValue() { return propVarpropvalue; }
private final PODClass propVarpropvalue;
    }

    @Test(timeout = 5000)
    public void TestToObject() {
      PODClass ao = new PODClass();
      CBORObject co = CBORObject.FromObject(ao);
if (co.ContainsKey("PrivatePropA")) {
 Assert.fail();
 }
if (co.ContainsKey("privatePropA")) {
 Assert.fail();
 }
if (co.ContainsKey("staticPropA")) {
 Assert.fail();
 }
if (co.ContainsKey("StaticPropA")) {
 Assert.fail();
 }
      co.set("propA",CBORObject.FromObject(999));
      co.set("floatProp",CBORObject.FromObject(3.5));
      co.set("doubleProp",CBORObject.FromObject(4.5));
      co.set("stringProp",CBORObject.FromObject("stringProp"));
      co.set("stringArray",CBORObject.NewArray().Add("a").Add("b"));
      ao = (PODClass)co.ToObject(PODClass.class);
      Assert.assertEquals(999, ao.getPropA());
      Assert.assertEquals((float)3.5, ao.getFloatProp(), 0f);
      Assert.assertEquals(4.5, ao.getDoubleProp());
      Assert.assertEquals("stringProp", ao.getStringProp());
      String[] stringArray = ao.getStringArray();
      Assert.assertEquals(2, stringArray.length);
      Assert.assertEquals("a", stringArray[0]);
      Assert.assertEquals("b", stringArray[1]);
      if (ao.isPropC()) {
 Assert.fail();
 }
      co.set("propC",CBORObject.True);
      ao = (PODClass)co.ToObject(PODClass.class);
      if (!(ao.isPropC())) {
 Assert.fail();
 }
      co = CBORObject.True;
      Assert.assertEquals(true, co.ToObject(boolean.class));
      co = CBORObject.False;
      Assert.assertEquals(false, co.ToObject(boolean.class));
      co = CBORObject.FromObject("hello world");
      String stringTemp = (String)co.ToObject(String.class);
      Assert.assertEquals(
        "hello world",
        stringTemp);
      co = CBORObject.NewArray();
      co.Add("hello");
      co.Add("world");
      ArrayList<String> stringList = (ArrayList<String>)
        co.ToObject((new java.lang.reflect.ParameterizedType() {public java.lang.reflect.Type[] getActualTypeArguments() {return new java.lang.reflect.Type[] { String.class };}public java.lang.reflect.Type getRawType() { return ArrayList.class; } public java.lang.reflect.Type getOwnerType() { return null; }}));
      Assert.assertEquals(2, stringList.size());
      Assert.assertEquals("hello", stringList.get(0));
      Assert.assertEquals("world", stringList.get(1));
      List<String> istringList = (List<String>)
        co.ToObject((new java.lang.reflect.ParameterizedType() {public java.lang.reflect.Type[] getActualTypeArguments() {return new java.lang.reflect.Type[] { String.class };}public java.lang.reflect.Type getRawType() { return List.class; } public java.lang.reflect.Type getOwnerType() { return null; }}));
      Assert.assertEquals(2, istringList.size());
      Assert.assertEquals("hello", istringList.get(0));
      Assert.assertEquals("world", istringList.get(1));
      co = CBORObject.NewMap();
      co.Add("a", 1);
      co.Add("b", 2);
      HashMap<String, Integer> intDict =
        (HashMap<String, Integer>)co.ToObject(
          (new java.lang.reflect.ParameterizedType() {public java.lang.reflect.Type[] getActualTypeArguments() {return new java.lang.reflect.Type[] { String.class, Integer.class };}public java.lang.reflect.Type getRawType() { return HashMap.class; } public java.lang.reflect.Type getOwnerType() { return null; }}));
      Assert.assertEquals(2, intDict.size());
      if (!(intDict.containsKey("a"))) {
 Assert.fail();
 }
      if (!(intDict.containsKey("b"))) {
 Assert.fail();
 }
      if (intDict.get("a") != 1) {
        {
          Assert.fail();
        }
      }
      if (intDict.get("b") != 2) {
        {
          Assert.fail();
        }
      }
      Map<String, Integer> iintDict = (Map<String, Integer>)co.ToObject(
          (new java.lang.reflect.ParameterizedType() {public java.lang.reflect.Type[] getActualTypeArguments() {return new java.lang.reflect.Type[] { String.class, Integer.class };}public java.lang.reflect.Type getRawType() { return Map.class; } public java.lang.reflect.Type getOwnerType() { return null; }}));
      Assert.assertEquals(2, iintDict.size());
      if (!(iintDict.containsKey("a"))) {
 Assert.fail();
 }
      if (!(iintDict.containsKey("b"))) {
 Assert.fail();
 }
      if (iintDict.get("a") != 1) {
        Assert.fail();
      }
      if (iintDict.get("b") != 2) {
        Assert.fail();
      }
      co = CBORObject.FromObjectAndTag(
       "2000-01-01T00:00:00Z",
       0);
      try {
        co.ToObject(java.util.Date.class);
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test(timeout = 5000)
    public void TestFromObject_PODOptions() {
      PODClass ao = new PODClass();
      PODOptions valueCcTF = new PODOptions(true, false);
      PODOptions valueCcFF = new PODOptions(false, false);
      PODOptions valueCcFT = new PODOptions(false, true);
      PODOptions valueCcTT = new PODOptions(true, true);
      CBORObject co;
      CBORObjectTest.CheckPropertyNames(ao);
      PODClass[] arrao = new PODClass[] { ao, ao };
      co = CBORObject.FromObject(arrao, valueCcTF);
      CBORObjectTest.CheckArrayPropertyNames(
  CBORObject.FromObject(arrao, valueCcTF),
           2,
  "PropA",
  "PropB",
  "PropC");
      CBORObjectTest.CheckArrayPropertyNames(
  CBORObject.FromObject(arrao, valueCcFT),
           2,
  "propA",
  "propB",
  "isPropC");
      CBORObjectTest.CheckArrayPropertyNames(
  CBORObject.FromObject(arrao, valueCcTT),
           2,
  "propA",
  "propB",
  "propC");
      NestedPODClass ao2 = new NestedPODClass();
      CBORObjectTest.CheckPODPropertyNames(
  CBORObject.FromObject(ao2, valueCcTF),
  valueCcTF,
           "PropA",
  "PropB",
  "PropC");
      CBORObjectTest.CheckPODPropertyNames(
  CBORObject.FromObject(ao2, valueCcFT),
  valueCcFT,
           "propA",
  "propB",
  "isPropC");
      CBORObjectTest.CheckPODPropertyNames(
  CBORObject.FromObject(ao2, valueCcTT),
  valueCcTT,
           "propA",
  "propB",
  "propC");
      HashMap<String, Object> aodict = new HashMap<String, Object>();
      aodict.put("PropValue",new PODClass());

      CBORObjectTest.CheckPODInDictPropertyNames(
  CBORObject.FromObject(aodict, valueCcTF),
  "PropA",
  "PropB",
  "PropC");
      CBORObjectTest.CheckPODInDictPropertyNames(
  CBORObject.FromObject(aodict, valueCcFT),
  "propA",
  "propB",
  "isPropC");
      CBORObjectTest.CheckPODInDictPropertyNames(
  CBORObject.FromObject(aodict, valueCcTT),
  "propA",
  "propB",
  "propC");
      /*
TODO: The following cases conflict with the Java version
of the CBOR library. Resolving this conflict may result in the
Java version being backward-incompatible and so require
a major version change.
// ----
         CBORObjectTest.CheckArrayPropertyNames(
  CBORObject.FromObject(arrao, valueCcFF),
              2,
  "PropA",
  "PropB",
  "IsPropC");
         CBORObjectTest.CheckPODPropertyNames(
  CBORObject.FromObject(ao2, valueCcFF),
  valueCcFF,
              "PropA",
  "PropB",
  "IsPropC");
         CBORObjectTest.CheckPODInDictPropertyNames(
  CBORObject.FromObject(aodict, valueCcFF),
  "PropA",
  "PropB",
  "IsPropC");
       */
    }

    @Test
    public void TestFromObjectAndTag() {
      EInteger bigvalue = EInteger.FromString("99999999999999999999999999999");
      try {
        CBORObject.FromObjectAndTag(2, bigvalue);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObjectAndTag(2, -1);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObjectAndTag(CBORObject.Null, -1);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObjectAndTag(CBORObject.Null, 999999);
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      EInteger eintNull = null;
      try {
        CBORObject.FromObjectAndTag(2, eintNull);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObjectAndTag(2, EInteger.FromString("-1"));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestFromSimpleValue() {
      try {
        CBORObject.FromSimpleValue(-1);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromSimpleValue(256);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      for (int i = 0; i < 256; ++i) {
        if (i >= 24 && i < 32) {
          try {
            CBORObject.FromSimpleValue(i);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
        } else {
          CBORObject cbor = CBORObject.FromSimpleValue(i);
          Assert.assertEquals(i, cbor.getSimpleValue());
        }
      }
    }
    @Test
    public void TestGetByteString() {
      try {
        CBORObject.True.GetByteString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(0).GetByteString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject("test").GetByteString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.GetByteString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewArray().GetByteString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().GetByteString();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestGetHashCode() {
      // not implemented yet
    }
    @Test
    public void TestGetTags() {
      // not implemented yet
    }
    @Test
    public void TestHasTag() {
      try {
        CBORObject.True.HasTag(-1);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        EInteger ValueBigintNull = null;
        CBORObject.True.HasTag(ValueBigintNull);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.True.HasTag(EInteger.FromString("-1"));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      if (CBORObject.True.HasTag(0)) {
 Assert.fail();
 }
      if (CBORObject.True.HasTag(EInteger.FromInt32(0))) {
 Assert.fail();
 }
    }
    @Test
    public void TestMostInnerTag() {
      // not implemented yet
    }
    @Test
    public void TestInsert() {
      // not implemented yet
    }
    @Test
    public void TestIsFalse() {
      // not implemented yet
    }
    @Test
    public void TestIsFinite() {
      CBORObject cbor;
      if (!(CBORObject.FromObject(0).isFinite())) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").isFinite()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().isFinite()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().isFinite()) {
 Assert.fail();
 }
      cbor = CBORObject.True;
      if (cbor.isFinite()) {
 Assert.fail();
 }
      cbor = CBORObject.False;
      if (cbor.isFinite()) {
 Assert.fail();
 }
      cbor = CBORObject.Null;
      if (cbor.isFinite()) {
 Assert.fail();
 }
      cbor = CBORObject.Undefined;
      if (cbor.isFinite()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().isFinite()) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(0).isFinite())) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(2.5).isFinite())) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.POSITIVE_INFINITY).isFinite()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.NEGATIVE_INFINITY).isFinite()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.NaN).isFinite()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(
        CBORTestCommon.DecPosInf).isFinite()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(
        CBORTestCommon.DecNegInf).isFinite()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(EDecimal.NaN).isFinite()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (!numberinfo.get("integer").equals(CBORObject.Null)) {
          if (!(cbornumber.isFinite())) {
 Assert.fail();
 }
        } else {
          if (cbornumber.isFinite()) {
 Assert.fail();
 }
        }
      }
    }
    @Test
    public void TestIsInfinity() {
      if (!(CBORObject.PositiveInfinity.IsInfinity())) {
 Assert.fail();
 }
      if (!(CBORObject.NegativeInfinity.IsInfinity())) {
 Assert.fail();
 }
      if (!(CBORObject.DecodeFromBytes(new byte[] { (byte)0xfa, 0x7f,
        (byte)0x80, 0x00, 0x00 }).IsInfinity()))Assert.fail();
    }

    @Test
    public void TestIsIntegral() {
      CBORObject cbor;
      if (!(CBORObject.FromObject(0).isIntegral())) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().isIntegral()) {
 Assert.fail();
 }
      boolean isint = CBORObject.FromObject(
  EInteger.FromRadixString(
  "8000000000000000",
  16)).isIntegral();
      if (!(isint)) {
 Assert.fail();
 }
      isint = CBORObject.FromObject(
      EInteger.FromRadixString(
      "80000000000000000000",
      16)).isIntegral();
      if (!(isint)) {
 Assert.fail();
 }

      isint = CBORObject.FromObject(
    EInteger.FromRadixString(
    "8000000000000000000000000",
    16)).isIntegral();
      if (!(isint)) {
 Assert.fail();
 }
      if (!(CBORObject.FromObject(
        EDecimal.FromString("4444e+800")).isIntegral()))Assert.fail();

      if (CBORObject.FromObject(
        EDecimal.FromString("4444e-800")).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(2.5).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(999.99).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.POSITIVE_INFINITY).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.NEGATIVE_INFINITY).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(Double.NaN).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(
        CBORTestCommon.DecPosInf).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(
        CBORTestCommon.DecNegInf).isIntegral()) {
 Assert.fail();
 }
      if (CBORObject.FromObject(EDecimal.NaN).isIntegral()) {
 Assert.fail();
 }
      cbor = CBORObject.True;
      if (cbor.isIntegral()) {
 Assert.fail();
 }
      cbor = CBORObject.False;
      if (cbor.isIntegral()) {
 Assert.fail();
 }
      cbor = CBORObject.Null;
      if (cbor.isIntegral()) {
 Assert.fail();
 }
      cbor = CBORObject.Undefined;
      if (cbor.isIntegral()) {
 Assert.fail();
 }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
            numberinfo.get("number").AsString()));
        if (numberinfo.get("isintegral").AsBoolean()) {
          if (!(cbornumber.isIntegral())) {
 Assert.fail();
 }
          if (cbornumber.IsPositiveInfinity()) {
 Assert.fail();
 }
          if (cbornumber.IsNegativeInfinity()) {
 Assert.fail();
 }
          if (cbornumber.IsNaN()) {
 Assert.fail();
 }
          if (cbornumber.isNull()) {
 Assert.fail();
 }
        } else {
          if (cbornumber.isIntegral()) {
 Assert.fail();
 }
        }
      }
    }
    @Test
    public void TestIsNaN() {
      if (CBORObject.True.IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.False.IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.Null.IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.PositiveInfinity.IsNaN()) {
 Assert.fail();
 }
      if (CBORObject.NegativeInfinity.IsNaN()) {
 Assert.fail();
 }
      if (!(CBORObject.NaN.IsNaN())) {
 Assert.fail();
 }
    }
    @Test
    public void TestIsNegativeInfinity() {
      if (CBORObject.True.IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.False.IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.Null.IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.IsNegativeInfinity()) {
 Assert.fail();
 }
      if (CBORObject.PositiveInfinity.IsNegativeInfinity()) {
 Assert.fail();
 }
      if (!(CBORObject.NegativeInfinity.IsNegativeInfinity())) {
 Assert.fail();
 }
      if (CBORObject.NaN.IsNegativeInfinity()) {
 Assert.fail();
 }
    }
    @Test
    public void TestIsNull() {
      if (CBORObject.True.isNull()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").isNull()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().isNull()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().isNull()) {
 Assert.fail();
 }
      if (CBORObject.False.isNull()) {
 Assert.fail();
 }
      if (!(CBORObject.Null.isNull())) {
 Assert.fail();
 }
      if (CBORObject.Undefined.isNull()) {
 Assert.fail();
 }
      if (CBORObject.PositiveInfinity.isNull()) {
 Assert.fail();
 }
      if (CBORObject.NegativeInfinity.isNull()) {
 Assert.fail();
 }
      if (CBORObject.NaN.isNull()) {
 Assert.fail();
 }
    }
    @Test
    public void TestIsPositiveInfinity() {
      if (CBORObject.True.IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.False.IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.Null.IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.Undefined.IsPositiveInfinity()) {
 Assert.fail();
 }
      if (!(CBORObject.PositiveInfinity.IsPositiveInfinity())) {
 Assert.fail();
 }
      if (CBORObject.NegativeInfinity.IsPositiveInfinity()) {
 Assert.fail();
 }
      if (CBORObject.NaN.IsPositiveInfinity()) {
 Assert.fail();
 }
    }
    @Test
    public void TestIsTagged() {
      // not implemented yet
    }
    @Test
    public void TestIsTrue() {
      // not implemented yet
    }
    @Test
    public void TestIsUndefined() {
      if (CBORObject.True.isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.FromObject("").isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.NewArray().isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.NewMap().isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.False.isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.Null.isUndefined()) {
 Assert.fail();
 }
      if (!(CBORObject.Undefined.isUndefined())) {
 Assert.fail();
 }
      if (CBORObject.PositiveInfinity.isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.NegativeInfinity.isUndefined()) {
 Assert.fail();
 }
      if (CBORObject.NaN.isUndefined()) {
 Assert.fail();
 }
    }
    @Test
    public void TestIsZero() {
      // not implemented yet
    }
    @Test
    public void TestItem() {
      CBORObject cbor = CBORObject.True;
      try {
        CBORObject cbor2 = cbor.get(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      cbor = CBORObject.False;
      try {
        CBORObject cbor2 = cbor.get(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      cbor = CBORObject.FromObject(0);
      try {
        CBORObject cbor2 = cbor.get(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      cbor = CBORObject.FromObject(2);
      try {
        CBORObject cbor2 = cbor.get(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      cbor = CBORObject.NewArray();
      try {
        CBORObject cbor2 = cbor.get(0);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    private void Sink(Object obj) {
      System.out.println("Sink for " + obj);
      Assert.fail();
    }

    @Test
    public void TestKeys() {
      CBORObject co;
      try {
        co = CBORObject.True;
        this.Sink(co.getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        this.Sink(CBORObject.FromObject(0).getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        this.Sink(CBORObject.FromObject("String").getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        this.Sink(CBORObject.NewArray().getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        this.Sink(CBORObject.FromObject(
                 new byte[] { 0 }).getKeys());
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      if (CBORObject.NewMap().getKeys() == null) {
        Assert.fail();
      }
    }
    @Test
    public void TestMultiply() {
      try {
        CBORObject.Multiply(null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Multiply(CBORObject.FromObject(2), null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Multiply(CBORObject.Null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Multiply(CBORObject.FromObject(2), CBORObject.Null);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }

      RandomGenerator r = new RandomGenerator();
      for (int i = 0; i < 3000; ++i) {
        CBORObject o1 = CBORTestCommon.RandomNumber(r);
        CBORObject o2 = CBORTestCommon.RandomNumber(r);
        EDecimal cmpDecFrac = AsED(o1).Multiply(AsED(o2));
        EDecimal cmpCobj = AsED(CBORObject.Multiply(
          o1,
          o2));
        TestCommon.CompareTestEqual(cmpDecFrac, cmpCobj);
        CBORTestCommon.AssertRoundTrip(o1);
        CBORTestCommon.AssertRoundTrip(o2);
      }
    }
    @Test
    public void TestNegate() {
      Assert.assertEquals(
        CBORObject.FromObject(2),
        CBORObject.FromObject(-2).Negate());
      Assert.assertEquals(
        CBORObject.FromObject(-2),
        CBORObject.FromObject(2).Negate());
      try {
        CBORObject.True.Negate();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.Negate();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewArray().Negate();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.NewMap().Negate();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test
    public void TestNegativeTenDigitLong() {
      CBORObject obj = CBORObject.FromJSONString("-1000000000");
      {
        String stringTemp = obj.ToJSONString();
        Assert.assertEquals(
        "-1000000000",
        stringTemp);
      }
      {
        String stringTemp = obj.toString();
        Assert.assertEquals(
        "-1000000000",
        stringTemp);
      }
    }

    @Test
    public void TestNegativeZero() {
      CBORObject negzero = CBORObject.FromObject(
        EDecimal.FromString("-0"));
      CBORTestCommon.AssertRoundTrip(negzero);
    }
    @Test
    public void TestNewArray() {
      // not implemented yet
    }
    @Test
    public void TestNewMap() {
      // not implemented yet
    }
    @Test
    public void TestOperatorAddition() {
      // not implemented yet
    }
    @Test
    public void TestOperatorDivision() {
      // not implemented yet
    }
    @Test
    public void TestOperatorModulus() {
      // not implemented yet
    }
    @Test
    public void TestOperatorMultiply() {
      // not implemented yet
    }
    @Test
    public void TestOperatorSubtraction() {
      // not implemented yet
    }
    @Test
    public void TestMostOuterTag() {
      CBORObject cbor = CBORObject.FromObjectAndTag(CBORObject.True, 999);
      cbor = CBORObject.FromObjectAndTag(CBORObject.True, 1000);
      Assert.assertEquals(EInteger.FromString("1000"), cbor.getMostOuterTag());
      cbor = CBORObject.True;
      Assert.assertEquals(EInteger.FromString("-1"), cbor.getMostOuterTag());
    }
    @Test
    public void TestRead() {
      try {
        CBORObject.Read(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        {
java.io.ByteArrayInputStream ms2 = null;
try {
ms2 = new java.io.ByteArrayInputStream(new byte[] { 0 });

          try {
            CBORObject.Read(ms2, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms2 != null) {
 ms2.close();
 } } catch (java.io.IOException ex) {}
}
}
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestReadJSON() {
      try {
        {
java.io.ByteArrayInputStream ms2 = null;
try {
ms2 = new java.io.ByteArrayInputStream(new byte[] { 0x30 });

          try {
            CBORObject.ReadJSON(ms2, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms2 != null) {
 ms2.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(new byte[] { (byte)0xef, (byte)0xbb, (byte)0xbf, 0x7b,
        0x7d });

          try {
            CBORObject.ReadJSON(ms);
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
}
        // whitespace followed by BOM
        {
java.io.ByteArrayInputStream ms2 = null;
try {
ms2 = new java.io.ByteArrayInputStream(new byte[] { 0x20, (byte)0xef, (byte)0xbb, (byte)0xbf,
        0x7b, 0x7d });

          try {
            CBORObject.ReadJSON(ms2);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms2 != null) {
 ms2.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream ms2a = null;
try {
ms2a = new java.io.ByteArrayInputStream(new byte[] { 0x7b, 0x05, 0x7d });

          try {
            CBORObject.ReadJSON(ms2a);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms2a != null) {
 ms2a.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream ms2b = null;
try {
ms2b = new java.io.ByteArrayInputStream(new byte[] { 0x05, 0x7b, 0x7d });

          try {
            CBORObject.ReadJSON(ms2b);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms2b != null) {
 ms2b.close();
 } } catch (java.io.IOException ex) {}
}
}
        // two BOMs
        {
java.io.ByteArrayInputStream ms3 = null;
try {
ms3 = new java.io.ByteArrayInputStream(new byte[] { (byte)0xef, (byte)0xbb, (byte)0xbf, (byte)0xef,
        (byte)0xbb, (byte)0xbf, 0x7b, 0x7d });

          try {
            CBORObject.ReadJSON(ms3);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms3 != null) {
 ms3.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
          0,
        0,
                    0x74, 0, 0, 0, 0x72, 0, 0, 0, 0x75, 0, 0, 0,
                    0x65 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x74, 0, 0,
        0, 0x72, 0,
                    0, 0, 0x75, 0, 0, 0, 0x65 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0, 0,
        0x74, 0, 0, 0,
                    0x72, 0, 0, 0, 0x75, 0, 0, 0, 0x65, 0, 0, 0 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x74, 0, 0, 0, 0x72,
          0,
          0,
        0,
                    0x75, 0, 0, 0, 0x65, 0, 0, 0 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, 0, 0x74,
        0, 0x72, 0,
                    0x75, 0, 0x65 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0x74, 0, 0x72, 0,
        0x75, 0, 0x65 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x74, 0,
          0x72,
        0,
                    0x75,
                    0, 0x65, 0 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x74, 0, 0x72, 0,
        0x75, 0, 0x65, 0 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xef, (byte)0xbb, (byte)0xbf,
        0x74, 0x72, 0x75,
       0x65 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x74, 0x72, 0x75, 0x65 });

          Assert.assertEquals(CBORObject.True, CBORObject.ReadJSON(msjson));
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        0, 0, 0x22,
                    0, 1, 0, 0, 0, 0, 0, 0x22 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x22, 0, 1,
        0, 0, 0, 0,
                    0, 0x22 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0, 0,
        0x22, 0, 0, 0,
                    0, 0, 1, 0, 0x22, 0, 0, 0 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x22, 0, 0, 0, 0, 0,
        1, 0, 0x22,
                    0,
                    0, 0 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
   {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, 0, 0x22, (byte)0xd8,
        0,
                    (byte)0xdc, 0, 0, 0x22 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0x22, (byte)0xd8, 0,
        (byte)0xdc, 0, 0, 0x22 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x22, 0,
        0, (byte)0xd8, 0,
                    (byte)0xdc, 0x22, 0 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x22, 0, 0, (byte)0xd8, 0,
        (byte)0xdc, 0x22, 0 });

          {
            String stringTemp = CBORObject.ReadJSON(msjson).AsString();
            Assert.assertEquals(
            "\ud800\udc00",
            stringTemp);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        0, 0, 0x22,
                    0, 0, (byte)0xd8, 0, 0, 0, 0, 0x22 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x22, 0, 0,
        (byte)0xd8, 0, 0,
                    0,
                    0, 0x22 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0, 0,
        0x22, 0, 0, 0,
                    0, (byte)0xd8, 0, 0, 0x22, 0, 0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x22, 0, 0, 0, 0,
          (byte)0xd8,
          0,
        0,
                    0x22, 0, 0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, 0, 0x22,
        0, (byte)0xdc, 0,
                    (byte)0xdc, 0, 0, 0x22 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0x22, 0, (byte)0xdc, 0,
        (byte)0xdc, 0, 0,
                    0x22 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x22, 0,
        0, (byte)0xdc, 0,
                    (byte)0xdc, 0x22, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0x22, 0, 0, (byte)0xdc, 0,
        (byte)0xdc, 0x22, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfc });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        // Illegal UTF-16
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, 0x20,
        0x20, 0x20 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x20,
        0x20, 0x20 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xd8, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xdc, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xd8,
        0x00, 0x20, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xdc,
        0x00, 0x20, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xd8,
        0x00, (byte)0xd8, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xdc,
        0x00, (byte)0xd8, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xdc,
        0x00, (byte)0xd8, 0x00, (byte)0xdc, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xfe, (byte)0xff, (byte)0xdc,
        0x00, (byte)0xdc, 0x00 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}

 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00, (byte)0xd8 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00, (byte)0xdc });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00,
        (byte)0xd8, 0x00, 0x20 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00,
        (byte)0xdc, 0x00, 0x20 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00,
        (byte)0xd8, 0x00, (byte)0xd8 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00,
        (byte)0xdc, 0x00, (byte)0xd8 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00,
        (byte)0xdc, 0x00, (byte)0xd8, 0x00, (byte)0xdc });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { (byte)0xff, (byte)0xfe, 0x00,
        (byte)0xdc, 0x00, (byte)0xdc });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}

        // Illegal UTF-32
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
    {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0, 0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0, 0,
        (byte)0xd8, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0, 0,
        (byte)0xdc, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0,
        0x11, 0x00, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0,
        (byte)0xff, 0x00, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, 0, 0x20, 0x1,
        0, 0x00, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
    {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
 {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        0, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        0, (byte)0xd8, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        0, (byte)0xdc, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        0x11, 0x00, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff, 0,
        (byte)0xff, 0x00, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
        {
java.io.ByteArrayInputStream msjson = null;
try {
msjson = new java.io.ByteArrayInputStream(new byte[] { 0, 0, (byte)0xfe, (byte)0xff,
        0x1, 0, 0x00, 0 });

          try {
            CBORObject.ReadJSON(msjson);
            Assert.fail("Should have failed");
          } catch (CBORException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (msjson != null) {
 msjson.close();
 } } catch (java.io.IOException ex) {}
}
}
      } catch (IOException ex) {
        Assert.fail(ex.getMessage());
      }
    }

    @Test
    public void TestRemainder() {
      try {
        CBORObject.Remainder(null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Remainder(CBORObject.FromObject(2), null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Remainder(CBORObject.Null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Remainder(CBORObject.FromObject(2), CBORObject.Null);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test
    public void TestClear() {
      CBORObject cbor;
      cbor = CBORObject.NewArray().Add("a").Add("b").Add("c");
      Assert.assertEquals(3, cbor.size());
      cbor.Clear();
      Assert.assertEquals(0, cbor.size());
      cbor = CBORObject.NewMap()
        .Add("a", 0).Add("b", 1).Add("c", 2);
      Assert.assertEquals(3, cbor.size());
      cbor.Clear();
      Assert.assertEquals(0, cbor.size());
      try {
        CBORObject.FromObject(1).Clear();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.Clear();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Null.Clear();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test
    public void TestRemove() {
      CBORObject cbor;
      cbor = CBORObject.NewArray().Add("a").Add("b").Add("c");
      Assert.assertEquals(3, cbor.size());
      if (!(cbor.Remove(CBORObject.FromObject("b"))))Assert.fail();
      if (cbor.Remove(CBORObject.FromObject("x"))) {
 Assert.fail();
 }
      try {
        cbor.Remove((CBORObject)null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(2, cbor.size());
      Assert.assertEquals(CBORObject.FromObject("a"), cbor.get(0));
      Assert.assertEquals(CBORObject.FromObject("c"), cbor.get(1));
      cbor = CBORObject.NewArray().Add("a").Add("b").Add("c");
      Assert.assertEquals(3, cbor.size());

      if (!(cbor.Remove("b"))) {
 Assert.fail();
 }
      if (cbor.Remove("x")) {
 Assert.fail();
 }
      Assert.assertEquals(2, cbor.size());
      Assert.assertEquals(CBORObject.FromObject("a"), cbor.get(0));
      Assert.assertEquals(CBORObject.FromObject("c"), cbor.get(1));
      cbor = CBORObject.NewMap().Add("a", 0).Add("b", 1).Add("c", 2);
      Assert.assertEquals(3, cbor.size());

      if (!(cbor.Remove(CBORObject.FromObject("b"))))Assert.fail();
      if (cbor.Remove(CBORObject.FromObject("x"))) {
 Assert.fail();
 }
      try {
        cbor.Remove((CBORObject)null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(2, cbor.size());
      if (!(cbor.ContainsKey("a"))) {
 Assert.fail();
 }
      if (!(cbor.ContainsKey("c"))) {
 Assert.fail();
 }
      cbor = CBORObject.NewMap().Add("a", 0).Add("b", 1).Add("c", 2);
      Assert.assertEquals(3, cbor.size());

      if (!(cbor.Remove("b"))) {
 Assert.fail();
 }
      if (cbor.Remove("x")) {
 Assert.fail();
 }
      Assert.assertEquals(2, cbor.size());
      if (!(cbor.ContainsKey("a"))) {
 Assert.fail();
 }
      if (!(cbor.ContainsKey("c"))) {
 Assert.fail();
 }
      try {
        CBORObject.FromObject(1).Remove("x");
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.Remove("x");
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Null.Remove("x");
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(1).Remove(CBORObject.FromObject("b"));
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.Remove(CBORObject.FromObject("b"));
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Null.Remove(CBORObject.FromObject("b"));
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestRemoveAt() {
      CBORObject cbor;
      cbor = CBORObject.NewArray().Add("a").Add("b").Add("c");
      if (!(cbor.RemoveAt(1))) {
 Assert.fail();
 }
      if (cbor.RemoveAt(2)) {
 Assert.fail();
 }
      if (cbor.RemoveAt(-1)) {
 Assert.fail();
 }
      Assert.assertEquals(2, cbor.size());
      Assert.assertEquals(CBORObject.FromObject("a"), cbor.get(0));
      Assert.assertEquals(CBORObject.FromObject("c"), cbor.get(1));
      try {
        CBORObject.NewMap().RemoveAt(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.FromObject(1).RemoveAt(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.False.RemoveAt(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Null.RemoveAt(0);
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestSet() {
      CBORObject cbor = CBORObject.NewMap().Add("x", 0).Add("y", 1);
      Assert.assertEquals(0, cbor.get("x").AsInt32());
      Assert.assertEquals(1, cbor.get("y").AsInt32());
      cbor.Set("x", 5).Set("z", 6);
      Assert.assertEquals(5, cbor.get("x").AsInt32());
      Assert.assertEquals(6, cbor.get("z").AsInt32());
    }
    @Test
    public void TestSign() {
      try {
        int sign = CBORObject.True.signum();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        int sign = CBORObject.False.signum();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        int sign = CBORObject.NewArray().signum();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        int sign = CBORObject.NewMap().signum();
        Assert.fail("Should have failed");
      } catch (IllegalStateException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      CBORObject numbers = GetNumberData();
      for (int i = 0; i < numbers.size(); ++i) {
        CBORObject numberinfo = numbers.get(i);
        CBORObject cbornumber =
          CBORObject.FromObject(EDecimal.FromString(
  numberinfo.get("number").AsString()));
        if (cbornumber.IsNaN()) {
          try {
            Assert.fail("" + cbornumber.signum());
            Assert.fail("Should have failed");
          } catch (IllegalStateException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
        } else if (numberinfo.get("number").AsString().indexOf('-') == 0) {
          Assert.assertEquals(-1, cbornumber.signum());
        } else if (numberinfo.get("number").AsString().equals("0")) {
          Assert.assertEquals(0, cbornumber.signum());
        } else {
          Assert.assertEquals(1, cbornumber.signum());
        }
      }
    }
    @Test
    public void TestSimpleValue() {
      // not implemented yet
    }
    @Test
    public void TestSubtract() {
      try {
        CBORObject.Subtract(null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Subtract(CBORObject.FromObject(2), null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Subtract(CBORObject.Null, CBORObject.FromObject(2));
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Subtract(CBORObject.FromObject(2), CBORObject.Null);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    @Test
    public void TestToJSONString() {
      {
        String stringTemp = CBORObject.FromObject(
        "\u2027\u2028\u2029\u202a\u0008\u000c").ToJSONString();
        Assert.assertEquals(
        "\"\u2027\\u2028\\u2029\u202a\\b\\f\"",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.FromObject(
        "\u0085\ufeff\ufffe\uffff").ToJSONString();
        Assert.assertEquals(
        "\"\\u0085\\uFEFF\\uFFFE\\uFFFF\"",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.True.ToJSONString();
        Assert.assertEquals(
        "true",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.False.ToJSONString();
        Assert.assertEquals(
        "false",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.Null.ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject(Float.POSITIVE_INFINITY).ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject(Float.NEGATIVE_INFINITY).ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.FromObject(Float.NaN).ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject(Double.POSITIVE_INFINITY).ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      {
        String stringTemp =
          CBORObject.FromObject(Double.NEGATIVE_INFINITY).ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.FromObject(Double.NaN).ToJSONString();
        Assert.assertEquals(
        "null",
        stringTemp);
      }
      // Base64 tests
      CBORObject o;
      o = CBORObject.FromObjectAndTag(
        new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xf0, (byte)0xe8 }, 22);
      {
        String stringTemp = o.ToJSONString();
        Assert.assertEquals(
        "\"mtbw6A\"",
        stringTemp);
      }
      o = CBORObject.FromObject(new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xf0, (byte)0xe8 });
      {
        String stringTemp = o.ToJSONString();
        Assert.assertEquals(
        "\"mtbw6A\"",
        stringTemp);
      }
      o = CBORObject.FromObjectAndTag(
        new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xf0, (byte)0xe8 },
        23);
      {
        String stringTemp = o.ToJSONString();
        Assert.assertEquals(
        "\"9AD6F0E8\"",
        stringTemp);
      }
      o = CBORObject.FromObject(new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xff, (byte)0xe8 });
      // Encode with Base64URL by default
      {
        String stringTemp = o.ToJSONString();
        Assert.assertEquals(
        "\"mtb_6A\"",
        stringTemp);
      }
      o = CBORObject.FromObjectAndTag(
        new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xff, (byte)0xe8 },
        22);
      // Encode with Base64
      {
        String stringTemp = o.ToJSONString();
        Assert.assertEquals(
        "\"mtb/6A\"",
        stringTemp);
      }

      CBORObject cbor = CBORObject.NewArray();
      byte[] b64bytes = new byte[] { 0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xff, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xff, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc,
  0x01, (byte)0xfe, (byte)0xdd, (byte)0xfd, (byte)0xdc };
      cbor.Add(b64bytes);
      TestSucceedingJSON(cbor.ToJSONString());
      cbor = CBORObject.NewArray();
      cbor.Add(CBORObject.FromObjectAndTag(b64bytes, 22));
      TestSucceedingJSON(cbor.ToJSONString());
    }

    @Test
    public void TestToJSONString_ByteArray_Padding() {
      CBORObject o;
      JSONOptions options = new JSONOptions(true);  // base64 padding enabled
      o = CBORObject.FromObjectAndTag(
        new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xf0, (byte)0xe8 }, 22);
      {
        String stringTemp = o.ToJSONString(options);
        Assert.assertEquals(
        "\"mtbw6A==\"",
        stringTemp);
      }
      o = CBORObject.FromObject(new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xf0, (byte)0xe8 });
      {
        String stringTemp = o.ToJSONString(options);
        Assert.assertEquals(
        "\"mtbw6A==\"",
        stringTemp);
      }
      o = CBORObject.FromObjectAndTag(
        new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xf0, (byte)0xe8 },
        23);
      {
        String stringTemp = o.ToJSONString(options);
        Assert.assertEquals(
        "\"9AD6F0E8\"",
        stringTemp);
      }
      o = CBORObject.FromObject(new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xff, (byte)0xe8 });
      // Encode with Base64URL by default
      {
        String stringTemp = o.ToJSONString(options);
        Assert.assertEquals(
        "\"mtb_6A==\"",
        stringTemp);
      }
      o = CBORObject.FromObjectAndTag(
        new byte[] { (byte)0x9a, (byte)0xd6, (byte)0xff, (byte)0xe8 },
        22);
      // Encode with Base64
      {
        String stringTemp = o.ToJSONString(options);
        Assert.assertEquals(
        "\"mtb/6A==\"",
        stringTemp);
      }
    }

    @Test
    public void TestToString() {
      {
        String stringTemp = CBORObject.Undefined.toString();
        Assert.assertEquals(
        "undefined",
        stringTemp);
      }
      {
        String stringTemp = CBORObject.FromSimpleValue(50).toString();
        Assert.assertEquals(
        "simple(50)",
        stringTemp);
      }
    }

    @Test
    public void TestTrue() {
      CBORTestCommon.AssertJSONSer(CBORObject.True, "true");
      Assert.assertEquals(CBORObject.True, CBORObject.FromObject(true));
    }

    @Test
    public void TestType() {
      // not implemented yet
    }
    @Test
    public void TestUntag() {
      CBORObject o = CBORObject.FromObjectAndTag("test", 999);
      Assert.assertEquals(EInteger.FromString("999"), o.getMostInnerTag());
      o = o.Untag();
      Assert.assertEquals(EInteger.FromString("-1"), o.getMostInnerTag());
    }
    @Test
    public void TestUntagOne() {
      // not implemented yet
    }
    @Test
    public void TestValues() {
      // not implemented yet
    }

    @Test
    public void TestWrite() {
      for (int i = 0; i < 2000; ++i) {
        this.TestWrite2();
      }
      for (int i = 0; i < 40; ++i) {
        this.TestWrite3();
      }
    }

    @Test
    public void TestWriteExtra() {
      try {
        String str = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(str);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)str);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(str, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(str, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)str, null);
        }

        {
          CBORObject cborTemp1 = CBORObject.FromObject("test");
          CBORObject cborTemp2 = CBORObject.FromObject((Object)"test");
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write("test", null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write("test", ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject("test"));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)"test", "test");
        }

        str = TestCommon.Repeat("test", 4000);
        {
          CBORObject cborTemp1 = CBORObject.FromObject(str);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)str);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(str, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(str, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject(str));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)str, str);
        }

        long[] values = {
          0, 1, 23, 24, -1, -23, -24, -25,
   0x7f, -128, 255, 256, 0x7fff, -32768, 0x7fff,
  -32768, -65536, -32769, -65537,
  0x7fffff, 0x7fff7f, 0x7fff7fff, 0x7fff7fff7fL,
  0x7fff7fff7fffL, 0x7fff7fff7fff7fL, 0x7fff7fff7fff7fffL,
      Long.MAX_VALUE, Long.MIN_VALUE, Integer.MIN_VALUE,
      Integer.MAX_VALUE };
        for (int i = 0; i < values.length; ++i) {
          {
            CBORObject cborTemp1 = CBORObject.FromObject(values[i]);
            CBORObject cborTemp2 = CBORObject.FromObject((Object)values[i]);
            TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
            try {
              CBORObject.Write(values[i], null);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            AssertWriteThrow(cborTemp1);
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              CBORObject.Write(values[i], ms);
              CBORObject.Write(cborTemp1, ms);
              cborTemp1.WriteTo(ms);
              AssertReadThree(ms.toByteArray(), CBORObject.FromObject(values[i]));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
            TestWriteObj((Object)values[i], values[i]);
          }

          EInteger bigintVal = EInteger.FromInt64(values[i]);
          {
            CBORObject cborTemp1 = CBORObject.FromObject(bigintVal);
            CBORObject cborTemp2 = CBORObject.FromObject((Object)bigintVal);
            TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
            try {
              CBORObject.Write(bigintVal, null);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            AssertWriteThrow(cborTemp1);
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              CBORObject.Write(bigintVal, ms);
              CBORObject.Write(cborTemp1, ms);
              cborTemp1.WriteTo(ms);
              AssertReadThree(ms.toByteArray(), CBORObject.FromObject(bigintVal));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
            TestWriteObj((Object)bigintVal, bigintVal);
          }

          if (values[i] >= (long)Integer.MIN_VALUE && values[i] <=
                  (long)Integer.MAX_VALUE) {
            int intval = (int)values[i];
            {
              CBORObject cborTemp1 = CBORObject.FromObject(intval);
              CBORObject cborTemp2 = CBORObject.FromObject((Object)intval);
              TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
              try {
                CBORObject.Write(intval, null);
                Assert.fail("Should have failed");
              } catch (NullPointerException ex) {
                // NOTE: Intentionally empty
              } catch (Exception ex) {
                Assert.fail(ex.toString());
                throw new IllegalStateException("", ex);
              }
              AssertWriteThrow(cborTemp1);
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

                CBORObject.Write(intval, ms);
                CBORObject.Write(cborTemp1, ms);
                cborTemp1.WriteTo(ms);
                AssertReadThree(ms.toByteArray(), CBORObject.FromObject(intval));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
              TestWriteObj((Object)intval, intval);
            }
          }
          if (values[i] >= -32768L && values[i] <= 32767) {
            short shortval = (short)values[i];
            {
              CBORObject cborTemp1 = CBORObject.FromObject(shortval);
              CBORObject cborTemp2 = CBORObject.FromObject((Object)shortval);
              TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
              try {
                CBORObject.Write(shortval, null);
                Assert.fail("Should have failed");
              } catch (NullPointerException ex) {
                // NOTE: Intentionally empty
              } catch (Exception ex) {
                Assert.fail(ex.toString());
                throw new IllegalStateException("", ex);
              }
              AssertWriteThrow(cborTemp1);
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

                CBORObject.Write(shortval, ms);
                CBORObject.Write(cborTemp1, ms);
                cborTemp1.WriteTo(ms);
                AssertReadThree(ms.toByteArray(), CBORObject.FromObject(shortval));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
              TestWriteObj((Object)shortval, shortval);
            }
          }
          if (values[i] >= 0L && values[i] <= 255) {
            byte byteval = (byte)values[i];
            {
              CBORObject cborTemp1 = CBORObject.FromObject(byteval);
              CBORObject cborTemp2 = CBORObject.FromObject((Object)byteval);
              TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
              try {
                CBORObject.Write(byteval, null);
                Assert.fail("Should have failed");
              } catch (NullPointerException ex) {
                // NOTE: Intentionally empty
              } catch (Exception ex) {
                Assert.fail(ex.toString());
                throw new IllegalStateException("", ex);
              }
              AssertWriteThrow(cborTemp1);
              java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

                CBORObject.Write(byteval, ms);
                CBORObject.Write(cborTemp1, ms);
                cborTemp1.WriteTo(ms);
                AssertReadThree(ms.toByteArray(), CBORObject.FromObject(byteval));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
              TestWriteObj((Object)byteval, byteval);
            }
          }
        }
        {
          CBORObject cborTemp1 = CBORObject.FromObject(0.0f);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)0.0f);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(0.0f, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(0.0f, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject(0.0f));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)0.0f, 0.0f);
        }

        {
          CBORObject cborTemp1 = CBORObject.FromObject(2.6);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)2.6);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(2.6, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(2.6, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject(2.6));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)2.6, 2.6);
        }

        CBORObject cbor = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(cbor);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)cbor);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(cbor, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(cbor, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)cbor, null);
        }

        Object aobj = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(aobj);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)aobj);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(aobj, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(aobj, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)aobj, null);
        }
      } catch (IOException ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException(ex.toString(), ex);
      }
    }
    public void TestWrite3() {
      EFloat ef = null;
      EDecimal ed = null;
      RandomGenerator fr = new RandomGenerator();
      try {
for (int i = 0; i < 256; ++i) {
 byte b=(byte)(i & 0xff);
 java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

  CBORObject.Write((byte)b, ms);
  CBORObject cobj = CBORObject.DecodeFromBytes(b.ToArray());
  Assert.assertEquals(i, cobj.AsInt32());
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
}

        for (int i = 0; i < 50; ++i) {
          ef = RandomObjects.RandomEFloat(fr);
          if (!ef.IsNaN()) {
            CBORObject cborTemp1 = CBORObject.FromObject(ef);
            CBORObject cborTemp2 = CBORObject.FromObject((Object)ef);
            TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
            try {
              CBORObject.Write(ef, null);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            AssertWriteThrow(cborTemp1);
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              CBORObject.Write(ef, ms);
              CBORObject.Write(cborTemp1, ms);
              cborTemp1.WriteTo(ms);
              AssertReadThree(ms.toByteArray(), CBORObject.FromObject(ef));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
            TestWriteObj((Object)ef, ef);
          }

          ef = EFloat.Create(
           RandomObjects.RandomEInteger(fr),
           RandomObjects.RandomEInteger(fr));
          {
            CBORObject cborTemp1 = CBORObject.FromObject(ef);
            CBORObject cborTemp2 = CBORObject.FromObject((Object)ef);
            TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
            try {
              CBORObject.Write(ef, null);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            AssertWriteThrow(cborTemp1);
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              CBORObject.Write(ef, ms);
              CBORObject.Write(cborTemp1, ms);
              cborTemp1.WriteTo(ms);
              if (cborTemp1.isNegative() && cborTemp1.isZero()) {
                AssertReadThree(ms.toByteArray());
              } else {
                AssertReadThree(ms.toByteArray(), CBORObject.FromObject(ef));
              }
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
            TestWriteObj((Object)ef, ef);
          }
        }
        for (int i = 0; i < 50; ++i) {
          ed = RandomObjects.RandomEDecimal(fr);
          if (!ed.IsNaN()) {
            CBORObject cborTemp1 = CBORObject.FromObject(ed);
            CBORObject cborTemp2 = CBORObject.FromObject((Object)ed);
            TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
            try {
              CBORObject.Write(ed, null);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            AssertWriteThrow(cborTemp1);
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              CBORObject.Write(ed, ms);
              CBORObject.Write(cborTemp1, ms);
              cborTemp1.WriteTo(ms);
              if (cborTemp1.isNegative() && cborTemp1.isZero()) {
                AssertReadThree(ms.toByteArray());
              } else {
                AssertReadThree(ms.toByteArray(), CBORObject.FromObject(ed));
              }
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
            if (!(cborTemp1.isNegative() && cborTemp1.isZero())) {
              TestWriteObj((Object)ed, ed);
            }
          }

          ed = EDecimal.Create(
           RandomObjects.RandomEInteger(fr),
           RandomObjects.RandomEInteger(fr));
          {
            CBORObject cborTemp1 = CBORObject.FromObject(ed);
            CBORObject cborTemp2 = CBORObject.FromObject((Object)ed);
            TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
            try {
              CBORObject.Write(ed, null);
              Assert.fail("Should have failed");
            } catch (NullPointerException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            AssertWriteThrow(cborTemp1);
            java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

              CBORObject.Write(ed, ms);
              CBORObject.Write(cborTemp1, ms);
              cborTemp1.WriteTo(ms);
              AssertReadThree(ms.toByteArray(), CBORObject.FromObject(ed));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
            TestWriteObj((Object)ed, ed);
          }
        }
      } catch (IOException ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException(ex.toString(), ex);
      }
    }

    @Test
    public void TestWrite2() {
      try {
        RandomGenerator fr = new RandomGenerator();

        EFloat ef = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(ef);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)ef);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(ef, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(ef, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)ef, null);
        }

        ef = EFloat.FromString("20");
        {
          CBORObject cborTemp1 = CBORObject.FromObject(ef);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)ef);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(ef, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(ef, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject(ef));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)ef, ef);
        }

        ERational er = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(er);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)er);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(er, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(er, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)er, null);
        }
        do {
          er = RandomObjects.RandomERational(fr);
        } while (er.isNegative() && er.isZero());
        {
          CBORObject cborTemp1 = CBORObject.FromObject(er);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)er);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(er, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(er, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            if (cborTemp1.isNegative() && cborTemp1.isZero()) {
              AssertReadThree(ms.toByteArray());
            } else {
              AssertReadThree(ms.toByteArray(), CBORObject.FromObject(er));
            }
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)er, er);
        }

        EDecimal ed = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(ed);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)ed);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(ed, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(ed, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)ed, null);
        }

        EInteger bigint = null;
        {
          CBORObject cborTemp1 = CBORObject.FromObject(bigint);
          CBORObject cborTemp2 = CBORObject.FromObject((Object)bigint);
          TestCommon.CompareTestEqualAndConsistent(cborTemp1, cborTemp2);
          try {
            CBORObject.Write(bigint, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(bigint, ms);
            CBORObject.Write(cborTemp1, ms);
            cborTemp1.WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject((Object)null));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
          TestWriteObj((Object)bigint, null);
        }
      } catch (IOException ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException(ex.toString(), ex);
      }
    }
    @Test
    public void TestWriteJSON() {
      // not implemented yet
    }
    @Test
    public void TestWriteJSONTo() {
      // not implemented yet
    }
    @Test
    public void TestWriteTo() {
      // not implemented yet
    }

    @Test
    public void TestZero() {
      {
        String stringTemp = CBORObject.Zero.toString();
        Assert.assertEquals(
        "0",
        stringTemp);
      }
      Assert.assertEquals(
  CBORObject.FromObject(0),
  CBORObject.Zero);
    }

    static void CompareDecimals(CBORObject o1, CBORObject o2) {
      int cmpDecFrac = TestCommon.CompareTestReciprocal(
        o1.AsEDecimal(),
        o2.AsEDecimal());
      int cmpCobj = TestCommon.CompareTestReciprocal(o1, o2);
      if (cmpDecFrac != cmpCobj) {
        Assert.assertEquals(TestCommon.ObjectMessages(o1, o2, "Compare: Results don't match"),cmpDecFrac,cmpCobj);
      }
      CBORTestCommon.AssertRoundTrip(o1);
      CBORTestCommon.AssertRoundTrip(o2);
    }

    private static void AreEqualExact(double a, double b) {
      if (Double.isNaN(a)) {
        if (!(Double.isNaN(b))) {
 Assert.fail();
 }
      } else if (a != b) {
        Assert.fail("expected " + a + ", got " + b);
      }
    }

    private static void AreEqualExact(float a, float b) {
      if (Float.isNaN(a)) {
        if (!(Float.isNaN(b))) {
 Assert.fail();
 }
      } else if (a != b) {
        Assert.fail("expected " + a + ", got " + b);
      }
    }

    private static void AssertReadThree(byte[] bytes) {
      try {
        {
java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(bytes);

          CBORObject cbor1, cbor2, cbor3;
          cbor1 = CBORObject.Read(ms);
          cbor2 = CBORObject.Read(ms);
          cbor3 = CBORObject.Read(ms);
          TestCommon.CompareTestRelations(cbor1, cbor2, cbor3);
          TestCommon.CompareTestEqualAndConsistent(cbor1, cbor2);
          TestCommon.CompareTestEqualAndConsistent(cbor2, cbor3);
          TestCommon.CompareTestEqualAndConsistent(cbor3, cbor1);
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
}
      } catch (Exception ex) {
        Assert.fail(ex.toString() + "\r\n" +
          TestCommon.ToByteArrayString(bytes));
        throw new IllegalStateException(ex.toString(), ex);
      }
    }

    private static void AssertReadThree(byte[] bytes, CBORObject cbor) {
      try {
        {
java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(bytes);

          CBORObject cbor1, cbor2, cbor3;
          cbor1 = CBORObject.Read(ms);
          cbor2 = CBORObject.Read(ms);
          cbor3 = CBORObject.Read(ms);
          TestCommon.CompareTestEqualAndConsistent(cbor1, cbor);
          TestCommon.CompareTestRelations(cbor1, cbor2, cbor3);
          TestCommon.CompareTestEqualAndConsistent(cbor1, cbor2);
          TestCommon.CompareTestEqualAndConsistent(cbor2, cbor3);
          TestCommon.CompareTestEqualAndConsistent(cbor3, cbor1);
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
}
      } catch (Exception ex) {
        Assert.fail(ex.toString() + "\r\n" +
          TestCommon.ToByteArrayString(bytes) + "\r\n" +
          "cbor = " + cbor.toString() + "\r\n");
        throw new IllegalStateException(ex.toString(), ex);
      }
    }

    private static void AssertWriteThrow(CBORObject cbor) {
      try {
        cbor.WriteTo(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        CBORObject.Write(cbor, null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
        // NOTE: Intentionally empty
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }

    private static void TestWriteObj(Object obj) {
      try {
        {
          CBORObject cborTemp1 = CBORObject.FromObject(obj);
          try {
            CBORObject.Write(obj, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(obj, ms);
            CBORObject.Write(CBORObject.FromObject(obj), ms);
            CBORObject.FromObject(obj).WriteTo(ms);
            AssertReadThree(ms.toByteArray());
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
        }
      } catch (IOException ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException(ex.toString(), ex);
      }
    }

    @Test
    public void TestEMap() {
      CBORObject cbor = CBORObject.NewMap()
                    .Add("name", "Example");
      byte[] bytes = cbor.EncodeToBytes();
    }

    private void TestWriteObj(Object obj, Object objTest) {
      try {
        {
          CBORObject cborTemp1 = CBORObject.FromObject(obj);
          try {
            CBORObject.Write(obj, null);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          AssertWriteThrow(cborTemp1);
          java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

            CBORObject.Write(obj, ms);
            CBORObject.Write(CBORObject.FromObject(obj), ms);
            CBORObject.FromObject(obj).WriteTo(ms);
            AssertReadThree(ms.toByteArray(), CBORObject.FromObject(objTest));
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
        }
      } catch (IOException ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException(ex.toString(), ex);
      }
    }

    @Test
    public void TestWriteValue() {
      try {
        try {
          CBORObject.WriteValue(null, 0, 0);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          CBORObject.WriteValue(null, 1, 0);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          CBORObject.WriteValue(null, 2, 0);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          CBORObject.WriteValue(null, 3, 0);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        try {
          CBORObject.WriteValue(null, 4, 0);
          Assert.fail("Should have failed");
        } catch (NullPointerException ex) {
          // NOTE: Intentionally empty
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

          try {
            CBORObject.WriteValue(ms, -1, 0);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            CBORObject.WriteValue(ms, 8, 0);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            CBORObject.WriteValue(ms, 7, 256);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            CBORObject.WriteValue(ms, 7, Integer.MAX_VALUE);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            CBORObject.WriteValue(ms, 7, Long.MAX_VALUE);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
            // NOTE: Intentionally empty
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          for (int i = 0; i <= 7; ++i) {
            try {
              CBORObject.WriteValue(ms, i, -1);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              CBORObject.WriteValue(ms, i, Integer.MIN_VALUE);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              CBORObject.WriteValue(ms, i, (long)-1);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              CBORObject.WriteValue(ms, i, Long.MIN_VALUE);
              Assert.fail("Should have failed");
            } catch (IllegalArgumentException ex) {
              // NOTE: Intentionally empty
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
          }
          for (int i = 0; i <= 6; ++i) {
            try {
              CBORObject.WriteValue(ms, i, Integer.MAX_VALUE);
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
            try {
              CBORObject.WriteValue(ms, i, Long.MAX_VALUE);
            } catch (Exception ex) {
              Assert.fail(ex.toString());
              throw new IllegalStateException("", ex);
            }
          }
          // Test minimum data length
          int[] ranges = {
        0, 23, 1,
        24, 255, 2,
        256, 266, 3,
        65525, 65535, 3,
        65536, 65546, 5,
      };
          String[] bigRanges = {
        "4294967285", "4294967295",
        "4294967296", "4294967306",
        "18446744073709551604", "18446744073709551615"
      };
          int[] bigSizes = { 5, 9, 9, 5, 9, 9 };
          for (int i = 0; i < ranges.length; i += 3) {
            for (int j = ranges[i]; j <= ranges[i + 1]; ++j) {
              for (int k = 0; k <= 6; ++k) {
                int count;
                count = CBORObject.WriteValue(ms, k, j);
                Assert.assertEquals(ranges[i + 2], count);
                count = CBORObject.WriteValue(ms, k, (long)j);
                Assert.assertEquals(ranges[i + 2], count);
                count = CBORObject.WriteValue(ms, k, EInteger.FromInt32(j));
                Assert.assertEquals(ranges[i + 2], count);
              }
            }
          }
          for (int i = 0; i < bigRanges.length; i += 2) {
            EInteger bj = EInteger.FromString(bigRanges[i]);
            EInteger valueBjEnd = EInteger.FromString(bigRanges[i + 1]);
            while (bj.compareTo(valueBjEnd)< 0) {
              for (int k = 0; k <= 6; ++k) {
                int count;
                count = CBORObject.WriteValue(ms, k, bj);
                Assert.assertEquals(bigSizes[i / 2], count);
              }
              bj = bj.Add(EInteger.FromInt32(1));
            }
          }
}
finally {
try { if (ms != null) {
 ms.close();
 } } catch (java.io.IOException ex) {}
}
      } catch (IOException ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException(ex.toString(), ex);
      }
    }
  }
