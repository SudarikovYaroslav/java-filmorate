package ru.yandex.practicum.filmorate.controllers;

import java.util.Collection;

public abstract class Controller<T> {
    abstract public void add(T t) throws Exception;

    abstract public void update(T t) throws Exception;

    abstract public Collection<T> get() throws Exception;

    private void validate(T t){};
}
