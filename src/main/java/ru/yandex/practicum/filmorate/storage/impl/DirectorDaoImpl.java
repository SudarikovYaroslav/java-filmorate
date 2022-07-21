package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class DirectorDaoImpl implements DirectorDao {

    public static final String DIRECTOR_ID_COLUMN = "director_id";
    public static final String DIRECTOR_NAME_COLUMN = "director_name";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "select * from DIRECTORS";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Optional<Director> findDirectorById(long directorId) {
        String sqlQuery = "select * from DIRECTORS where director_id = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::makeDirector, directorId);
        if (directors.isEmpty()) return Optional.empty();
        return Optional.of(directors.get(0));
    }

    @Override
    public Director save(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("director_id");
        long id = simpleJdbcInsert.executeAndReturnKey(directorToMap(director)).longValue();
        director.setId(id);
        log.debug("Сохранён режиссёр id: " + director.getId());
        return director;
    }

    private Map<String, Object> directorToMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("director_name", director.getName());
        return values;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "update DIRECTORS set director_name = ? where director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        log.debug("Обновлён режиссёр id: " + director.getId());
        return director;
    }

    @Override
    public void delete(long directorId) {
        String sqlQuery = "delete from DIRECTORS where director_id = ?";
        jdbcTemplate.update(sqlQuery, directorId);
        log.debug("удалён режиссёр id: " + directorId);
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong(DIRECTOR_ID_COLUMN))
                .name(rs.getString(DIRECTOR_NAME_COLUMN))
                .build();
    }
}