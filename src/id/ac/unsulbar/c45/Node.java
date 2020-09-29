package id.ac.unsulbar.c45;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {

    private static final long serialVersionUID = 1706L;

    //field input
    private final int level;
    private final String[] dataLabels;
    private final String[][] dataValues;
    private final ArrayList<String[]> data;
    private final String[] pattern;
    private final int iNode;
    private final String edge;
    private final String leaf;

    //field proses
    double[] Gain;
    Node[] childs = null;

    public Node(int level, String[] dataLabels, String[][] dataValues, ArrayList<String[]> data, String[] pattern, int iNode, String edge, String leaf) {
        this.level = level;
        this.dataLabels = dataLabels;
        this.dataValues = dataValues;
        this.data = data;
        this.pattern = pattern;
        this.iNode = iNode;
        this.edge = edge;
        this.leaf = leaf;
        this.expand();
    }

    private boolean validasi() {
        boolean valid = false;
        if (leaf == null && dataLabels != null && dataValues != null && data != null && pattern != null
                && dataLabels.length == dataValues.length
                && dataLabels.length == pattern.length
                && dataLabels.length == data.get(0).length) {
            valid = true;
        }
        return valid;
    }

    private void expand() {
        if (validasi()) {
            int indexC = dataLabels.length - 1;//System.out.println("indexC: "+indexC);
            Gain[] gain = new Gain[indexC];
            double MAX_GAIN = Double.MIN_VALUE;
            int iMAX = -1;
            for (int k = 0; k < gain.length; k++) {
                gain[k] = null;
                if (pattern[k] == null) {
                    int indexAtribut = k;
                    String[] newPattern = this.pattern.clone();
                    gain[k] = new Gain(indexAtribut, newPattern, dataLabels, dataValues, data);
                    if (gain[k].getGain() > MAX_GAIN) {
                        MAX_GAIN = gain[k].getGain();
                        iMAX = k;
                    }
                }
            }
            if (iMAX >= 0 && iMAX < indexC) {
                String[] classification = gain[iMAX].getClassification();
                childs = new Node[classification.length];
                for (int i = 0; i < childs.length; i++) {
                    String[] newPattern = pattern.clone();
                    newPattern[iMAX] = dataValues[iMAX][i];
                    String leaf_i = classification[i];
                    int new_level = level + 1;
                    String edge = dataValues[iMAX][i];
                    Node node = new Node(new_level, dataLabels, dataValues, data, newPattern, iMAX, edge, leaf_i);
                    childs[i] = node;
                }
            }
        }
    }

    public String test(String gulaDarah, String tekananDarah, String beratBadan, String umur, String jenisKelamin) {
        String result = "";
        if (gulaDarah != null && tekananDarah != null && beratBadan != null && umur != null && jenisKelamin != null) {
            String[] dataTest = {gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin, null};
            Node nodeTest = this;
            if (nodeTest.leaf != null) {
                result = nodeTest.leaf;
            } else {
                if (nodeTest.childs != null) {
                    for (Node child : nodeTest.childs) {
                        int iNodeChild = child.iNode;
                        String s1 = dataTest[iNodeChild];
                        String s2 = child.edge;
                        if (s1.equalsIgnoreCase(s2)) {
                            result = child.test(gulaDarah, tekananDarah, beratBadan, umur, jenisKelamin);
                            break;
                        }
                    }
                }else{
                    //result = "STATUS_NEGATIF";
                    result = "NULL";
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < level; i++) {
            sb.append(" ------ ");
        }
        if (iNode <= -1) {
            sb.append("[root]");
        } else {
            sb.append("[" + dataLabels[iNode] + "]");
        }
        if (edge == null) {
            sb.append("");
        } else {
            sb.append("-" + edge);
        }
        if (leaf == null) {
            sb.append("-expand\n");
        } else {
            sb.append("--#" + leaf + "\n");
        }
        //iterasi childs
        Node nodeTest = this;
        if (nodeTest.childs != null) {
            for (Node child : nodeTest.childs) {
                String sChild = child.toString();
                sb.append(sChild);
            }
        }
        return sb.toString();
    }

}
