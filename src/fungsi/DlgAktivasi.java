package fungsi;

import javax.swing.*;

public class DlgAktivasi extends javax.swing.JDialog {

    private boolean activated = false;

    public DlgAktivasi(java.awt.Frame parent, boolean expired) {
        super(parent, "Aktivasi Lisensi", true);
        initComponents(expired);
        setLocationRelativeTo(parent);
    }

    private void initComponents(boolean expired) {
        setSize(420, 220);
        setResizable(false);
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 10, 8, 10);
        gbc.fill   = java.awt.GridBagConstraints.HORIZONTAL;

        // Info
        String pesan = expired
            ? "⚠ Lisensi Anda telah habis! Masukkan key baru:"
            : "Masukkan key lisensi untuk mengaktifkan aplikasi:";
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(new JLabel(pesan), gbc);

        // Input key
        JTextArea txtKey = new JTextArea(3, 30);
        txtKey.setLineWrap(true);
        txtKey.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtKey);
        gbc.gridy = 1;
        add(scroll, gbc);

        // Info kontak
        gbc.gridy = 2;
        add(new JLabel("Hubungi developer: WA 085334254726"), gbc);

        // Tombol
        JButton btnAktivasi = new JButton("Aktivasi");
        JButton btnBatal    = new JButton("Batal");

        gbc.gridy = 3; gbc.gridwidth = 1; gbc.gridx = 0;
        add(btnAktivasi, gbc);
        gbc.gridx = 1;
        add(btnBatal, gbc);

        btnAktivasi.addActionListener(e -> {
            String key = txtKey.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Key tidak boleh kosong!");
                return;
            }
            if (LicenseManager.aktivasiKey(key)) {
                activated = true;
                JOptionPane.showMessageDialog(this,
                    "✓ Aktivasi berhasil!\n" + LicenseManager.infoLisensi(),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "✗ Key tidak valid atau sudah kadaluarsa!\nHubungi developer.",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBatal.addActionListener(e -> dispose());
    }

    public boolean isActivated() { return activated; }
}