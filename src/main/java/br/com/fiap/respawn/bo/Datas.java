package br.com.fiap.respawn.bo;

import java.time.LocalDate;
import java.time.ZoneId;

// o servidor do Render roda em UTC, entao fixamos o fuso de SP
final class Datas {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private Datas() {
    }

    static LocalDate hoje() {
        return LocalDate.now(SAO_PAULO);
    }
}
