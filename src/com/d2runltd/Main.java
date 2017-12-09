package com.d2runltd;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Main
{
    private static List<ArrayList<ArrayList<Integer>>> read(String filename)
    {
        ArrayList<ArrayList<Integer>> A = new ArrayList<>();
        ArrayList<ArrayList<Integer>> B = new ArrayList<>();

        String thisLine;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            // Begin reading A
            while ((thisLine = br.readLine()) != null)
            {
                if (thisLine.trim().equals(""))
                    break;

                else
                {
                    ArrayList<Integer> line = new ArrayList<>();
                    String[] lineArray = thisLine.split("\t");

                    for (String number : lineArray)
                        line.add(Integer.parseInt(number));

                    A.add(line);
                }
            }

            // Begin reading B
            while ((thisLine = br.readLine()) != null)
            {
                ArrayList<Integer> line = new ArrayList<>();
                String[] lineArray = thisLine.split("\t");
                for (String number : lineArray)
                    line.add(Integer.parseInt(number));

                B.add(line);
            }

            br.close();
        } catch (IOException e)
        {
            System.err.println("Error: " + e);
        }

        List<ArrayList<ArrayList<Integer>>> res = new LinkedList<>();
        res.add(A);
        res.add(B);
        return res;
    }

    private static void printMatrix(int[][] matrix)
    {
        for (int[] line : matrix)
        {
            int i = 0;
            StringBuilder sb = new StringBuilder(matrix.length);
            for (int number : line)
            {
                if (i != 0)
                    sb.append("\t");
                else
                    i++;

                sb.append(number);
            }
            System.out.println(sb.toString());
        }
    }

    private static int[][] parallelMult(ArrayList<ArrayList<Integer>> A,
                                        ArrayList<ArrayList<Integer>> B, int coreNumber)
    {
        int[][] C = new int[A.size()][B.get(0).size()];
        ExecutorService executor = Executors.newFixedThreadPool(coreNumber);
        List<Future<int[][]>> list = new ArrayList<>();

        int part = A.size() / coreNumber;
        if (part < 1)
            part = 1;

        for (int i = 0; i < A.size(); i += part)
        {
            Callable<int[][]> worker = new LineMultiplier(A, B, i, i+part);
            Future<int[][]> submit = executor.submit(worker);
            list.add(submit);
        }

        // now retrieve the result
        int start = 0;
        int CF[][];
        for (Future<int[][]> future : list)
        {
            try
            {
                CF = future.get();
                for (int i=start; i < start+part; i += 1)
                {
                    C[i] = CF[i];
                }
            } catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
            start+=part;
        }
        executor.shutdown();

        return C;
    }

    public static void main(String[] args) throws IOException
    {
        String filename;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        Integer coresInput = null;
        int cores = Runtime.getRuntime().availableProcessors(), i = 1;

        System.out.println("Number of available cores:\t" + cores);

        do
        {
            try
            {
                System.out.println("How many cores do you want to work on?: ");
                coresInput = Integer.parseInt(bufferedReader.readLine());

                if(coresInput > cores || coresInput< 1)
                    throw new Exception();

            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (Exception e)
            {
                System.out.println("Wrong input, Try again!");
                i=-1;
            }
        } while (i<0);

        System.out.println("Wait...");

        filename = "files\\1000.in";   //change the matrix you want to work on here....

        List<ArrayList<ArrayList<Integer>>> matrices = read(filename);

        ArrayList<ArrayList<Integer>> A = matrices.get(0);
        ArrayList<ArrayList<Integer>> B = matrices.get(1);

        long startTime = System.currentTimeMillis();

        int[][] C = parallelMult(A, B, coresInput);

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        printMatrix(C);

        System.out.println("Time taken: " + totalTime + "ms");
    }
}