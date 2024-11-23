import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AplikasiCekCuacaWeatherAPI extends JFrame {
    private JComboBox<String> comboLokasi;
    private JButton tombolCek;
    private JLabel labelHasil, labelGambar;

    // API key Anda
    private static final String API_KEY = "f14cb8f56d8e4456a9c42115242111";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";

    public AplikasiCekCuacaWeatherAPI() {
        setTitle("Aplikasi Cuaca - WeatherAPI");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background warna untuk JFrame
        getContentPane().setBackground(new Color(240, 248, 255)); // AliceBlue

        // Panel atas untuk memilih lokasi
        JPanel panelInput = new JPanel(new FlowLayout());
        panelInput.setBackground(new Color(173, 216, 230)); // LightBlue
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel labelLokasi = new JLabel("Pilih Lokasi:");
        labelLokasi.setFont(new Font("Arial", Font.BOLD, 14));

        comboLokasi = new JComboBox<>(new String[]{"Jakarta", "Surabaya", "Bandung", "Banjarmasin", "Banjarbaru"});
        comboLokasi.setFont(new Font("Arial", Font.PLAIN, 14));
        comboLokasi.setPreferredSize(new Dimension(150, 25));

        tombolCek = new JButton("Cek Cuaca");
        tombolCek.setFont(new Font("Arial", Font.BOLD, 14));
        tombolCek.setBackground(new Color(30, 144, 255)); // DodgerBlue
        tombolCek.setForeground(Color.WHITE);

        panelInput.add(labelLokasi);
        panelInput.add(comboLokasi);
        panelInput.add(tombolCek);
        add(panelInput, BorderLayout.NORTH);

        // Panel hasil
        JPanel panelHasil = new JPanel(new BorderLayout());
        panelHasil.setBackground(new Color(240, 248, 255)); // AliceBlue
        panelHasil.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelHasil = new JLabel("Informasi cuaca akan tampil di sini", SwingConstants.CENTER);
        labelHasil.setFont(new Font("Arial", Font.PLAIN, 16));
        labelHasil.setForeground(new Color(70, 130, 180)); // SteelBlue

        labelGambar = new JLabel("", SwingConstants.CENTER);

        panelHasil.add(labelHasil, BorderLayout.CENTER);
        panelHasil.add(labelGambar, BorderLayout.SOUTH);
        add(panelHasil, BorderLayout.CENTER);

        // Event Listener
        tombolCek.addActionListener(e -> cekCuaca());

        setVisible(true);
    }

    private void cekCuaca() {
        String lokasi = (String) comboLokasi.getSelectedItem();
        if (lokasi == null || lokasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih lokasi terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Bangun URL API
            String endpoint = BASE_URL + "?key=" + API_KEY + "&q=" + lokasi + "&aqi=no";
            URL url = new URL(endpoint);

            // Buka koneksi HTTP
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Baca respons
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parsing manual respons JSON
            String jsonResponse = response.toString();
            String kondisi = ambilNilai(jsonResponse, "\"text\":\"", "\"");
            String suhu = ambilNilai(jsonResponse, "\"temp_c\":", ",");
            String icon = ambilNilai(jsonResponse, "\"icon\":\"", "\"");

            // Tampilkan hasil
            labelHasil.setText("<html>Suhu: " + suhu + "°C<br>Kondisi: " + kondisi + "</html>");
            ImageIcon iconCuaca = new ImageIcon(new URL("http:" + icon));
            labelGambar.setIcon(iconCuaca);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String ambilNilai(String json, String kunciAwal, String kunciAkhir) {
        try {
            int startIndex = json.indexOf(kunciAwal) + kunciAwal.length();
            int endIndex = json.indexOf(kunciAkhir, startIndex);
            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "Data tidak ditemukan";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AplikasiCekCuacaWeatherAPI::new);
    }
}
