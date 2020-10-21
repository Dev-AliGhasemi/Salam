package ir.vira.utils;

public enum EncryptionAlgorithm {
    DSA("DSA"),
    AES("AES");

    private String encryptionAlgorithm;

    EncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
}
