package id.ac.unsulbar.naivebayes;

import id.ac.unsulbar.Frekuensi;
import java.io.Serializable;
import java.util.ArrayList;

public class NaiveBayes implements Serializable {

    private static final long serialVersionUID = 2009L;

    //field input
    private final String[] dataLabels;
    private final String[][] dataValues;
    private final ArrayList<String[]> data;
    private final String[] pattern;
    //field model
    double[] PC = null;
    double[][][] PX_C = null;

    public NaiveBayes(String[] dataLabels, String[][] dataValues, ArrayList<String[]> data) {
        this.dataLabels = dataLabels;
        this.dataValues = dataValues;
        this.data = data;
        this.pattern = new String[dataLabels.length];
        training();
    }

    private boolean validasi() {
        boolean valid = false;
        if (dataLabels != null && dataValues != null && data != null && pattern != null
                && dataLabels.length == dataValues.length
                && dataLabels.length == pattern.length
                && dataLabels.length == data.get(0).length) {
            valid = true;
        }
        return valid;
    }

    private void training() {
        if (validasi()) {
            int indexC = dataLabels.length - 1;//System.out.println("indexC: "+indexC);
            int numClass = dataValues[indexC].length;
            int numAttribute = indexC;

            //bikin tabel frekuensi kelas dan tabel peluang kelas
            double[] fc = new double[numClass];
            double[] pc = new double[numClass];
            //hitung frekuensi kelas
            String[] cPattern = pattern.clone();
            fc = new Frekuensi().hitung(cPattern, dataLabels, dataValues, data);
            double sumC = 0;
            for (double f : fc) {
                sumC += f;
            }
            //hitung peluang kelas
            for (int i = 0; i < fc.length; i++) {
                pc[i] = fc[i] / sumC;
            }

            //bikin tabel frekuensi atribut di kelas tabel peluang kelas
            double[][][] fx_c = new double[numAttribute][][];
            double[][][] px_c = new double[numAttribute][][];
            for (int i = 0; i < fx_c.length; i++) {
                int J = dataValues[i].length;
                fx_c[i] = new double[J][numClass];
                px_c[i] = new double[J][numClass];
                for (int j = 0; j < J; j++) {
                    String[] xPattern = pattern.clone();
                    xPattern[i] = dataValues[i][j];
                    fx_c[i][j] = new Frekuensi().hitung(xPattern, dataLabels, dataValues, data);
                    for (int k = 0; k < fx_c[i][j].length; k++) {
                        px_c[i][j][k] = fx_c[i][j][k] / fc[k];
                    }
                }
            }
            //set variabel instance
            PC = pc.clone();
            PX_C = px_c.clone();
        }
    }

    public String test(String gulaDarah, String tekananDarah, String beratBadan, String umur, String jenisKelamin) {
        String result = "";
        if (gulaDarah != null && tekananDarah != null && beratBadan != null && umur != null && jenisKelamin != null && PC != null && PX_C != null) {
            String[] dataTest = {gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin, null};
            int indexC = dataLabels.length - 1;//System.out.println("indexC: "+indexC);
            int numClass = dataValues[indexC].length;
            int numAttribute = indexC;
            double[] probability = new double[numClass];
            for (int i = 0; i < PC.length; i++) {
                probability[i] = PC[i];
            }
            for(int i=0;i<PX_C.length;i++){
                for(int j=0;j<PX_C[i].length;j++){
                    if(dataTest[i].equalsIgnoreCase(dataValues[i][j])){
                        for(int k=0;k<PX_C[i][j].length;k++){
                            probability[k] *= PX_C[i][j][k];
                        }
                        break;
                    }
                }     
            }
            //mencari probability terbesar
            int iMAX = -1;
            double MAX = Double.MIN_VALUE;
            for(int i=0;i<probability.length;i++){
                if(probability[i]>MAX){
                    MAX = probability[i];
                    iMAX = i;
                }
            }
            //set hasil
            if(iMAX>=0){
                result = dataValues[indexC][iMAX];
            }else{
                //result = "STATUS_NEGATIF";
                result = "NULL";
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String result = null;
        if (PC != null && PX_C != null) {
            int indexC = dataLabels.length - 1;//System.out.println("indexC: "+indexC);
            int numClass = dataValues[indexC].length;
            int numAttribute = indexC;
            StringBuffer sb = new StringBuffer();
            sb.append("PRIOR PROBABILITY--------------------------------------------------------------\n");
            for (int i = 0; i < PC.length; i++) {
                sb.append("Probabilitas Hipotesis-" + dataValues[indexC][i] + " P(C" + i + ") = " + PC[i] + "\n");
            }
            sb.append("\n");
            sb.append("POSTERIORY PROBABILITY---------------------------------------------------------\n");
            for (int i = 0; i < PX_C.length; i++) {
                for (int j = 0; j < PX_C[i].length; j++) {
                    for (int k = 0; k < PX_C[i][j].length; k++) {
                        sb.append("Probabilitas Hipotesis-" + dataValues[indexC][k] + " berdasarkan kondisi " + dataValues[i][j] + " P(X" + i + "," + j + "|C" + k + ") = " + PX_C[i][j][k] + "\n");
                    }
                }
            }
            sb.append("\n");
            result = sb.toString();
        }
        return result;
    }

}
