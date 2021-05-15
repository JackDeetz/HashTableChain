package edu.miracosta.cs113;
import java.util.*;

/**
 * HashTable implementation using chaining to tack a pair of key and value pairs.
 * @param <K> Generic Key
 * @param <V> Generic Value
 */
public class HashTableChain<K, V> implements Map<K, V>  {

    //A table of references to LinkedLists of Entry<K, V> objects
    private LinkedList<Entry<K, V>>[] table ;
    //The number of keys (and thus entries) in the table)
    private  int numKeys ;
    //The static size of the table of references to linkedLists
    private static final int CAPACITY = 101 ;
    //Maximum Load Factor
    //  if the number of entries reaches 'LOAD_THRESHOLD' * 'table.length'
    //   the table will resize, to keep performance reasonable
    private static final double LOAD_THRESHOLD = 3 ;


    /**
     * Default constructor, sets the table to initial capacity size
     */
    public HashTableChain() {
        table = new LinkedList[CAPACITY] ;
    }

    // returns Value if table has the searched for key
    @Override
    public V get(Object key)
    {
        int index = key.hashCode() % table.length ;
        index = (index < 0 ? index + table.length : index) ;
        if (table[index] == null)
            return null ; // this key is NOT in the table
        for (Entry<K, V> nextItem : table[index])
        {
            if (nextItem.getKey().equals(key))
                return nextItem.getValue() ;
        }
        //assert: key is not in the table.
        return null ;
    }

    // adds the key and value pair to the table using hashing
    @Override
    public V put(K key, V value)
    {
        int index = key.hashCode() % table.length ;
        if (index < 0)
            index += table.length ;
        if (table[index] == null)
            table[index] = new LinkedList<>() ; //new one if it doesnt exist
        //search the LinkedList at table[index] to find the key
        for (Entry<K, V> nextItem : table[index])
        {
            if (nextItem.getKey().equals(key))  //if the key already exists
            {                                   //then update the key's value
                V oldValue = nextItem.getValue() ;
                return oldValue ;
            }
        }
        //new key is confirmed not already in the LinkedList, add a new Entry
        table[index].addFirst(new Entry<>(key, value)) ;
        numKeys++ ;
        if (numKeys > (LOAD_THRESHOLD * table.length)) //if Entry to table size ratio is too high
            rehash();                                   //rehash ~ make the table bigger
        return null ;                                    //all entries are reHashCoded and re-put
    }

    // remove an entry at the key location
    // return removed value
    @Override
    public V remove(Object key)
    {
//        if (table != null)
//            return null ;
        System.out.println("Here");
        int index = key.hashCode() % table.length ; //hash index
        if (index < 0)
            index += table.length ;
        if (table[index] == null)   //no entry where hashIndex would 'put' it
            return null ;
        for (Entry<K, V> nextItem : table[index]) //search all Entry's for the 'key' to remove
        {
            if (nextItem.getKey().equals(key))  //if found
            {
                Entry<K, V> output = new Entry<>(nextItem.getKey(), nextItem.getValue())  ;    //(return removed Value)
                System.out.println("Here is the value->" + output.getValue());
                table[index].remove(nextItem) ;   //remove Entry
                numKeys-- ; //decriment the counter of Entry's
                if (table[index].isEmpty()) //if only entry removed, remove empty LinkedList
                    table[index] = null ;
                return output.getValue() ; //return the value of removed Entry
            }
        }
        return null ; //not found
    }

    // returns number of keys
    @Override
    public int size()
    {
        return numKeys ;
    }

    // returns boolean if table has no keys
    @Override
    public boolean isEmpty()
    {

        return numKeys < 1 ;
    }

    // returns boolean if table has the searched for key
    @Override
    public boolean containsKey(Object key)
    {
        for (LinkedList<Entry<K, V>> tableEntry : table)
        {
            if (tableEntry != null)
                for (Entry e : tableEntry)
                {
                    if (e.getKey().equals(key))
                        return true ;
                }
        }
        return false ;
    }

    // returns boolean if table has the searched for value
    @Override
    public boolean containsValue(Object value)
    {
        for (LinkedList<Entry<K, V>> tableEntry : table)
        {
            if (tableEntry != null)
                for (Entry e : tableEntry)
                {
                    if (e.getValue().equals(value))
                        return true ;
                }
        }
        return false ;
    }




