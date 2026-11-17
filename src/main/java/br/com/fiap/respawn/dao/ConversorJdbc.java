package br.com.fiap.respawn.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/** Conversoes entre os tipos do Oracle (DATE, CHAR 'S'/'N', NUMBER nulo) e os tipos Java. */
final class ConversorJdbc {

    private ConversorJdbc() {
    }

    static LocalDate data(ResultSet rs, String coluna) throws SQLException {
        Date d = rs.getDate(coluna);
        return d == null ? null : d.toLocalDate();
    }

    static Date data(LocalDate data) {
        return data == null ? null : Date.valueOf(data);
    }

    static boolean simNao(ResultSet rs, String coluna) throws SQLException {
        return "S".equals(rs.getString(coluna));
    }

    static String simNao(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "S" : "N";
    }

    static Long longOuNulo(ResultSet rs, String coluna) throws SQLException {
        long valor = rs.getLong(coluna);
        return rs.wasNull() ? null : valor;
    }
}
