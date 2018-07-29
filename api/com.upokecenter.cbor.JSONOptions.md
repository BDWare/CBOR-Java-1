# com.upokecenter.cbor.JSONOptions

    public final class JSONOptions extends Object

Includes options to control how CBOR objects are converted to JSON.

## Fields

* `static JSONOptions Default`<br>
 The default options for converting CBOR objects to JSON.

## Constructors

* `JSONOptions() JSONOptions`<br>
 Initializes a new instance of the JSONOptions class with default
 options.
* `JSONOptions​(boolean base64Padding) JSONOptions`<br>
 Initializes a new instance of the JSONOptions class with the given
 values for the options.

## Methods

* `boolean getBase64Padding()`<br>
 If true, include padding when writing data in base64url or
 traditional base64 format to JSON.

## Field Details

### Default
    public static final JSONOptions Default
The default options for converting CBOR objects to JSON.
## Method Details

### getBase64Padding
    public final boolean getBase64Padding()
If <b>true</b>, include padding when writing data in base64url or
 traditional base64 format to JSON.<p> The padding character is '='.
 </p>

**Returns:**

* The default is false, no padding.