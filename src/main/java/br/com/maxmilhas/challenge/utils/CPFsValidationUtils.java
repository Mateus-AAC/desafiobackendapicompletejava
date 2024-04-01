package br.com.maxmilhas.challenge.utils;

public class CPFsValidationUtils {
    public static boolean isValidCPF(String cpf) {
        if (cpf.length() != 11 || cpf.matches(cpf.charAt(0) + "{11}")) {
            return false;
        }

        int sum = 0;
        int weight = 10;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * weight;
            weight--;
        }

        int remainder = 11 - (sum % 11);
        int digit1 = (remainder == 10 || remainder == 11) ? 0 : remainder;

        sum = 0;
        weight = 11;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * weight;
            weight--;
        }

        remainder = 11 - (sum % 11);
        int digit2 = (remainder == 10 || remainder == 11) ? 0 : remainder;

        return digit1 == Character.getNumericValue(cpf.charAt(9)) && digit2 == Character.getNumericValue(cpf.charAt(10));
    }
}