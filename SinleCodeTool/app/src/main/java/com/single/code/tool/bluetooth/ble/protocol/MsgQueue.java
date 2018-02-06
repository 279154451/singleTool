package com.single.code.tool.bluetooth.ble.protocol;

/**
 * Created by andy on 2016/1/19.
 * 链表实现的队列
 */
public final class MsgQueue<T> {
    private Node first;
    private Node last;
    private int length;

    public boolean isEmpty() {
        return length == 0;
    }

    public int size() {
        return length;
    }

    public  void enQueue(T item) {
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) {
            first = last;
        } else {
            oldLast.next = last;
        }
        length++;

    }

    public  T deQueue() {
        if (isEmpty()) {
            last = null;
            return null;
        }
        T values = first.item;
        first = first.next;
        length--;
        return values;
    }

    private class Node {
        T item;
        Node next;
    }
}
