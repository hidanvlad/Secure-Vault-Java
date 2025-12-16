public class Main {
    public static void main(String[] args) {
        String originalText = "MySuperSecretPassword";
        String masterKey = "Apple123";

        System.out.println("Original: " + originalText);

        // 1. Lock it
        String encrypted = CryptoUtils.encrypt(originalText, masterKey);
        System.out.println("Encrypted: " + encrypted);

        // 2. Unlock it
        String decrypted = CryptoUtils.decrypt(encrypted, masterKey);
        System.out.println("Decrypted: " + decrypted);

        // 3. Try to unlock with WRONG password
        String wrong = CryptoUtils.decrypt(encrypted, "Orange456");
        System.out.println("With wrong key: " + wrong);
    }
}