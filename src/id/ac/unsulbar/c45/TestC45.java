package id.ac.unsulbar.c45;

import id.ac.unsulbar.DataReader;
import java.io.File;
import java.util.ArrayList;

public class TestC45 {
    public static void main(String[] args) {
        String[] dataLabels = {"GULADARAH", "TEKANANDARAH", "BERATBADAN", "UMUR", "JENISKELAMIN", "STATUS"};//label terakhir adalah target

        String[][] dataValues = {
            {"GULADARAH_LOW", "GULADARAH_MEDIUM", "GULADARAH_HIGH"},
            {"TEKANANDARAH_LOW", "TEKANANDARAH_NORMAL", "TEKANANDARAH_HIGH"},
            {"BERATBADAN_LOW", "BERATBADAN_NORMAL", "BERATBADAN_OBESE", "BERATBADAN_SEVERELYOBESE"},
            {"UMUR_YOUNG", "UMUR_MEDIUM", "UMUR_OLD"},
            {"JENISKELAMIN_PEREMPUAN", "JENISKELAMIN_LAKILAKI"},
            {"STATUS_POSITIF", "STATUS_NEGATIF"}
        };

        String[] pattern = {null, null, null, null, null, null};

        ArrayList<String[]> data = null;
        //Baca Data dari File
        File fileDataTraining = new File("data_training.csv");
        data = new DataReader().readFromFile(fileDataTraining);

        //generate Node
        int level = 0;
        int iNode = -1;
        String edge = null;//root
        String leaf = null;
        Node node = new Node(level, dataLabels, dataValues, data, pattern, iNode, edge, leaf);
        String sModel = node.toString();
        System.out.println(sModel);
        
        //test
        String statusTest = node.test("GULADARAH_LOW", "TEKANANDARAH_NORMAL", "BERATBADAN_NORMAL", "UMUR_YOUNG", "JENISKELAMIN_LAKILAKI");
        //String statusTest = node.test("GULADARAH_HIGH", "TEKANANDARAH_NORMAL", "BERATBADAN_SEVERELYOBESE", "UMUR_YOUNG", "JENISKELAMIN_LAKILAKI");
        System.out.println("Test Result: "+statusTest);
    }
}
