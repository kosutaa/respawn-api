package br.com.fiap.respawn.bo;

import java.time.LocalDate;
import java.time.ZoneId;

/** Data de "hoje" sempre no fuso de Sao Paulo (o servidor do Render roda em UTC). */
final class Datas {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private Datas() {
    }

    static LocalDate hoje() {
        return LocalDate.now(SAO_PAULO);
    }
}
