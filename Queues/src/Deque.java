import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private final Node<Item> fakeNode;
    private int size;

    // construct an empty deque
    public Deque() {
        fakeNode = new Node<>(null);
        fakeNode.next = fakeNode;
        fakeNode.prev = fakeNode;
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        validateItem(item);
        Node.insertBetween(item, fakeNode, fakeNode.next);
        size += 1;
    }

    // add the item to the back
    public void addLast(Item item) {
        validateItem(item);
        Node.insertBetween(item, fakeNode.prev, fakeNode);
        size += 1;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
        final Item item = Node.remove(fakeNode.next);
        size -= 1;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
        final Item item = Node.remove(fakeNode.prev);
        size -= 1;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator(fakeNode.next);
    }

    // unit testing (required)
    public static void main(String[] args) {
        final Deque<Integer> deque = new Deque<>();
        StdOut.println(deque.isEmpty());

        deque.addFirst(3);
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addLast(4);
        deque.addLast(5);
        StdOut.println(deque.isEmpty() + " " + deque.size());

        StdOut.println(deque.iterator());
        for (Integer item: deque) {
            StdOut.print(item + " ");
        }
        StdOut.println();

        deque.removeFirst();
        deque.removeLast();

        for (Integer item: deque) {
            StdOut.print(item + " ");
        }
        StdOut.println();

        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeFirst());
        StdOut.println(deque.isEmpty());
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item must be not null");
        }
    }

    private static class Node<Item> {
        private Node<Item> next;
        private final Item item;
        private Node<Item> prev;

        public Node(Item item) {
            this.item = item;
        }

        public Node(Item item, Node<Item> prev, Node<Item> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }

        public static <Item> void insertBetween(Item item, Node<Item> prev, Node<Item> next) {
            final Node<Item> node = new Node<>(item, prev, next);
            prev.next = node;
            next.prev = node;
        }

        public static <Item> Item remove(Node<Item> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;

            node.next = null;
            node.prev = null;
            return node.item;
        }
    }

    private class DequeIterator implements Iterator<Item> {
        private Node<Item> node;

        public DequeIterator(Node<Item> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != fakeNode;
        }

        @Override
        public Item next() {
            if (node == fakeNode) {
                throw new NoSuchElementException("there is no next");
            }

            final Item item = node.item;
            node = node.next;
            return item;
        }
    }
}