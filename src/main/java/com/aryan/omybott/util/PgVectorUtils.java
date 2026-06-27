package com.aryan.omybott.util;

public final class PgVectorUtils {

    public static final int EMBEDDING_DIMENSIONS = 768;

    private PgVectorUtils() {
    }

    public static String toLiteral(float[] embedding) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding[i]);
        }
        builder.append(']');
        return builder.toString();
    }

}
