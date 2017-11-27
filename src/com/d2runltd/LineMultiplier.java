package com.d2runltd;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LineMultiplier implements Callable<int[][]>
{
	private ArrayList<ArrayList<Integer>> A;
	private ArrayList<ArrayList<Integer>> B;
	private int start;
	private int end;
	private int[][] C;

	LineMultiplier(ArrayList<ArrayList<Integer>> a,
				   ArrayList<ArrayList<Integer>> b, int s, int e)
	{
		A = a;
		B = b;
		C = new int[a.size()][b.get(0).size()];
		start = s;
		end = e;
	}

	@Override
	public int[][] call()
	{
		for (int i = start; i < end; i++)
		{
			for (int k = 0; k < B.size(); k++)
			{
				for (int j = 0; j < B.get(0).size(); j++)
				{
					C[i][j] += A.get(i).get(k) * B.get(k).get(j);
				}
			}
		}
		return C;
	}
}
