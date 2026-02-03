package com.example.shopeeerp.util;

public final class SensitiveDataMasker {
    private SensitiveDataMasker() {}

    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        if (phone.length() < 11) {
            return phone;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(phone, 0, 3);
        sb.append("****");
        sb.append(phone, phone.length() - 4, phone.length());
        return sb.toString();
    }

    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return idCard;
        }
        if (idCard.length() < 8) {
            return idCard;
        }
        int middleLen = idCard.length() - 8;
        StringBuilder sb = new StringBuilder();
        sb.append(idCard, 0, 4);
        for (int i = 0; i < middleLen; i++) {
            sb.append('*');
        }
        sb.append(idCard, idCard.length() - 4, idCard.length());
        return sb.toString();
    }

    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.isEmpty()) {
            return bankCard;
        }
        if (bankCard.length() < 8) {
            return bankCard;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bankCard, 0, 4);
        sb.append("****");
        sb.append(bankCard, bankCard.length() - 4, bankCard.length());
        return sb.toString();
    }

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return email;
        }
        String local = email.substring(0, atIndex);
        if (local.length() < 3) {
            return email;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(local, 0, 3);
        sb.append("***");
        sb.append(email.substring(atIndex));
        return sb.toString();
    }

    public static String maskPassword(String password) {
        return "******";
    }
}
