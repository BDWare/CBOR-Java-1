package com.upokecenter.cbor;

import java.util.*;

import com.upokecenter.util.*;

  final class OptionsParser {
    private final Map<String, String> dict = new
HashMap<String, String>();

    private static String[] SplitAt(String str, String delimiter) {
      if (delimiter == null) {
        throw new NullPointerException("delimiter");
      }
      if (delimiter.length() == 0) {
        throw new IllegalArgumentException("delimiter is empty.");
      }
      if (((str) == null || (str).length() == 0)) {
        return new String[] { "" };
      }
      int index = 0;
      boolean first = true;
      ArrayList<String> strings = null;
      int delimLength = delimiter.length();
      while (true) {
        int index2 = str.indexOf(delimiter, index);
        if (index2 < 0) {
          if (first) {
            String[] strret = new String[1];
            strret[0] = str;
            return strret;
          }
          strings = (strings == null) ? (new ArrayList<String>()) : strings;
          strings.add(str.substring(index));
          break;
        } else {
          first = false;
          String newstr = str.substring(index, (index)+(index2 - index));
          strings = (strings == null) ? (new ArrayList<String>()) : strings;
          strings.add(newstr);
          index = index2 + delimLength;
        }
      }
      return strings.toArray(new String[] { });
    }

    public OptionsParser(String options) {
      if (options == null) {
        throw new NullPointerException("options");
      }
      if (options.length() > 0) {
        String[] optionsArray = SplitAt(options, ";");
        for (String opt : optionsArray) {
          int index = opt.indexOf('=');
          if (index < 0) {
            throw new IllegalArgumentException("Invalid options String: " + options);
          }
          String key = DataUtilities.ToLowerCaseAscii(opt.substring(0, index));
          String value = opt.substring(index + 1);
          this.dict.put(key, value);
        }
      }
    }

    public boolean GetBoolean(String key, boolean defaultValue) {
      String lckey = DataUtilities.ToLowerCaseAscii(key);
      if (this.dict.containsKey(lckey)) {
        String lcvalue = DataUtilities.ToLowerCaseAscii(this.dict.get(lckey));
        return lcvalue.equals("1") ||
lcvalue.equals("yes") ||
            lcvalue.equals("on") ||
lcvalue.equals("true");
      }
      return defaultValue;
    }
  }
