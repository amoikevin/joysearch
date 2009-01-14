/*
 * CPageRank.java
 *
 * Created on 2007年11月25日, 下午8:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.pagerank;

/**
 *
 * @author 海
 */
public class PageRank {

    private float[] prob;
    private int[] outLinkNumArray;
    // Link Array 2-dim if LinkArray[i,j] = 1, then it means that i node links to j
    private BigBoolArray linkArray;
    private int totalNodes;
    private float precision;
    private float[] preProb;
    private float beta;

    public PageRank() {
        totalNodes = 0;
        precision = 0.00001f;
        beta = 0.15f;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public void setBeta(float fbeta) {
        beta = fbeta;
    }

    /*
     * Method	: SetLinkArray
     *
     * Description	: To set the LinkArray and init the initial data
     *
     * Notice	: Make sure DO NOT CHANGE any LinkArray data during the process
     */
    public void setLinkArray(BigBoolArray pLinkArray, int iTotalNodes) {
        int iTemp;

        totalNodes = iTotalNodes;
        this.linkArray = pLinkArray;

        prepare();

        for (int i = 0; i < iTotalNodes; i++) {
            iTemp = 0;
            for (int j = 0; j < iTotalNodes; j++) {
                iTemp += linkArray.get(i * iTotalNodes + j) == true ? 1 : 0;
            }

            outLinkNumArray[i] = iTemp;
        }
    }

    /*
     * Method	: RunOnce
     *
     *Description	: The method is to calc PV once, and then you can call IsPrecisionEnough to see whether the precision is OK.
     */
    private void runOnce() {
        for (int i = 0; i < getProb().length; i++) {
            preProb[i] = getProb()[i];
        }
        float fTempVal;

//                cout<<"Beta :"<<Beta<<endl<<"Probability :";
//        System.out.println("Beta:" + beta + " " + "Probability：");
//        for (int m = 0; m < totalNodes; m++) {
//            System.out.print(getProb()[m] + " ");
//        }
   //     System.out.println("");
//        cout<<endl;
//
        for (int i = 0; i < totalNodes; i++) {
            fTempVal = 0.0f;
            for (int j = 0; j < totalNodes; j++) {
                if (linkArray.get(j * totalNodes + i) == true) {
                    fTempVal = fTempVal + getProb()[j] / outLinkNumArray[j];
                }
            }
            getProb()[i] = 1 - beta + beta * fTempVal;
        }
    }

    /*
     * Method	: IsPrecisionEnough
     *
     * Description	: See whether the precision of the precision of the probability is OK
     * 		  All element should constant with the precision
     */
    private boolean isPrecisionEnough() {
        boolean bret = true;
        for (int i = 0; i < totalNodes; i++) {
            if (Math.abs(getProb()[i] - preProb[i]) > precision) {
                bret = false;
                break;
            }
        }
        return bret;
    }

    public float[] run() {
        while (true) {
            if (isPrecisionEnough()) {
                break;
            }
            runOnce();
        }
        return getProb();
    }

    private void prepare() {
        prob = new float[totalNodes];
        for (int i = 0; i < totalNodes; i++) {
            getProb()[i] = (float) 1 / totalNodes;
        }

        outLinkNumArray = new int[totalNodes];
        preProb = new float[totalNodes];
    }

    public float[] getProb() {
        return prob;
    }

    public BigBoolArray getLinkArray() {
        return linkArray;
    }
}

