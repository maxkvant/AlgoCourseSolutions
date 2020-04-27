import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int size;
    private Item[] items;

    // construct an empty randomized queue
    public RandomizedQueue() {
        size = 0;
        int capacity = 1;
        items = (Item []) new Object[capacity];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item must bet not null");
        }

        if (size >= items.length) {
            items = copyItems(items.length * 2);
        }

        items[size] = item;
        size += 1;

        final int index = StdRandom.uniform(size);
        swap(index, size - 1);
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
        final Item item = items[size - 1];
        size -= 1;
        items[size] = null;

        if (size > 0 && size <= items.length / 4) {
            items = copyItems(items.length / 2);
        }

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
        final int index = StdRandom.uniform(size);
        return items[index];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        final Item[] copy = copyItems(size);
        return new RandomizedIterator(copy);
    }

    // unit testing (required)
    public static void main(String[] args) {
        final RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        StdOut.println(queue.isEmpty() + " " + queue.size());
        for (int i = 1; i <= 5; i += 1) {
            queue.enqueue(i);
        }
        StdOut.println(queue.isEmpty() + " " + queue.size());

        for (int i = 0; i < 10; i += 1) {
            StdOut.print(queue.sample() + " ");
        }
        StdOut.println();
        StdOut.println();
        StdOut.println(queue.iterator());
        for (Integer item: queue) {
            StdOut.print(item + " ");
        }
        StdOut.println();
        for (Integer item: queue) {
            StdOut.print(item + " ");
        }
        StdOut.println();

        for (int i = 0; i < 5; i += 1) {
            StdOut.print(queue.dequeue() + " ");
        }
        StdOut.println();
        StdOut.println(queue.isEmpty() + " " + queue.size());
    }

    private Item[] copyItems(int capacity) {
        assert capacity >= size;

        final Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = items[i];
        }
        return copy;
    }

    private void swap(int i, int j) {
        final Item tmp = items[i];
        items[i] = items[j];
        items[j] = tmp;
    }

    private class RandomizedIterator implements Iterator<Item> {
        private final Item[] items;
        int index;

        public RandomizedIterator(Item[] items) {
            this.items = items;
            StdRandom.shuffle(items);
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < items.length;
        }

        @Override
        public Item next() {
            if (index >= items.length) {
                throw new NoSuchElementException("there is no next");
            }

            final Item item = items[index];
            index += 1;
            return item;
        }
    }
}