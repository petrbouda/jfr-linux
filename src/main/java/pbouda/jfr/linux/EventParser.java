package pbouda.jfr.linux;

import jdk.jfr.Event;

/**
 * A parser that is able to a string content and creates a new JFR event.
 *
 * @param <T> extends {@link Event}
 */
public interface EventParser<T extends Event> {

    T create(String content);

}
