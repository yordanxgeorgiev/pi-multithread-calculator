package com.company;

import org.apache.commons.cli.*;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static Apfloat[] factorials;

    public static void main(String[] args) throws InterruptedException, ParseException, IOException {

        System.out.println("Starting program.");
        long start, end;
        int apfloatPrecision;

        start = System.currentTimeMillis();

        // Setting up the options
        Options options = new Options();
        options.addOption("p", true, "precision - members of the series");
        options.addOption("t", true, "threads - how many");
        options.addOption("o", true, "output file");
        options.addOption("q", false, "quiet mode");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        // Getting the options
        boolean QUIET_MODE= false;
        String str_precision = cmd.getOptionValue("p");
        String str_threads = cmd.getOptionValue("t");
        String str_output = cmd.getOptionValue("o");

        /*
        str_precision = "5000";
        apfloatPrecision = 5000;
        str_threads = "16";
        */

        if(str_precision == null || str_threads == null){
            System.out.println("Error: -p and -t options are necessary");
            System.exit(-1);
        }
        if(str_output == null){ str_output = "output.txt"; }

        // Creating output file if it doesn't exist
        try {
            File output = new File(str_output);
            output.createNewFile();
        }
        catch (Exception ignored){}

        FileWriter myWriter = new FileWriter(str_output);

        int precision = Integer.parseInt(str_precision);
        apfloatPrecision = precision;
        int n_threads = Integer.parseInt(str_threads);
        if(cmd.hasOption("q")) QUIET_MODE = true;

        // calculating the factorials
        factorials = new Apfloat[precision*6+1];
        factorials[0] = new Apfloat("1", apfloatPrecision);

        Apfloat res = new Apfloat("1", apfloatPrecision);

        int iter;
        for(iter = 1; iter < precision+1; iter++)
        {
            res = res.multiply(new Apfloat(iter, apfloatPrecision));
            factorials[iter] = res;
        }

        while(iter < 6*precision + 1)
        {
            res = res.multiply(new Apfloat(iter, apfloatPrecision));
            if(iter%3 == 0)
            {
                factorials[iter] = res;
            }
            iter++;
        }

        // Setting up the threads
        Thread[] threads = new Thread[n_threads];
        PiRunnable[] runnables = new PiRunnable[n_threads];

        // Starting the threads

        int perThread =precision / 2 /  n_threads;

        for(int i = 0; i < n_threads-1; i++)
        {
            runnables[i] = new PiRunnable(i, i*perThread,(i+1)*perThread-1, QUIET_MODE, precision - (i+1)*perThread + 1,precision - i*perThread, apfloatPrecision);
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }

        runnables[n_threads-1] = new PiRunnable(n_threads-1, (n_threads-1)*perThread,(n_threads-1)*perThread+1, QUIET_MODE, (n_threads-1)*perThread + 2,precision - (n_threads-1)*perThread, apfloatPrecision);
        threads[n_threads-1] = new Thread(runnables[n_threads-1]);
        threads[n_threads-1].start();

        Apfloat sum = new Apfloat(0,apfloatPrecision);

        // Waiting for the threads to finish
        for(int i = 0; i < n_threads; i++)
        {
            threads[i].join();
            sum = sum.add(runnables[i].getSum());
        }

        // Calculating a constant and pi
        Apfloat sqrtC = ApfloatMath.sqrt(new Apfloat("10005",apfloatPrecision));
        Apfloat C = sqrtC.multiply(new Apfloat("426880", apfloatPrecision));
        Apfloat pi = C.divide(sum);

        // Writing pi in the output file
        myWriter.write(String.valueOf(pi));
        myWriter.close();

        //System.out.println(pi);

        end = System.currentTimeMillis();
        if(!QUIET_MODE)
        {
            System.out.println();
            System.out.println("Threads used - " + n_threads);
        }

        System.out.println("Total execution time (millis): " + (end-start));
    }

}
