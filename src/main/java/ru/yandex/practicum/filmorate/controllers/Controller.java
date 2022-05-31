package ru.yandex.practicum.filmorate.controllers;

import java.util.*;

public abstract class Controller<T> {

    protected Map<Long,T> data = new HashMap<>();

    abstract public T add(T t) throws Exception;

    abstract public T update(T t) throws Exception;

    abstract public List<T> get() throws Exception;

    protected void validate(T t) throws Exception {}
}
