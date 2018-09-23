package com.upokecenter.cbor;
/*
Written by Peter O. in 2014.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://peteroupc.github.io/
 */

import com.upokecenter.numbers.*;

  class CBORDateConverter implements ICBORObjectConverter<java.util.Date> {
    private static String DateTimeToString(java.util.Date bi) {
      int[] lesserFields = new int[7];
      EInteger[] year = new EInteger[1];
      PropertyMap.BreakDownDateTime(bi, year, lesserFields);
      return CBORUtilities.ToAtomDateTimeString(year[0], lesserFields);
    }

    public CBORObject ValidateObject(CBORObject obj) {
      if (obj.getType() != CBORType.TextString) {
        throw new CBORException("Not a text String");
      }
      return obj;
    }

    public java.util.Date FromCBORObject(CBORObject obj) {
      if (obj.HasMostOuterTag(0)) {
        return StringToDateTime(obj.AsString());
      } else if (obj.HasMostOuterTag(1)) {
        if (!obj.isFinite()) {
          throw new CBORException("Not a finite number");
        }
          EDecimal dec = obj.AsEDecimal();
          int[] lesserFields = new int[7];
          EInteger[] year = new EInteger[1];
          CBORUtilities.BreakDownSecondsSinceEpoch(
                  dec,
                  year,
                  lesserFields);
          return PropertyMap.BuildUpDateTime(year[0], lesserFields);
      }
      throw new CBORException("Not tag 0 or 1");
    }

    public static java.util.Date StringToDateTime(String str) {
      int[] lesserFields = new int[7];
      EInteger[] year = new EInteger[1];
      CBORUtilities.ParseAtomDateTimeString(str, year, lesserFields);
      return PropertyMap.BuildUpDateTime(year[0], lesserFields);
    }

    public CBORObject ToCBORObject(java.util.Date obj) {
      return CBORObject.FromObjectAndTag(DateTimeToString(obj), 0);
    }
  }