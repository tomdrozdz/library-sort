import java.util.Comparator;

public class LibrarySort<T> {
	
	private int gap;
	private Comparator<T> comparator;
	
	public LibrarySort(int gap, Comparator<T> comparator) throws IllegalArgumentException {
		this.comparator = comparator;
		
		if(gap <= 0)
			throw new IllegalArgumentException("The gap should be at least 1");
		else
			this.gap = gap+1;
	}
	
	public void Librarysort(T[] sequence) {
		
		if(sequence.length <= 1) return;
		T[] gapped = (T[]) new Object[gap];
		gapped[0] = sequence[0];
		for(int i=1; i<gapped.length; i++)
			gapped[i] = null;
		
		gapped = startSorting(sequence, gapped);
		
		for(int i=0, j=0; i<sequence.length; j++)
			if(gapped[j] != null)
				sequence[i++] = gapped[j];
	}
	
	private T[] startSorting(T[] sequence, T[] gapped) {
		
		//Every time we double the number of already sorted elements
		for(int pos = 1, goal = 1; pos<sequence.length; goal *= 2) {
			for(int i=0; i<goal; i++) {
				
				int insPos = binarySearch(gapped, sequence[pos]);
				
				insPos++; //Inserting after the found element
				
				// Extra case when the element must be inserted at the end
				if(insPos == gapped.length) {	
					insPos--;
					
					int free = insPos - 1;
					while (gapped[free] != null)
						free--;
					
					for (; free < insPos; free++)
						gapped[free] = gapped[free + 1];
				}
				// Case where the position is taken
				else if(gapped[insPos] != null) {

					// Searching to the right for a free space 
					int free = insPos + 1;
					while (free < gapped.length && gapped[free] != null)
						free++;
					
					// Searching to the left in case the right side is full
					if (free == gapped.length) {
						
						// If we are shifting elements to the left, the current one must also be
						// inserted one place to the left
						insPos--;

						free = insPos - 1;
						while (gapped[free] != null)
							free--;
						
						// Shifting elements to the left
						for (; free < insPos; free++)
							gapped[free] = gapped[free + 1];
					}
					else  // Shifting elements to the right if an empty space is found
						for (; free > insPos; free--)
							gapped[free] = gapped[free - 1];
				}
				gapped[insPos] = sequence[pos++];
				
				if(pos >= sequence.length) return gapped;
			}
			gapped = rebalance(gapped, sequence);
		}
		return gapped;
	}
	
	private int binarySearch(T[] gapped, T elem) {
		
		int left = 0;
		int mid;
		int right = gapped.length - 1;
		
		// Finding first values
		while(gapped[right] == null)
			right--;
		while(gapped[left] == null)
			left++;
		
		while(left <= right) {
			
			mid = (left+right)/2;
			
			// Finding 
			if(gapped[mid] == null) {
				
				int tmp = mid + 1;
				
				// Searching to the right
				while (tmp < right && gapped[tmp] == null)
					tmp++;
				
				if (gapped[tmp] == null || comparator.compare(gapped[tmp], elem) > 0) {
					
					while (mid >= left && gapped[mid] == null)
						mid--;
					
					if (comparator.compare(gapped[mid], elem) < 0)
						return mid;		// Returns position of an element, not an empty space
					
					right = mid - 1;
				} else
					left = tmp + 1;
			}
			else if(comparator.compare(gapped[mid], elem) < 0)
				left = mid + 1;
			else
				right = mid - 1;
		}
		
		// To be consistent, this case must also return an element and not an empty space
		while(right >= 0 && gapped[right] == null)
			right--;
		
		return right;
	}
	
	private T[] rebalance(T[] gapped, T[] sequence) {
		
		T[] rebalanced = (T[]) new Object[min(2*gapped.length, gap*sequence.length)];
		int eps = gap-1;
		
		// Reinserting the elements with new gaps
		for(int i = gapped.length - 1, j = rebalanced.length - 1; i >= 0; i--)
			if(gapped[i] != null) {
				rebalanced[j--] = gapped[i];
				
				for(int k=0; k<eps; k++)
					rebalanced[j--] = null;
			}
		
		return rebalanced;
	}
	
	private int min(int n1, int n2) {
		return n1 < n2 ? n1 : n2;
	}
}