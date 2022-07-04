package com.emamagic.safe.util

import android.util.Log

private const val CAPACITY = 10
private const val TAG = "LRUCache"

// TODO It is better to implement thread-safe
class LRUCache constructor(
    private val capacity: Int? = CAPACITY
) {
    // for printing the doubleLinkedList
    private val builder = StringBuilder()

    private val map = hashMapOf<String, Node>()
    private var head = Node()
    private var tail = Node()

    data class Node(
        var key: String?= null,
        var prev: Node? = null,
        var next: Node? = null,
        var value: SafeWrapper<*>? = null,
        var isExpire: Boolean? = null
    )

    private fun delete(node: Node) {
        if (node.prev != null) {
            node.prev!!.next = node.next
        } else {
            head = node.next!!
        }
        if (node.next != null) {
            node.next!!.prev = node.prev
        } else {
            tail = node.prev!!
        }
    }

    private fun updateHead(node: Node) {
        node.next = head
        node.prev = null
        head.prev = node
        head = node
        tail = head
    }

    fun put(key: String, value: SafeWrapper<*>, isExpire: Boolean? = false) {
        if (map.containsKey(key)) {
            val old: Node = map[key]!!
            old.isExpire = isExpire
            old.value = value
            delete(old)
            updateHead(old)
        } else {
            val newNode = Node(key = key, value = value, isExpire = isExpire)
            if (map.size >= capacity!!) {
                map.remove(tail.key)
                // remove last node
                delete(tail)
                updateHead(newNode)
            } else {
                updateHead(newNode)
            }
            map[key] = newNode
        }
    }

    operator fun get(key: String): SafeWrapper<*>? {
        if (map.containsKey(key)) {
            val n: Node = map[key]!!
            delete(n)
            if (!n.isExpire!!) {
                updateHead(n)
                return n.value
            }
            map.remove(key)
        }
        return null
    }


    fun print() {
        val currentValue: SafeWrapper<*>? = head.value
        if (currentValue == null) {
            builder.append("[]")
        } else {
            builder.append('[')
            var currentNode: Node? = head
            while (currentNode?.value != null) {
                builder.append(currentNode.value).append(", ")
                currentNode = currentNode.next
            }
            builder.delete(builder.length - 2, builder.length)
            builder.append(']')
        }
        Log.e(TAG, "linked list items -> $builder")
    }

}