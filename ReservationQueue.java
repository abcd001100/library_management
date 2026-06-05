public class ReservationQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    // Constructor
    public ReservationQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    // ENQUEUE: Adds an item to the back of the line - O(1)
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        
        // If the queue is empty, the new node is both the front and the rear
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            // Otherwise, attach it behind the current rear, then update the rear pointer
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }

    // DEQUEUE: Removes and returns the item at the front of the line - O(1)
    public T dequeue() {
        if (isEmpty()) {
            System.out.println("The reservation queue is empty.");
            return null;
        }
        
        // Grab the data from the front node before removing it
        T dequeuedData = front.getData();
        
        // Shift the front pointer to the next node in line
        front = front.getNext();
        
        // If the queue is now empty after removing that item, update the rear to null as well
        if (front == null) {
            rear = null;
        }
        
        size--;
        return dequeuedData;
    }

    // PEEK: Looks at the front item without removing it - O(1)
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return front.getData();
    }

    // ISEMPTY: Checks if the queue has items - O(1)
    public boolean isEmpty() {
        return front == null;
    }

    // GETSIZE: Returns the current number of items - O(1)
    public int getSize() {
        return size;
    }
}
