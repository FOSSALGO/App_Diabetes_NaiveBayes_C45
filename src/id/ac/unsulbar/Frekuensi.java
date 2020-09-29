package id.ac.unsulbar;


import java.util.ArrayList;

public class Frekuensi {

    private double[] S = null;

    public double[] hitung(String[] pattern, String[] dataLabels, String[][] dataValues, ArrayList<String[]> data) {
        int indexClass = dataLabels.length - 1;
        int numClass = dataValues[indexClass].length;
        S = new double[numClass];
        //lakukan iterasi pada data
        for (String[] values : data) {
            String target = values[indexClass];
            for (int i = 0; i < numClass; i++) {
                if (target.equalsIgnoreCase(dataValues[indexClass][i])) {
                    String[] newPattern = pattern.clone();
                    newPattern[indexClass] = dataValues[indexClass][i];
                    boolean match = true;
                    for (int j = 0; j < newPattern.length; j++) {
                        if (newPattern[j] != null && !newPattern[j].equalsIgnoreCase(values[j])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        S[i]++;
                    }
                    break;
                }
            }

        }
        return S;
    }
}
