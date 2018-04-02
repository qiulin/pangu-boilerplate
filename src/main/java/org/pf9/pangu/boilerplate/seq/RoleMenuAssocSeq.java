package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class RoleMenuAssocSeq extends PgSequenceGenerator {

    public RoleMenuAssocSeq() {
        this.setSequence("pg_adm_role_menu_assoc_id_seq");
    }
}
