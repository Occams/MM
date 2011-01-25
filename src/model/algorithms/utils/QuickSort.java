package model.algorithms.utils;

public class QuickSort {

	public static void sort(float a[][]) {
		qSort(a, 0, a.length - 1);
	}

	private static void qSort(float a[][], int left, int right) {
		int i = partition(a, left, right);
		if (left < i - 1)
			qSort(a, left, i - 1);
		if (i < right)
			qSort(a, i, right);
	}

	private static int partition(float a[][], int left, int right) {
		int i, j;
		float[] tmp, pivot;
		pivot = a[(right + left) / 2];
		i = left;
		j = right;

		while (i <= j) {
			while (compare(a[i], pivot) < 0)
				i++;
			while (compare(a[j], pivot) > 0)
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