    /**
     * Resizes the table to be 2X +1 bigger than previous
     */
    private void rehash()
    {
        LinkedList<Entry<K, V>>[] oldTable = table ;
        table = new LinkedList[(table.length * 2) + 1] ;
        numKeys = 0 ;
        for (LinkedList<Entry<K, V>> tableEntry : oldTable)
        {
            if (tableEntry != null)
                for (Entry e : tableEntry)
                {
                    if (e != null)
                        put((K) e.getKey(), (V) e.getValue()) ;
                }
        }
    }
//To do this, you need to write a toString method for the
//table that captures the index of each table element that is not null and then the contents of
//that table element
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{") ;
        for (int i = table.length - 1 ; i >= 0 ; i-- ) {
            if (table[i] != null) {
                for (Entry<K, V> nextItem : table[i]) {
                    sb.append(nextItem.toString() + ", ") ;
                }
            }
        }
        sb.delete(sb.length()-2, sb.length()) ;
        return sb.toString() + "}"  ;

    }



    // throws UnsupportedOperationException
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException() ;
    }

    // empties the table
    @Override
    public void clear()
    {
        table = new LinkedList[CAPACITY] ;
        numKeys = 0 ;
    }

    // returns a view of the keys in set view
    @Override
    public Set<K> keySet()
    {
        Set<K> setOfKeys = new HashSet<K>(numKeys) ;

        for (LinkedList<Entry<K, V>> tableEntry : table)
        {
            if (tableEntry != null)
                for (Entry e : tableEntry)
                {
                    setOfKeys.add((K) e.getKey()) ;
                }
        }
        return  setOfKeys;
    }

    // throws UnsupportedOperationException
    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException() ;
    }


    // returns a set view of the hash table
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return new EntrySet() ;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashTableChain))
            return false ;
        HashTableChain<?, ?> that = (HashTableChain<?, ?>) o;
        return toString().equals(that.toString()) ;
        //return numKeys == that.numKeys && Arrays.equals(table, that.table);
    }

    @Override
    public int hashCode()
    {
        int hash = 0, count = 1 ;
        for (int i = 0 ; i < table.length ; i++)
            if (table[i] != null)
                for (Entry<K, V> entry : table[i])
                    for (int j = 0 ; j < entry.getKey().toString().length() ; j++)
                        hash += ((int) entry.getKey().toString().charAt(j)) * Math.pow(31, table.length - count++) ;

        return hash  ;

    }

    ///////////// ENTRY CLASS ///////////////////////////////////////

    /**
     * Contains key-value pairs for HashTable
     * @param <K> the key
     * @param <V> the value
     */
    private static class Entry<K, V> implements Map.Entry<K, V>{
        private K key ;
        private V value ;

        /**
         * Creates a new key-value pair
         * @param key the key
         * @param value the value
         */
        public Entry(K key, V value) {
            this.key = key ;
            this.value = value ;
        }

        /**
         * Returns the key
         * @return the key
         */
        public K getKey() {
            return  key;
        }

        /**
         * Returns the value
         * @return the value
         */
        public V getValue() {
            return value ;
        }

        /**
         * Sets the value
         * @param val the new value
         * @return the old value
         */
        public V setValue(V val) {
            V oldVal = value;
            value = val ;
            return oldVal ;
        }
        @Override
        public String toString() {
            return  key + "=" + value  ;
        }



    }

    ////////////// end Entry Class /////////////////////////////////

    ////////////// EntrySet Class //////////////////////////////////

    /**
     * Inner class to implement set view
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {


        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new SetIterator();
        }

        @Override
        public int size() {
            return numKeys ;
        }
    }

    ////////////// end EntrySet Class //////////////////////////////

    //////////////   SetIterator Class ////////////////////////////

    /**
     * Class that iterates over the table. Index is table location
     * and lastItemReturned is entry
     */
    private class SetIterator implements Iterator<Map.Entry<K, V>> {

        private int index = 0 ;
        private Entry<K,V> lastItemReturned = null;
        private Iterator<Entry<K, V>> iter = null;

        @Override
        public boolean hasNext()
        {

            if (iter != null && iter.hasNext()) {
                return true;
            }
            do {
                index++;
                if (index >= table.length) {
                    return false;
                }
            } while (table[index] == null);
            iter = table[index].iterator();
            return iter.hasNext();
        }

        @Override
        public Map.Entry<K, V> next()
        {
            if (iter.hasNext()) {

                lastItemReturned = iter.next();
                return lastItemReturned;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove()
        {
            if (lastItemReturned == null) {
                throw new IllegalStateException();
            } else {
                iter.remove();
                lastItemReturned = null;
            }
        }
    }

    ////////////// end SetIterator Class ////////////////////////////
}