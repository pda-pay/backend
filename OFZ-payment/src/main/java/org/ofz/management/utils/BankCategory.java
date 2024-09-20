package org.ofz.management.utils;

import java.util.HashMap;
import java.util.Map;

public enum BankCategory {
    WOORI("01", "우리은행"),
    SHINHAN("02", "신한은행"),
    NONGHYUP("03", "농협은행"),
    IBK("04", "기업은행"),
    HANA("05", "하나은행");

    private final String code;
    private final String name;

    private static final Map<String, BankCategory> CODE_MAP = new HashMap<>();

    static {
        for (BankCategory category : BankCategory.values()) {
            CODE_MAP.put(category.getCode(), category);
        }
    }

    BankCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String fromCode(String code) {
        BankCategory category = CODE_MAP.get(code);

        if (category == null) {
            throw new IllegalArgumentException("Invalid bank code: " + code);
        }

        return category.getName();
    }
}