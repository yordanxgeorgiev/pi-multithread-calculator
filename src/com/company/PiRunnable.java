package com.company;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.ApintMath;

public class PiRunnable implements Runnable{

    private final int startNumber, endNumber, startNumber2, endNumber2, apfloatPrecision, threadNumber;
    private final boolean QUIET_MODE;
    private Apfloat sum;

    public PiRunnable(int threadNumber, int startNumber, int endNumber, boolean QUIET_MODE, int startNumber2, int endNumber2, int apfloatPrecision)
    {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.QUIET_MODE = QUIET_MODE;
        this.startNumber2 = startNumber2;
        this.endNumber2 = endNumber2;
        this.apfloatPrecision = apfloatPrecision;
        this.threadNumber = threadNumber;

        sum = new Apfloat(0, apfloatPrecision);
    }

    public Apfloat getSum()
    {
        return sum;
    }

    @Override
    public void run()
    {
        long start = 0, end;

        if(!QUIET_MODE)
        {
            System.out.println("Thread " + threadNumber + " started.");
            start = System.currentTimeMillis();
        }

        final Apfloat const1 = new Apfloat("545140134", apfloatPrecision);
        final Apfloat const2 = new Apfloat("13591409", apfloatPrecision);
        final Apfloat const3 = new Apfloat("-262537412640768000", apfloatPrecision);

        Apfloat A = Main.factorials[6*startNumber].divide(Main.factorials[3*startNumber]).divide(ApfloatMath.pow(Main.factorials[startNumber], 3));
        Apfloat B = const2.add(const1.multiply(new Apfloat(startNumber, apfloatPrecision)));
        Apfloat C = ApfloatMath.pow(const3, startNumber);
        sum = sum.add(A.multiply(B).divide(C));

        for(int i = startNumber+1; i < endNumber+1; i++) {
            A = Main.factorials[6 * i].divide(Main.factorials[3 * i]).divide(ApfloatMath.pow(Main.factorials[i], 3));
            //A = Main.factorials[6*i].divide(Main.factorials[3*i].multiply(ApfloatMath.pow(Main.factorials[i],3 )));
            B = B.add(const1);
            C = C.multiply(ApfloatMath.pow(const3, 1));
            //C = C.multiply(C);
            sum = sum.add(A.multiply(B).divide(C));
        }

        Apfloat A1 = Main.factorials[6*startNumber2].divide(Main.factorials[3*startNumber2]).divide(ApfloatMath.pow(Main.factorials[startNumber2], 3));
        Apfloat B1 = const2.add(const1.multiply(new Apfloat(startNumber2, apfloatPrecision)));
        Apfloat C1 = ApfloatMath.pow(const3, startNumber2);
        sum = sum.add(A1.multiply(B1).divide(C1));

        for(int i = startNumber2+1; i < endNumber2+1; i++) {
            A1 = Main.factorials[6 * i].divide(Main.factorials[3 * i]).divide(ApfloatMath.pow(Main.factorials[i], 3));
            B1 = B1.add(const1);
            C1 = C1.multiply(ApfloatMath.pow(const3, 1));
            sum.add(A1.multiply(B1).divide(C1));
        }

        if(!QUIET_MODE)
        {
            end = System.currentTimeMillis();
            System.out.println("Thread " + threadNumber + " stopped. Running time(millis) - " + (end - start));
        }
    }
}