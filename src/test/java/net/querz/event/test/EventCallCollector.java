package net.querz.event.test;

import static junit.framework.TestCase.*;
import net.querz.event.Event;

import java.util.*;

public class EventCallCollector {

	private static LinkedHashMap<UUID, LinkedHashMap<String, Event>> map = new LinkedHashMap<>();

	public static void assertEventExists(UUID id, String name, Event event) {
		assertTrue(map.containsKey(id));
		assertTrue(map.get(id).containsKey(name));
		assertTrue(map.get(id).get(name) == event);
	}

	public static void assertEventDoesNotExist(UUID id, String name) {
		if (map.containsKey(id)) {
			assertFalse(map.get(id).containsKey(name));
		}
	}

	public static void add(UUID id, String name, Event event) {
		if (map.containsKey(id)) {
			assertFalse(map.get(id).containsKey(name));
			map.get(id).put(name, event);
		} else {
			LinkedHashMap<String, Event> s = new LinkedHashMap<>();
			s.put(name, event);
			map.put(id, s);
		}
	}

	public static void assertCallOrder(UUID id, String first, String second, Event event) {
		assertEventExists(id, first, event);
		assertEventExists(id, second, event);
		assertTrue(indexOf(map.get(id), first) < indexOf(map.get(id), second));
	}

	private static int indexOf(LinkedHashMap<String, Event> m, String key) {
		int i = 0;
		for (Map.Entry<String, Event> entry : m.entrySet()) {
			if (entry.getKey().equals(key)) {
				return i;
			}
			i++;
		}
		fail("Key does not exist in map.");
		return -1;
	}

	public static void clear() {
		map.clear();
	}
}
