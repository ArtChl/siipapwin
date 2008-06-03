/*
 *     file: WeakHashSet.java
 *  package: oreilly.hcj.references
 *
 * This software is granted under the terms of the Common Public License,
 * CPL, which may be found at the following URL:
 * http://www-124.ibm.com/developerworks/oss/CPLv1.0.htm
 *
 * Copyright(c) 2003-2005 by the authors indicated in the @author tags.
 * All Rights are Reserved by the various authors.
 *
########## DO NOT EDIT ABOVE THIS LINE ########## */

package com.luxsoft.utils.domain;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**  
 * Implements a HashSet where the objects given are stored in weak references.
 * 
 * <p>
 * Uses the WeakHashMap class as a backing store to implement a set of objects that are
 * stored as weak references. All information concerning using keys in the WeakHashMap
 * class pertain to this class and it is reccomended that the user of this class review
 * that material before using the class.
 * </p>
 * 
 * <p>
 * Because this set contains only weak references, it is not serializable. If one tried
 * to serialize a weak reference, the results would be highly unpredictable as the
 * object could likely vanish from memory before the proces was even completed. Users of
 * this class must use transient when the containing class uses this set.
 * </p>
 * 
 * <p>
 * Because of the semantics of the weak references, the value null is not allowed in this
 * set.
 * </p>
 * 
 * <p>
 * This collection is not identity based but equality based. This can cause some
 * confusion as you cannot put in two objects whose <tt>equals()</tt> methods return
 * true. It also means that an object being held is not necessarily the same one that
 * the user is holding. For example, you could have a String with the value 'fred' at
 * memory location X and ther could be another String with the value 'fred' at memory
 * location Y. The first instance is in the set but the second isn't.
 * </p>
 *
 * @author <a href="mailto:worderisor@yahoo.com">Robert Simmons jr.</a>
 * @version $Revision: 1.1 $
 *
 * @see java.lang.util.WeakHashMap
 * @see java.lang.ref.WeakReference
 */
public class WeakHashSet extends AbstractSet implements Set {
	/** Dummy value used as a value object. */
	private static final Object DUMMY = new String("DUMMY");  //$NON-NLS-1$

	/** Holds the backing store. */
	WeakHashMap backingStore = new WeakHashMap();

	/** 
	 * Constructs a new empty WeakHashSet with default values passed the the backing
	 * store.
	 *
	 * @see java.util.WeakHashMap#WeakHashMap()
	 */
	public WeakHashSet() {
		backingStore = new WeakHashMap();
	}

	/** 
	 * Constructs a new WeakHashSet with default values passed the the backing store and
	 * fills it with the given collection. Note that duplicates in the collection will
	 * merely be overwritten
	 *
	 * @see java.util.WeakHashMap#WeakHashMap(Collection)
	 */
	public WeakHashSet(final Collection c) {
		backingStore = new WeakHashMap(Math.max((int)(c.size() / .75f) + 1, 16));
		addAll(c);
	}

	/** 
	 * Constructs a new WeakHashSet with the values given passed the the backing store.
	 *
	 * @see java.util.WeakHashMap#WeakHashMap(int, float)
	 */
	public WeakHashSet(final int initialCapacity, final float loadFactor) {
		backingStore = new WeakHashMap(initialCapacity, loadFactor);
	}

	/** 
	 * Constructs a new WeakHashSet with the values given passed the the backing store.
	 *
	 * @see java.util.WeakHashMap#WeakHashMap(int)
	 */
	public WeakHashSet(final int initialCapacity) {
		backingStore = new WeakHashMap(initialCapacity);
	}

	/** 
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return backingStore.keySet()
		                   .isEmpty();
	}

	/** 
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException If the user tries to add null to the set.
	 */
	@SuppressWarnings("unchecked")
	public boolean add(final Object o) {
		if (o == null) {
			throw new NullPointerException();
		}

		return backingStore.put(o, DUMMY) == null;
	}

	/** 
	 * {@inheritDoc}
	 *
	 * @see #add(Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean addAll(final Collection c) {
		boolean changed = false;
		Iterator iter = c.iterator();

		while (iter.hasNext()) {
			changed = (changed | (backingStore.put(iter.next(), DUMMY) != DUMMY));
		}

		return changed;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void clear() {
		backingStore.clear();
	}

	/** 
	 * {@inheritDoc}
	 */
	public boolean contains(final Object o) {
		return backingStore.containsKey(o);
	}

	/** 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean containsAll(final Collection c) {
		return backingStore.keySet()
		                   .containsAll(c);
	}

	/** 
	 * {@inheritDoc}
	 */
	public boolean equals(final Object o) {
		return backingStore.equals(o);
	}

	/** 
	 * Returns the hash code value for this set.
	 * 
	 * <p>
	 * Gives back the hashCode for the backing store key set. The user should be aware,
	 * however, that this hash code can change without user intervention as the objects
	 * in the collection can easily be collected microseconds after completetion of the
	 * method. It is not reccomended that the user rely on this hash code for
	 * consistency
	 * </p>
	 *
	 * @return The hashcode for this object.
	 */
	public int hashCode() {
		return backingStore.keySet()
		                   .hashCode();
	}

	/** 
	 * Returns an iterator over the elements contained in this collection.
	 * 
	 * <p>
	 * Note that this iterator is extremely volatile because the user may iterate over an
	 * element in the set and find seconds later that it has been removed. This is
	 * because of the semantics of weak references which act like a second thread is
	 * silently modifying the collection. For this reason, it is advisable that if the
	 * user wants to do something with the set that they maintain a strong reference to
	 * the object and not rely on it being in the collection for them.
	 * </p>
	 * 
	 * <p>
	 * This iterator is fail fast and WeakReference transparrent. By this we mean that
	 * the iterator simply ignores objects pending in the reference queue for cleanup.
	 * </p>
	 *
	 * @return The iterator.
	 */
	public Iterator iterator() {
		return backingStore.keySet()
		                   .iterator();
	}

	/** 
	 * {@inheritDoc}
	 */
	public boolean remove(final Object o) {
		return backingStore.keySet()
		                   .remove(o);
	}

	/** 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean removeAll(final Collection c) {
		return backingStore.keySet()
		                   .removeAll(c);
	}

	/** 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean retainAll(final Collection c) {
		return backingStore.keySet()
		                   .retainAll(c);
	}

	/** 
	 * {@inheritDoc}
	 */
	public int size() {
		return backingStore.keySet()
		                   .size();
	}

	/** 
	 * {@inheritDoc}
	 */
	public Object[] toArray() {
		return backingStore.keySet()
		                   .toArray();
	}

	/** 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object[] toArray(final Object[] a) {
		return backingStore.keySet()
		                   .toArray(a);
	}

	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return backingStore.keySet()
		                   .toString();
	}

	/** 
	 * {@inheritDoc}
	 */
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}

/* ########## End of File ########## */
