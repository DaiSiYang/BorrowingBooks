package com.example.borrowingbooks.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    // 校验手机号
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^(1[3-9])\\d{9}$";
        return match(regex, phoneNumber);
    }

    // 校验邮箱
    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zAZ0-9]{2,7}$";
        return match(regex, email);
    }

    // 校验身份证号
    public static boolean isValidIDCard(String idCard) {
        String regex = "^(\\d{15}|\\d{18}|\\d{17}(\\d|X|x))$";
        return match(regex, idCard);
    }

    // 校验URL
    public static boolean isValidURL(String url) {
        String regex = "^(https?|ftp)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$";
        return match(regex, url);
    }

    // 校验密码（至少8位，包含数字和字母）
    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return match(regex, password);
    }

    // 校验是否符合正则表达式
    private static boolean match(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static void main(String[] args) {
        // 测试
        System.out.println("手机号校验： " + isValidPhoneNumber("13912345678")); // true
        System.out.println("邮箱校验： " + isValidEmail("test@example.com")); // true
        System.out.println("身份证号校验： " + isValidIDCard("123456789012345")); // false
        System.out.println("URL校验： " + isValidURL("https://www.example.com")); // true
        System.out.println("密码校验： " + isValidPassword("abc12345")); // true
    }
}
