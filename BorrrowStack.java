public class BorrowStack<T> {
    private Node<T> top;
    private int size;

    // Constructor
    public BorrowStack() {
        this.top = null;
        this.size = 0;
    }

    // PUSH: Adds an item to the top of the stack - O(1)
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.setNext(top);
        top = newNode;
        size++;
    }

    // POP: Removes and returns the top item - O(1)
    public T pop() {
        if (isEmpty()) {
            System.out.println("No books to return. The stack is empty.");
            return null;
        }
        T poppedData = top.getData();
        top = top.getNext();
        size--;
        return poppedData;
    }

    // PEEK: Looks at the top item without removing it - O(1)
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return top.getData();
    }

    // ISEMPTY: Checks if the stack has items - O(1)
    public boolean isEmpty() {
        return top == null;
    }

    // GETSIZE: Returns the current number of items - O(1)
    public int getSize() {
        return size;
    }
}
