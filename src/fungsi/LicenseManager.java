package fungsi;

import AESsecurity.EnkripsiAES;
import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LicenseManager {

    private static final String LICENSE_FILE = "setting/license.lic";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static LocalDate getMySQLDate() {
        while (true) {
            try {
                Connection conn = koneksiDB.condb();
                if (conn != null && !conn.isClosed()) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT CURDATE() AS tgl")) {
                        if (rs.next()) return rs.getDate("tgl").toLocalDate();
                    }
                }
            } catch (Exception e) {
                // koneksi belum siap
            }
        }
    }

    public static int cekLisensi() {
        try {
            LocalDate today = getMySQLDate();

            File f = new File(LICENSE_FILE);
            if (!f.exists()) return -1;

            String encrypted = new String(Files.readAllBytes(f.toPath())).trim();
            String decrypted = EnkripsiAES.decrypt(encrypted);

            if (decrypted == null || decrypted.isEmpty()) return -1;

            String[] parts = decrypted.split("\\|");
            if (parts.length < 2) return -1;

            LocalDate expire = LocalDate.parse(parts[1], FMT);

            if (today.isAfter(expire)) return 0;

            return (int) (expire.toEpochDay() - today.toEpochDay());

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean aktivasiKey(String key) {
        try {
            LocalDate today = getMySQLDate();

            String decrypted = EnkripsiAES.decrypt(key.trim());
            if (decrypted == null || decrypted.isEmpty()) return false;

            String[] parts = decrypted.split("\\|");
            if (parts.length < 2) return false;

            LocalDate expire = LocalDate.parse(parts[1], FMT);
            if (today.isAfter(expire)) return false;

            Files.createDirectories(Paths.get("setting"));
            Files.write(Paths.get(LICENSE_FILE), key.trim().getBytes());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String infoLisensi() {
        try {
            LocalDate today = getMySQLDate();

            String encrypted = new String(Files.readAllBytes(Paths.get(LICENSE_FILE))).trim();
            String decrypted = EnkripsiAES.decrypt(encrypted);
            String[] parts = decrypted.split("\\|");

            LocalDate expire = LocalDate.parse(parts[1], FMT);
            int sisa = (int) (expire.toEpochDay() - today.toEpochDay());

            String status = sisa > 0 ? "Aktif" : (sisa == 0 ? "Habis" : "Tidak valid");

            return "Instansi : " + parts[0] + "\n" +
                   "Expire   : " + parts[1] + "\n" +
                   "Sisa     : " + (sisa > 0 ? sisa + " hari" : (sisa == 0 ? "HABIS" : "Invalid")) + "\n" +
                   "Status   : " + status;

        } catch (Exception e) {
            return "Lisensi tidak valid";
        }
    }
    public static String getNamaInstansi() {
    try {
        String encrypted = new String(Files.readAllBytes(Paths.get(LICENSE_FILE))).trim();
        String decrypted = EnkripsiAES.decrypt(encrypted);
        String[] parts = decrypted.split("\\|");
        return parts[0];
    } catch (Exception e) {
        return "Khanza SIMKES";
    }
}
}