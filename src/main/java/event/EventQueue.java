package event;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class EventQueue {
    private Queue<Event> queue = new PriorityQueue<Event>(11, new MyComparator());

    public int size() {
        return this.queue.size();
    }

    public void schedule(Event event) {
        this.queue.add(event);
    }

    public Event pop() {
        Event event = getTop();
        return event;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public Event getTop() {
        if (isEmpty()) {
            throw new IllegalArgumentException("queue is empty");
        }
        return this.queue.poll();
    }
}

class MyComparator implements Comparator <Event> {
    @Override
    public int compare(Event event1, Event event2) {
        long x = (long) event1.getTime();
        long y = (long) event2.getTime();

        if (x > y) {
            return 1;
        } else if (x < y) {
            return -1;
        } else {
            return 0;
        }
    }
}
