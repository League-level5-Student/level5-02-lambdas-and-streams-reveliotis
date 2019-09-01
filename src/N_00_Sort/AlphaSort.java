package N_00_Sort;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class AlphaSort {

    public static void main(String[] args) {
        alphaSortArray();
        alphaSortList();
        alphaSortArrayWithNulls();
        alphaSortListWithNulls();
        numericThenAlphaSortArrayWithNulls();
        numericThenAlphaSortListWithNulls();
    }
    
    private static final Number[] NUMBERS = new Number[] { 1, 100.0, 2.1, 20, 3f, 0.1, 200, 1000.5f};
    
    private static void alphaSortArray() {
        // sort NUMBERS alphabetically
        
        // using anonymous class
        Arrays.parallelSort(NUMBERS, 
                            new Comparator<Number>() {
                                public int compare(Number n1, Number n2) {
                                    return n1.toString().compareTo(n2.toString());
                                }
                            }
        );

        // using lambda expression
        Arrays.parallelSort(NUMBERS, (n1, n2) -> n1.toString().compareTo(n2.toString())); 
        
        assertArrayEquals(NUMBERS, new Number[] { 0.1, 1, 100.0, 1000.5f, 2.1, 20, 200, 3f });
    }
    
    private static final List<Number> NUMBER_LIST = Arrays.asList(NUMBERS);
    
    private static void alphaSortList() {
        // sort NUMBER_LIST alphabetically
        
        // using anonymous class
        Collections.sort(NUMBER_LIST, 
                         new Comparator<Number>() {
                             public int compare(Number n1, Number n2) {
                                 return n1.toString().compareTo(n2.toString());
                             }
                         }
        );

        // using lambda expression
        Collections.sort(NUMBER_LIST, (n1, n2) -> n1.toString().compareTo(n2.toString())); 
        
        assertEquals(NUMBER_LIST, Arrays.asList(new Number[] { 0.1, 1, 100.0, 1000.5f, 2.1, 20, 200, 3f }));
    }

    private static final Number[] NUMBERS_WITH_NULLS = new Number[] { 1, 100.0, 2.1, 20, 3f, 0.1, 200, null, 1000.5f};
    
    private static void alphaSortArrayWithNulls() {
        // sort NUMBERS_WITH_NULLS alphabetically assuming NULLS first
        
        // using anonymous class
        Arrays.parallelSort(NUMBERS, 
                            Comparator.nullsFirst(
                                new Comparator<Number>() {
                                    public int compare(Number n1, Number n2) {
                                        return n1.toString().compareTo(n2.toString());
                                    }
                                }
                            )
        );

        // using lambda expression
        Arrays.parallelSort(NUMBERS_WITH_NULLS, Comparator.nullsFirst((n1, n2) -> n1.toString().compareTo(n2.toString()))); 
        
        assertArrayEquals(NUMBERS_WITH_NULLS, new Number[] { null, 0.1, 1, 100.0, 1000.5f, 2.1, 20, 200, 3f });
    }
    
    private static final List<Number> NUMBER_LIST_WITH_NULLS = Arrays.asList(NUMBERS_WITH_NULLS);
    
    private static void alphaSortListWithNulls() {
        // sort NUMBER_LIST_WITH_NULLS alphabetically assuming NULLS first
        
        // using anonymous class
        Collections.sort(NUMBER_LIST_WITH_NULLS, 
                         Comparator.nullsFirst(
                             new Comparator<Number>() {
                                 public int compare(Number n1, Number n2) {
                                    return n1.toString().compareTo(n2.toString());
                                 }
                             }
                         )
        );

        // using lambda expression
        Collections.sort(NUMBER_LIST_WITH_NULLS, Comparator.nullsFirst((n1, n2) -> n1.toString().compareTo(n2.toString()))); 
        
        assertEquals(NUMBER_LIST_WITH_NULLS, Arrays.asList(new Number[] { null, 0.1, 1, 100.0, 1000.5f, 2.1, 20, 200, 3f }));
    }
            
    @FunctionalInterface
    private static interface NullSafeChainComparator<T> extends Comparator<T> {
        
        @Override
        default Comparator<T> thenComparing(Comparator<? super T> other) {
            Objects.requireNonNull(other);
            return (Comparator<T> & Serializable) (c1, c2) -> {
                int res = compare(c1, c2);
                if (res != 0) return res;
                // otherwise
                try {    
                    return other.compare(c1, c2);
                }
                catch(NullPointerException e) {
                    return Comparator.nullsFirst(other).compare(c1, c2);
                }
            };
        }
    }
    
    private static final Number[] MORE_NUMBERS_WITH_NULLS = new Number[] { 1, 100.0, 2.1, 20, 3.0, 0.1, 200.0, 200, null, 1.0, 1000.5f, null};
    
    private static void numericThenAlphaSortArrayWithNulls() {
        // sort MORE_NUMBERS_WITH_NULLS first numerically and then alphabetically assuming NULLS first
        
        // using anonymous class
        Arrays.parallelSort(MORE_NUMBERS_WITH_NULLS, 
                                new NullSafeChainComparator<Number>() {
                                    public int compare(Number n1, Number n2) {
                                        if (n1 == null) {
                                            if (n2 == null) return 0;
                                            return -1;
                                        }
                                        if (n2 == null) return 1;
                                        double d1 = n1.doubleValue();
                                        double d2 = n2.doubleValue();
                                        return d1 == d2 ? 0 : d1 < d2 ? -1 : 1;
                                    }                                    
                                }.thenComparing(
                                    new Comparator<Number>() {
                                        public int compare(Number n1, Number n2) {
                                            return n1.toString().compareTo(n2.toString());
                                        }
                                    }
                                )
        );

        // using lambda expression
        Arrays.parallelSort(MORE_NUMBERS_WITH_NULLS, 
                ((NullSafeChainComparator<Number>) (n1, n2) -> {
//                ((Comparator<Number>)(n1, n2) -> {
                    if (n1 == null) {
                        if (n2 == null) return 0;
                        return -1;
                    }
                    if (n2 == null) return 1;
                    double d1 = n1.doubleValue();
                    double d2 = n2.doubleValue();
                    return d1 == d2 ? 0 : d1 < d2 ? -1 : 1;
                }).thenComparing(
                (n1, n2) -> n1.toString().compareTo(n2.toString()))
        ); 
        
        assertEquals(Arrays.toString(MORE_NUMBERS_WITH_NULLS), "[null, null, 0.1, 1, 1.0, 2.1, 3.0, 20, 100.0, 200, 200.0, 1000.5]");
    }
    
    private static final List<Number> OTHER_NUMBER_LIST_WITH_NULLS = Arrays.asList(MORE_NUMBERS_WITH_NULLS);

    private static void numericThenAlphaSortListWithNulls() {
        // sort OTHER_NUMBER_LIST_WITH_NULLS first numerically and then alphabetically assuming NULLS first
        
        // using anonymous class
        Collections.sort(OTHER_NUMBER_LIST_WITH_NULLS, 
                         new NullSafeChainComparator<Number>() {
                             public int compare(Number n1, Number n2) {
                                 if (n1 == null) {
                                     if (n2 == null) return 0;
                                     return -1;
                                 }
                                 if (n2 == null) return 1;
                                 double d1 = n1.doubleValue();
                                 double d2 = n2.doubleValue();
                                 return d1 == d2 ? 0 : d1 < d2 ? -1 : 1;
                             }                                    
                         }.thenComparing(
                             new Comparator<Number>() {
                                 public int compare(Number n1, Number n2) {
                                     return n1.toString().compareTo(n2.toString());
                                 }
                             }
                         )
        );

        // using lambda expression
        Collections.sort(OTHER_NUMBER_LIST_WITH_NULLS, 
                ((NullSafeChainComparator<Number>) (n1, n2) -> {
//                ((Comparator<Number>)(n1, n2) -> {
                    if (n1 == null) {
                        if (n2 == null) return 0;
                        return -1;
                    }
                    if (n2 == null) return 1;
                    double d1 = n1.doubleValue();
                    double d2 = n2.doubleValue();
                    return d1 == d2 ? 0 : d1 < d2 ? -1 : 1;
                }).thenComparing(
                (n1, n2) -> n1.toString().compareTo(n2.toString()))
        ); 
        
        assertEquals(OTHER_NUMBER_LIST_WITH_NULLS.toString(), "[null, null, 0.1, 1, 1.0, 2.1, 3.0, 20, 100.0, 200, 200.0, 1000.5]");
    }

}
