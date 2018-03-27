package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class UserRoleAssocSeq extends PgSequenceGenerator {

    public UserRoleAssocSeq() {
        this.setSequence("he_adm_user_role_assoc_id_seq");
    }
}
