package com.example.urlshortener.Utility;

import lombok.experimental.UtilityClass;
import org.unbrokendome.base62.Base62;

@UtilityClass
public class Base62Utility{

    public static String encodeLong(long input) {
        return Base62.encode(input);
    }

    public static String encodeUrl(String input) {
        long hashCode= input.hashCode();
        return Base62.encode(hashCode);
    }
}
