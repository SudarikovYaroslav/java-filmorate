package ru.yandex.practicum.filmorate.controllers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class Controller<T> {

    protected Set<T> data = new HashSet<>();

    abstract public void add(T t) throws Exception;

    abstract public void update(T t) throws Exception;

    abstract public Collection<T> get() throws Exception;

    protected void validate(T t) throws Exception {};
}
