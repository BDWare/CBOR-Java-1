package com.upokecenter.test;

import java.util.*;
import java.io.*;

import org.junit.Assert;
import org.junit.Test;
import com.upokecenter.util.*;

  public class DataUtilitiesTest {
    public static List<byte[]> GenerateIllegalUtf8Sequences() {
      ArrayList<byte[]> list = new ArrayList<byte[]>();
      // Generate illegal single bytes
      for (int i = 0x80; i <= 0xff; ++i) {
        if (i < 0xc2 || i > 0xf4) {
          list.add(new byte[] { (byte)i, (byte)0x80  });
        }
        list.add(new byte[] { (byte)i  });
      }
      list.add(new byte[] { (byte)0xe0, (byte)0xa0  });
      list.add(new byte[] { (byte)0xe1, (byte)0x80  });
      list.add(new byte[] { (byte)0xef, (byte)0x80  });
      list.add(new byte[] { (byte)0xf0, (byte)0x90  });
      list.add(new byte[] { (byte)0xf1, (byte)0x80  });
      list.add(new byte[] { (byte)0xf3, (byte)0x80  });
      list.add(new byte[] { (byte)0xf4, (byte)0x80  });
      list.add(new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80  });
      list.add(new byte[] { (byte)0xf1, (byte)0x80, (byte)0x80  });
      list.add(new byte[] { (byte)0xf3, (byte)0x80, (byte)0x80  });
      list.add(new byte[] { (byte)0xf4, (byte)0x80, (byte)0x80  });
      // Generate illegal multibyte sequences
      for (int i = 0x00; i <= 0xff; ++i) {
        if (i < 0x80 || i > 0xbf) {
          list.add(new byte[] { (byte)0xc2, (byte)i  });
          list.add(new byte[] { (byte)0xdf, (byte)i  });
          list.add(new byte[] { (byte)0xe1, (byte)i, (byte)0x80  });
          list.add(new byte[] { (byte)0xef, (byte)i, (byte)0x80  });
          list.add(new byte[] { (byte)0xf1, (byte)i, (byte)0x80, (byte)0x80  });
          list.add(new byte[] { (byte)0xf3, (byte)i, (byte)0x80, (byte)0x80  });
          list.add(new byte[] { (byte)0xe0, (byte)0xa0, (byte)i  });
          list.add(new byte[] { (byte)0xe1, (byte)0x80, (byte)i  });
          list.add(new byte[] { (byte)0xef, (byte)0x80, (byte)i  });
          list.add(new byte[] { (byte)0xf0, (byte)0x90, (byte)i, (byte)0x80  });
          list.add(new byte[] { (byte)0xf1, (byte)0x80, (byte)i, (byte)0x80  });
          list.add(new byte[] { (byte)0xf3, (byte)0x80, (byte)i, (byte)0x80  });
          list.add(new byte[] { (byte)0xf4, (byte)0x80, (byte)i, (byte)0x80  });
          list.add(new byte[] { (byte)0xf0, (byte)0x90, (byte)0x80, (byte)i  });
          list.add(new byte[] { (byte)0xf1, (byte)0x80, (byte)0x80, (byte)i  });
          list.add(new byte[] { (byte)0xf3, (byte)0x80, (byte)0x80, (byte)i  });
          list.add(new byte[] { (byte)0xf4, (byte)0x80, (byte)0x80, (byte)i  });
        }
        if (i < 0xa0 || i > 0xbf) {
          list.add(new byte[] { (byte)0xe0, (byte)i, (byte)0x80  });
        }
        if (i < 0x90 || i > 0xbf) {
          list.add(new byte[] { (byte)0xf0, (byte)i, (byte)0x80, (byte)0x80  });
        }
        if (i < 0x80 || i > 0x8f) {
          list.add(new byte[] { (byte)0xf4, (byte)i, (byte)0x80, (byte)0x80  });
        }
      }
      return list;
    }

    @Test
    public void TestCodePointAt() {
      try {
        DataUtilities.CodePointAt(null, 0);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(-1, DataUtilities.CodePointAt("A", -1));
      Assert.assertEquals(-1, DataUtilities.CodePointAt("A", 1));
      Assert.assertEquals(0x41, DataUtilities.CodePointAt("A", 0));
    }
    @Test
    public void TestCodePointBefore() {
      try {
        DataUtilities.CodePointBefore(null, 0);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      // not implemented yet
    }
    @Test
    public void TestCodePointCompare() {
      Assert.assertEquals(
        0,
        ((DataUtilities.CodePointCompare("abc", "abc")==0) ? 0 : ((DataUtilities.CodePointCompare("abc", "abc")< 0) ? -1 : 1)));
      Assert.assertEquals(
        0,
        ((DataUtilities.CodePointCompare("\ud800\udc00" , "\ud800\udc00"
)==0) ? 0 : ((DataUtilities.CodePointCompare("\ud800\udc00" , "\ud800\udc00"
)< 0) ? -1 : 1)));
      Assert.assertEquals(
        -1,
        ((DataUtilities.CodePointCompare("abc" , "\ud800\udc00")==0) ? 0 : ((DataUtilities.CodePointCompare("abc" , "\ud800\udc00")< 0) ? -1 : 1)));
      Assert.assertEquals(
        -1,
        ((DataUtilities.CodePointCompare("\uf000" , "\ud800\udc00")==0) ? 0 : ((DataUtilities.CodePointCompare("\uf000" , "\ud800\udc00")< 0) ? -1 : 1)));
      Assert.assertEquals(
        1,
        ((DataUtilities.CodePointCompare("\uf000" , "\ud800")==0) ? 0 : ((DataUtilities.CodePointCompare("\uf000" , "\ud800")< 0) ? -1 : 1)));
    }
    @Test
    public void TestGetUtf8Bytes() {
      try {
        DataUtilities.GetUtf8Bytes(null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestGetUtf8Length() {
      try {
        DataUtilities.GetUtf8Length(null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(6, DataUtilities.GetUtf8Length("ABC\ud800", true));
      Assert.assertEquals(-1, DataUtilities.GetUtf8Length("ABC\ud800", false));
      try {
        DataUtilities.GetUtf8Length(null, true);
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      try {
        DataUtilities.GetUtf8Length(null, false);
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString()); throw new
          IllegalStateException("", ex);
      }
      Assert.assertEquals(3, DataUtilities.GetUtf8Length("abc", true));
      Assert.assertEquals(4, DataUtilities.GetUtf8Length("\u0300\u0300", true));
      Assert.assertEquals(6, DataUtilities.GetUtf8Length("\u3000\u3000", true));
      Assert.assertEquals(6, DataUtilities.GetUtf8Length("\ud800\ud800", true));
      Assert.assertEquals(-1, DataUtilities.GetUtf8Length("\ud800\ud800", false));
    }
    @Test
    public void TestGetUtf8String() {
      try {
        DataUtilities.GetUtf8String(null, 0, 1, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.GetUtf8String(new byte[] { 0  }, -1, 1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.GetUtf8String(new byte[] { 0  }, 2, 1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.GetUtf8String(new byte[] { 0  }, 0, -1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.GetUtf8String(new byte[] { 0  }, 0, 2, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.GetUtf8String(new byte[] { 0  }, 1, 1, true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      Assert.assertEquals(
        "ABC",
        DataUtilities.GetUtf8String(
          new byte[] { 0x41,
            0x42,
            0x43  },
          0,
          3,
          true));
      Assert.assertEquals(
        "ABC\ufffd",
        DataUtilities.GetUtf8String(
          new byte[] { 0x41,
            0x42,
            0x43,
            (byte)0x80  },
          0,
          4,
          true));
      try {
        DataUtilities.GetUtf8String(
          new byte[] { 0x41,
            0x42,
            0x43,
            (byte)0x80  },
          0,
          4,
          false);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      List<byte[]> illegalSeqs = GenerateIllegalUtf8Sequences();
      for (byte[] seq : illegalSeqs) {
        try {
          DataUtilities.GetUtf8String(seq, false);
          Assert.fail("Should have failed");
        } catch (IllegalArgumentException ex) {
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        String strret = DataUtilities.GetUtf8String(seq, true);
        if (!(strret.length() > 0))Assert.fail();
        Assert.assertEquals('\ufffd', strret.charAt(0));
        try {
          DataUtilities.GetUtf8String(seq, 0, seq.length, false);
          Assert.fail("Should have failed");
        } catch (IllegalArgumentException ex) {
        } catch (Exception ex) {
          Assert.fail(ex.toString());
          throw new IllegalStateException("", ex);
        }
        strret = DataUtilities.GetUtf8String(seq, 0, seq.length, true);
        if (!(strret.length() > 0))Assert.fail();
        Assert.assertEquals('\ufffd', strret.charAt(0));
      }
    }
    @Test
    public void TestReadUtf8() {
      try {
        DataUtilities.ReadUtf8(null, 1, null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestReadUtf8FromBytes() {
      try {
        DataUtilities.WriteUtf8("x", 0, 1, null, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8FromBytes(null, 0, 1, new StringBuilder(), true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0  },
          -1,
          1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0  },
          2,
          1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0  },
          0,
          -1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0  },
          0,
          2,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8FromBytes(
          new byte[] { 0  },
          1,
          1,
          new StringBuilder(),
          true);
        Assert.fail("Should have failed");
      } catch (IllegalArgumentException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
    }
    @Test
    public void TestReadUtf8ToString() {
      try {
        DataUtilities.ReadUtf8ToString(null);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      try {
        DataUtilities.ReadUtf8ToString(null, 1, true);
        Assert.fail("Should have failed");
      } catch (NullPointerException ex) {
      } catch (Exception ex) {
        Assert.fail(ex.toString());
        throw new IllegalStateException("", ex);
      }
      List<byte[]> illegalSeqs = GenerateIllegalUtf8Sequences();
      for (byte[] seq : illegalSeqs) {
        java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(seq);

          try {
            DataUtilities.ReadUtf8ToString(ms, -1, false);
            Assert.fail("Should have failed");
          } catch (IOException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms != null)ms.close(); } catch (java.io.IOException ex) {}
}
        java.io.ByteArrayInputStream ms = null;
try {
ms = new java.io.ByteArrayInputStream(seq);

          String strret = null;
          try {
            strret = DataUtilities.ReadUtf8ToString(ms, -1, true);
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          if (!(strret.length() > 0))Assert.fail();
          Assert.assertEquals('\ufffd', strret.charAt(0));
}
finally {
try { if (ms != null)ms.close(); } catch (java.io.IOException ex) {}
}
      }
    }
    @Test
    public void TestToLowerCaseAscii() {
      if ((DataUtilities.ToLowerCaseAscii(null)) != null)Assert.fail();
      Assert.assertEquals("abc012-=?", DataUtilities.ToLowerCaseAscii("abc012-=?"));
      Assert.assertEquals("abc012-=?", DataUtilities.ToLowerCaseAscii("ABC012-=?"));
    }
    @Test
    public void TestWriteUtf8() {
      try {
        java.io.ByteArrayOutputStream ms = null;
try {
ms = new java.io.ByteArrayOutputStream();

          try {
            DataUtilities.WriteUtf8("x", null, true);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 0, 1, null, true);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 0, 1, null, true, true);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8(null, 0, 1, ms, true);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", -1, 1, ms, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 2, 1, ms, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 0, -1, ms, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 0, 2, ms, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 1, 1, ms, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8(null, 0, 1, ms, true, true);
            Assert.fail("Should have failed");
          } catch (NullPointerException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", -1, 1, ms, true, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 2, 1, ms, true, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 0, -1, ms, true, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 0, 2, ms, true, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
          try {
            DataUtilities.WriteUtf8("x", 1, 1, ms, true, true);
            Assert.fail("Should have failed");
          } catch (IllegalArgumentException ex) {
          } catch (Exception ex) {
            Assert.fail(ex.toString());
            throw new IllegalStateException("", ex);
          }
}
finally {
try { if (ms != null)ms.close(); } catch (java.io.IOException ex) {}
}
      } catch (Exception ex) {
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    }
  }
