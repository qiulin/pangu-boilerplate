package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class RoleSeq extends PgSequenceGenerator {

    public RoleSeq() {
        this.setSequence("he_adm_role_id_seq");
    }
}
