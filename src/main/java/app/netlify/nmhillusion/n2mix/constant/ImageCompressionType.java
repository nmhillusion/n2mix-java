package app.netlify.nmhillusion.n2mix.constant;


import java.util.List;


public enum ImageCompressionType {
    CCITT_RLE("CCITT RLE"),
    CCITT_T_6("CCITT T.6"),
    CCITT_T_4("CCITT T.4"),
    LZW("LZW"),
    ZLib("ZLib"),
    PackBits("PackBits"),
    Deflate("Deflate");

    private final String value;

    ImageCompressionType(String value) {
        this.value = value;
    }

    public static ImageCompressionType fromValue(String rawValue) {
        final List<ImageCompressionType> values = List.of(values());
        return values.parallelStream()
                .filter(type_ -> type_.value.equals(rawValue))
                .findFirst()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }
}
