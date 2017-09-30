package db;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private int front;

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextFirst = 4;
        front = 4;
        nextLast = nextFirst + 1;
    }

    /** Resizes the underlying array to the target capacity. */

    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        int newFirstIndex = capacity / 2;
        int currFirstIndex = front;
        if (nextFirst > items.length / 2 || front > items.length / 2) {
            System.arraycopy(items, currFirstIndex, a, newFirstIndex, size - currFirstIndex);
            System.arraycopy(items, 0, a, newFirstIndex + size - currFirstIndex,  front);
            nextFirst = (capacity / 2) - 1;
            front = nextFirst + 1;
            nextLast = 0;
            items = a;
        } else if (nextLast <= items.length / 2) {
            System.arraycopy(items, front, a, newFirstIndex, size - currFirstIndex);
            System.arraycopy(items, 0, a, newFirstIndex + size - currFirstIndex, front);
            nextFirst = (capacity / 2) - 1;
            front = nextFirst + 1;
            nextLast = 0;
            items = a;
        }
    }

    private void downsize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        int newFirstIndex = capacity / 2;
        int currFirstIndex = front;
        if (nextFirst > nextLast) {
            System.arraycopy(items, front, a, newFirstIndex, items.length - front);
            System.arraycopy(items, 0, a, newFirstIndex + items.length - front, nextLast);
            nextFirst = (capacity / 2) - 1;
            front = nextFirst + 1;
            nextLast = (a.length - 1) - nextLast;
            items = a;
        } else {
            System.arraycopy(items, front, a, newFirstIndex, size);
            nextFirst = (capacity / 2) - 1;
            front = nextFirst + 1;
            nextLast = front + size;
            items = a;
        }
    }



    public void addFirst(Item x) {

        if (size == items.length) {
            resize(size * 2);
        }


        items[nextFirst] = x;
        front = nextFirst;
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst = nextFirst - 1;
        }
        size = size + 1;
    }

    /** Inserts X into the back of the list. */
    public void addLast(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }

        int target = nextLast;
        items[target] = x;
        if (nextLast == items.length - 1) {
            nextLast = 0;
        } else {
            nextLast = nextLast + 1;
        }
        if (size == 0) {
            front = target;
        }
        size = size + 1;
    }


    private Item getFirst() {
        if (nextFirst == items.length - 1) {
            return items[0];
        } else {
            return items[nextFirst + 1];
        }
    }

    /** Returns the item from the back of the list. */
    private Item getLast() {
        if (nextLast == 0) {
            return items[items.length - 1];
        } else {
            return items[nextLast - 1];
        }
    }
    /** Gets the ith item in the list (0 is the front). */
    public Item get(int index) {
        if (index > size - 1) {
            return null;
        } else if (index == 0) {
            return items[front];
        }
        int i = (front + index) % items.length;
        return items[i];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    public Item removeFirst() {

        Item first = get(0);
        if (nextFirst == items.length - 1) {
            nextFirst = 0;
        } else {
            nextFirst = nextFirst + 1;
        }
        if (front == items.length - 1) {
            front = 0;
        } else {
            front = front + 1;
        }
        size = size - 1;


        if ((double) size / items.length < 0.25 && items.length > 20) {
            downsize(items.length / 2);
        }

      
        return first;
    }

    /** Deletes item from back of the list and
      * returns deleted item. */
    public Item removeLast() {
        Item last = get(size - 1);
        if (nextLast == 0) {
            nextLast = items.length - 1;
        } else {
            nextLast = nextLast - 1;
        }
        size = size - 1;


        if ((double) size / items.length < 0.25 && items.length > 20) {
            downsize(items.length / 2);
        }


        return last;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
    }
}
