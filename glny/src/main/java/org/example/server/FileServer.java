package org.example.server;


import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("FileServer")
public class FileServer {
    public static void getsss(String[] args) {
        // 前17位的SSCC编码，这里使用示例编码
        String ssccBase = "12345678901234567";

        // 计算校验位
        char checkDigit = calculateCheckDigit(ssccBase);

        // 构建完整的18位SSCC编码
        String ssccWithCheckDigit = ssccBase + checkDigit;

        System.out.println("生成的18位SSCC编码： " + ssccWithCheckDigit);
    }

    public static char calculateCheckDigit(String input) {
        int sum = 0;
        for (int i = 0; i < input.length(); i++) {
            int digit = Character.getNumericValue(input.charAt(i));
            int weight = (i % 2 == 0) ? 1 : 3; // 设置权重，奇数位为3，偶数位为1
            sum += digit * weight;
        }

        int checkDigitValue = 10 - (sum % 10);
        return Character.forDigit(checkDigitValue % 10, 10); // 将个位数字转换为字符
    }
}
