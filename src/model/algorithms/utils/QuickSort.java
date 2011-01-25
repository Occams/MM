package model.algorithms.utils;

public class QuickSort {

	public static void sort(short a[][], int[] compareMask) {
		qSort(a, 0, a.length - 1, compareMask);
	}

	private static void qSort(short a[][], int left, int right,
			int[] compareMask) {
		int i = partition(a, left, right, compareMask);
		
		if (left < i - 1)
			qSort(a, left, i - 1, compareMask);
		if (i < right)
			qSort(a, i, right, compareMask);
	}

	private static int partition(short a[][], int left, int right,
			int[] compareMask) {
		int i, j;
		short[] tmp, pivot;
		pivot = a[(right + left) / 2];
		i = left;
		j = right;

		while (i <= j) {
			while (compare(a[i], pivot, compareMask) < 0)
				i++;
			while (compare(a[j], pivot, compareMask) > 0)
				j--;
			if (i <= j) {
				tmp = a[i];
				a[i] = a[j];
				a[j] = tmp;
				i++;
				j--;
			}
		}

		return i;
	}

	private static int compare(final short[] a, final short[] b, int[] compareMask) {
		for (int i = 0; i < compareMask.length; i++) {
			if (a[compareMask[i]] < b[compareMask[i]]) {
				return -1;
			} else if (a[compareMask[i]] > b[compareMask[i]]) {
				return 1;
			}
		}

		return 0;
	}
}
