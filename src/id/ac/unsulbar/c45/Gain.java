package id.ac.unsulbar.c45;


import id.ac.unsulbar.Frekuensi;
import java.util.ArrayList;

public class Gain {

    private double gain;
    private String[] classification = null;

    public Gain(int indexAtribut, String[] pattern, String[] dataLabels, String[][] dataValues, ArrayList<String[]> data) {
        this.hitung(indexAtribut, pattern, dataLabels, dataValues, data);
    }   
    public double getGain() {
        return gain;
    }

    public String[] getClassification() {
        return classification;
    }
    
    public double hitung(int indexAtribut, String[] pattern, String[] dataLabels, String[][] dataValues, ArrayList<String[]> data) {
        gain = 0;
        try {
            int indexClass = dataLabels.length - 1;
            //bikin tabel perhitungan node
            int numRows = dataValues[indexAtribut].length;
            int numCols = dataValues[indexClass].length;
            double[][] tabelPerhitunganNode = new double[numRows][numCols];
            for (int i = 0; i < numRows; i++) {
                //hitung frekuensi
                String[] newPattern = pattern.clone();
                newPattern[indexAtribut] = dataValues[indexAtribut][i];
                tabelPerhitunganNode[i] = new Frekuensi().hitung(newPattern, dataLabels, dataValues, data);
                double sum = 0;
            }

            //hitung jumlah frekuensi di tiap baris dan kolom
            double total = 0;
            double[] sumRows = new double[numRows];
            double[] sumCols = new double[numCols];
            for (int i = 0; i < numRows; i++) {
                double sum = 0;
                for (int j = 0; j < numCols; j++) {
                    sum += tabelPerhitunganNode[i][j];
                    sumCols[j] += tabelPerhitunganNode[i][j];
                }
                sumRows[i] = sum;
                total += sum;
            }

            //hitung entropy total
            double entropyTotal = 0;
            for (int j = 0; j < numCols; j++) {
                double value = sumCols[j];
                if (value > 0) {
                    double proporsi = value / total;
                    double minPiLogPi = -proporsi * log2(proporsi);
                    entropyTotal += minPiLogPi;
                } else {
                    entropyTotal = 0;
                    break;
                }
            }

            //hitung entropy values
            double[] entropyValues = new double[numRows];
            for (int i = 0; i < numRows; i++) {
                double entropy = 0;
                for (int j = 0; j < numCols; j++) {
                    double value = tabelPerhitunganNode[i][j];
                    if (value > 0 && sumRows[i] > 0) {
                        double proporsi = value / sumRows[i];
                        double minPiLogPi = -proporsi * log2(proporsi);
                        entropy += minPiLogPi;
                    } else {
                        entropy = 0;
                        break;
                    }
                }
                entropyValues[i] = entropy;
            }

            //hitung gain
            gain = 0;
            for (int i = 0; i < numRows; i++) {
                gain += ((sumRows[i] / total) * entropyValues[i]);
            }
            gain = entropyTotal - gain;

            if (gain < 0) {
                gain = 0;//antisipasi gain minus
            }

            //set classification
            classification = new String[numRows];
            for (int i = 0; i < numRows; i++) {
                classification[i] = null;
                double sum = sumRows[i];
                for (int j = 0; j < numCols; j++) {
                    if (tabelPerhitunganNode[i][j] >= sum) {
                        classification[i] = dataValues[indexClass][j];
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gain;
    }

    private double log2(double x) {
        return ((double) Math.log(x) / (double) Math.log(2) + 1e-10);
    }

}
