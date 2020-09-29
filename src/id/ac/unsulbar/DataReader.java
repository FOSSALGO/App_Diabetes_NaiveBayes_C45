package id.ac.unsulbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataReader {

    private StringBuffer sb = null;

    public String toString() {
        return sb.toString();
    }

    public ArrayList<String[]> readFromFile(File file) {
        ArrayList<String[]> data = null;
        try {
            sb = new StringBuffer();
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String baris = br.readLine(); //Baca Header dari Data CSV            
            data = new ArrayList<String[]>();
            int nomor = 1;
            while ((baris = br.readLine()) != null) {
                String[] dBaris = baris.split(";");
                double gd = Double.parseDouble(dBaris[0].trim()); //Membaca Gula Darah Perkolom
                double td = Double.parseDouble(dBaris[1].trim()); //Membaca Tekanan Darah Perkolom
                double bb = Double.parseDouble(dBaris[2].trim()); //Membaca Berat Badan Perkolom
                double u = Double.parseDouble(dBaris[3].trim()); //Membaca Umur Perkolom
                String jk = dBaris[4].trim(); //Membaca Jenis Kelamin
                String sts = dBaris[5].trim(); //Membaca Status

                //Melakukan Kategori pada Data
                String gulaDarah = null;
                String tekananDarah = null;
                String beratBadan = null;
                String umur = null;
                String jenisKelamin = null;
                String status = null;

                //gulaDarah
                if (gd < 95) {
                    gulaDarah = "GULADARAH_LOW";
                } else if (gd >= 95 && gd <= 140) {
                    gulaDarah = "GULADARAH_MEDIUM";
                } else if (gd > 140) {
                    gulaDarah = "GULADARAH_HIGH";
                }
                //tekananDarah
                if (td < 100) {
                    tekananDarah = "TEKANANDARAH_LOW";
                } else if (td >= 100 && td <= 140) {
                    tekananDarah = "TEKANANDARAH_NORMAL";
                } else if (td > 140) {
                    tekananDarah = "TEKANANDARAH_HIGH";
                }
                //beratBadan
                if (bb < 45) {
                    beratBadan = "BERATBADAN_LOW";
                } else if (bb >= 45 && bb <= 50) {
                    beratBadan = "BERATBADAN_NORMAL";
                } else if (bb > 50 && bb <= 60) {
                    beratBadan = "BERATBADAN_OBESE";
                } else if (bb > 60) {
                    beratBadan = "BERATBADAN_SEVERELYOBESE";
                }
                //umur
                if (u < 45) {
                    umur = "UMUR_YOUNG";
                } else if (u >= 45 && u <= 60) {
                    umur = "UMUR_MEDIUM";
                } else if (u > 60) {
                    umur = "UMUR_OLD";
                }
                //jenisKelamin
                if (jk.equalsIgnoreCase("P")) {
                    jenisKelamin = "JENISKELAMIN_PEREMPUAN";
                } else if (jk.equalsIgnoreCase("L")) {
                    jenisKelamin = "JENISKELAMIN_LAKILAKI";
                }
                //Status
                if (sts.equalsIgnoreCase("N")) {
                    status = "STATUS_NEGATIF";
                } else if (sts.equalsIgnoreCase("P")) {
                    status = "STATUS_POSITIF";
                }
                //inisialisasi Data
                String[] values = {gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin, status};
                data.add(values);

                //cetak
//                System.out.print("Gula Darah: " + gulaDarah);
//                System.out.print(" | Tekanan Darah: " + tekananDarah);
//                System.out.print(" | Berat Badan: " + beratBadan);
//                System.out.print(" | Umur: " + umur);
//                System.out.print(" | Jenis Kelamin: " + jenisKelamin);
//                System.out.print(" | Status: " + status);
//                System.out.println();
                //end of cetak+
                sb.append("("+(nomor++)+") Gula Darah: " + gulaDarah + " | Tekanan Darah: " + tekananDarah + " | Berat Badan: " + beratBadan + " | Umur: " + umur + " | Jenis Kelamin: " + jenisKelamin + " | Status: " + status + "\n");
            }
            br.close();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(DataReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
}
