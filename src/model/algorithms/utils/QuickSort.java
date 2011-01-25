package model.algorithms.utils;

public class QuickSort {
	
	public static void sort(float a[][]) {
		qSort(a, 0, a.length - 1);
	}

	private static void qSort(float a[][], int left, int right) {
		if (left < right) {
			int i = partition(a, left, right);
			qSort(a, left, i - 1);
			qSort(a, i + 1, right);
		}
	}

	private static int partition(float a[][], int left, int right) {
		int i, j;
		float[] tmp, pivot;
		pivot = a[right];
		i = left;
		j = right - 1;
		
		while (i <= j) {
			if (compare(a[i], pivot) > 0) {

				tmp = a[i];
				a[i] = a[j];
				a[j] = tmp;
				j--;
			} else
				i++;
		}

		tmp = a[i];
		a[i] = a[right];
		a[right] = tmp;

		return i;
	}

	private static int compare(final float[] a, final float[] b) {
		for (int i = 0; i < 256; i++) {
			if (a[i] < b[i]) {
				return -1;
			} else if (a[i] > b[i]) {
				return 1;
			}
		}

		return 0;
	}
}
