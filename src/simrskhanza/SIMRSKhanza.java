/*
  Dilarang keras memperjualbelikan/mengambil keuntungan dari Software 
  ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */
package simrskhanza;

import fungsi.DlgAktivasi;
import fungsi.LicenseManager;
import javax.swing.JOptionPane;
import usu.widget.util.WidgetUtilities;

/**
 *
 * @author khanzasoft
 */
public class SIMRSKhanza {

    private static int sisaHariLisensi = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // 🔥 CEK LISENSI SEBELUM APLIKASI DIJALANKAN
        int sisaHari = LicenseManager.cekLisensi();
        
        // Cek koneksi internet
        if (sisaHari == -2) {
            return;
        }
        
        // Cek lisensi expired atau tidak ada
        if (sisaHari <= 0) {
            boolean expired = (sisaHari == 0);
            DlgAktivasi dlg = new DlgAktivasi(null, expired);
            dlg.setVisible(true);
            
            if (!dlg.isActivated()) {
                JOptionPane.showMessageDialog(null,
                    "Aplikasi tidak dapat dijalankan tanpa lisensi aktif.\n" +
                    "Hubungi developer: WA 085334254726",
                    "Lisensi Diperlukan", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }
            
            // Setelah aktivasi, cek ulang
            sisaHari = LicenseManager.cekLisensi();
            if (sisaHari <= 0) {
                JOptionPane.showMessageDialog(null,
                    "Aktivasi gagal! Hubungi developer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                return;
            }
        }
        
        // Tampilkan warning jika hampir habis
        if (sisaHari > 0 && sisaHari <= 7) {
            JOptionPane.showMessageDialog(null,
                "⚠ Lisensi akan habis dalam " + sisaHari + " hari!\n" +
                "Segera hubungi developer untuk perpanjangan.\n" +
                "WA: 085334254726",
                "Peringatan Lisensi", JOptionPane.WARNING_MESSAGE);
        }
        
        // Simpan sisa hari untuk ditampilkan di title
        sisaHariLisensi = sisaHari;
        
        // Jalankan aplikasi utama
        WidgetUtilities.invokeLater(() -> {
           frmUtama utama = frmUtama.getInstance();
           utama.setSisaHariLisensi(sisaHariLisensi);  // Kirim sisa hari ke frmUtama
           utama.isWall();
           utama.updateWindowTitle();
           utama.setVisible(true);
        }); 
    }
    
    public static int getSisaHariLisensi() {
        return sisaHariLisensi;
    }
}