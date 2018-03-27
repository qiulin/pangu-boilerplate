package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class RoleAuthorityAssocSeq extends PgSequenceGenerator {

    public RoleAuthorityAssocSeq() {
        this.setSequence("he_adm_role_authority_assoc_seq");
    }
}
