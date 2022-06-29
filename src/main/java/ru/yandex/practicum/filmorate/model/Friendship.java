package ru.yandex.practicum.filmorate.model;

public class Friendship {
    private boolean unconfirmed; //пользователь отправил запрос на добавление другого пользователя в друзья
    private boolean confirmed; // второй пользователь согласился на добавление.

    public boolean isConfirmed() {
        return confirmed;
    }

    public Friendship setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    public boolean isUnconfirmed() {
        return unconfirmed;
    }

    public Friendship setUnconfirmed(boolean unconfirmed) {
        this.unconfirmed = unconfirmed;
        return this;
    }
}
