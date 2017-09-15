package net.querz.event;

import java.util.*;
import java.util.function.Consumer;

class EventTree {

	private TreeElement root;

	EventTree() {
		root = new TreeElement(Event.class);
	}

	<T extends Event> EventFunction<T> add(Consumer<T> function, Class<T> eventClass, int priority) {
		EventTree.TreeElement closest = getClosest(eventClass);
		EventTree.TreeElement elem;

		if (closest.children != null && closest.children.containsKey(eventClass)) {
			elem = closest.children.get(eventClass);
		} else {
			elem = new EventTree.TreeElement(eventClass);
		}

		EventFunction<T> fData = new EventFunction<>(function, eventClass, priority);
		elem.functions.add(fData);
		Collections.sort(elem.functions);

		Class<?> clazz = eventClass;
		while ((clazz = clazz.getSuperclass()) != Event.class && clazz != closest.eventClass) {
			EventTree.TreeElement parent = new EventTree.TreeElement(clazz);
			parent.addChild(elem);
			elem = elem.parent = parent;
		}
		elem.parent = closest;
		closest.addChild(elem);
		return fData;
	}

	<T extends Event> void remove(EventFunction<T> functionData) {
		EventTree.TreeElement elem = getClosest(functionData.eventClass);
		if (elem.children.get(functionData.eventClass) != null) {
			elem.children.get(functionData.eventClass).removeFunction(functionData);
		}
	}

	private <T extends Event> TreeElement getClosest(Class<T> eventClass) {
		Stack<Class<?>> branch = new Stack<>();
		Class<?> current = eventClass;

		while ((current = current.getSuperclass()) != Event.class) {
			branch.push(current);
		}

		TreeElement elem = root;

		while (!branch.isEmpty()) {
			if (elem.children != null && elem.children.containsKey(branch.peek())) {
				elem = elem.children.get(branch.pop());
			} else {
				break;
			}
		}
		return elem;
	}

	void execute(Event event) {
		TreeElement elem = getClosest(event.getClass());
		if (elem.children != null && elem.children.containsKey(event.getClass())) {
			elem.children.get(event.getClass()).execute(event);
		}
	}

	@Override
	public String toString() {
		return root.toString();
	}

	class TreeElement {
		TreeElement parent;
		Map<Class<?>, TreeElement> children;
		Class<?> eventClass;
		List<EventFunction<?>> functions = new ArrayList<>();

		TreeElement(Class<?> eventClass) {
			this.eventClass = eventClass;
		}

		void removeFunction(EventFunction function) {
			functions.removeIf(r -> r.equals(function));
		}

		void addChild(TreeElement elem) {
			if (children == null) {
				children = new HashMap<>();
			}
			children.put(elem.eventClass, elem);
		}

		void execute(Event event) {
			functions.forEach(function -> executeEventFunction(function.function, event));
			if (parent != null) {
				parent.execute(event);
			}
		}

		@SuppressWarnings("unchecked")
		<T extends Event> void executeEventFunction(Consumer<T> function, Event event) {
			function.accept((T) event);
		}

		@Override
		public int hashCode() {
			return eventClass.hashCode();
		}

		@Override
		public String toString() {
			return toString(0);
		}

		String toString(int depth) {
			StringBuilder d = new StringBuilder(depth);

			for (int i = 0; i < depth; i++) {
				d.append(" ");
			}

			StringBuilder out = new StringBuilder(
					d + "parent=" + (parent == null ? "null" : parent.eventClass.getSimpleName())
					+ "\n" + d + "eventClass=" + eventClass.getSimpleName()
					+ "\n" + d + "functions=" + Arrays.toString(functions.toArray())
					+ "\n" + d + "children:");
			if (children == null) {
				out.append("null\n");
			} else {
				out.append("\n");
				for (Map.Entry<Class<?>, TreeElement> entry : children.entrySet()) {
					out.append(entry.getValue().toString(depth + 1));
				}
			}
			return out.toString();
		}
	}
}
