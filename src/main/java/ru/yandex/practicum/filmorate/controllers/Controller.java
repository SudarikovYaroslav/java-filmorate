package ru.yandex.practicum.filmorate.controllers;

import java.util.*;

public abstract class Controller<T> {

    protected Map<Long,T> data = new HashMap<>();

    abstract public void add(T t) throws Exception;

    abstract public void update(T t) throws Exception;

    abstract public Collection<T> get() throws Exception;

    protected void validate(T t) throws Exception {
    }

    ;
}
